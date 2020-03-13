package com.example.stridon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.ReceiverCallNotAllowedException;
import android.database.Cursor;
import android.provider.CalendarContract;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class BuildModelService extends JobIntentService {

    private static final String TAG = "Build Model Service";
    private static final int JOB_ID = 1;

    private AlarmManager alarmManager;

    private RequestQueue mQueue;

    private static final int MY_CAL_REQ = 1;
    Button button;

    public static final String[] INSTANCE_PROJECTION = new String[]{
            CalendarContract.Instances.TITLE,      // 0
            CalendarContract.Instances.BEGIN,         // 1
            CalendarContract.Instances.END        // 2
    };

    // The indices for the projection array above.
    private static final int PROJECTION_TITLE_INDEX = 0;
    private static final int PROJECTION_BEGIN_INDEX = 1;
    private static final int PROJECTION_END_INDEX = 2;

    private ArrayList<Long> badWeatherTimes;

    public BuildModelService() {
        super();
        badWeatherTimes = new ArrayList<Long>();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "my service is running " + Calendar.getInstance().getTime().getTime());
        mQueue = Volley.newRequestQueue(this);

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        // change this to actual intervals
        getWeather();

//        ArrayList<ArrayList<Long>> intervals = new ArrayList<>();
//        for (int i = 0; i < 1; i++) {
//            ArrayList<Long> in = new ArrayList<>();
//            in.add(Calendar.getInstance().getTimeInMillis() + 3000);
//            intervals.add(in);
//        }
//        setAlarms(intervals);
    }

    public static void enqueueWork(Context ctx, Intent intent) {
        enqueueWork(ctx, BuildModelService.class, JOB_ID, intent);
    }

    /*
        returns list of valid calendar interval start time

        BE SURE TO GET CALENDAR PERMISSIONS
     */
    public ArrayList<ArrayList<Long>> getCalendarTimes() {
        //get the start of the next day
        ArrayList<ArrayList<Long>> build = new ArrayList<ArrayList<Long>>();

        Calendar cal = Calendar.getInstance();

        // debug
        //        cal.add(Calendar.MINUTE, 1);

        cal.add(Calendar.DATE, 1);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        long startMillis = cal.getTimeInMillis();
        cal.add(Calendar.DATE, 1);
        long endMillis = cal.getTimeInMillis();

        ContentResolver cr = getContentResolver();
        Cursor cur = CalendarContract.Instances.query(cr, INSTANCE_PROJECTION, startMillis, endMillis);

        if (cur.getCount() == 0) {
            ArrayList<Long> interval = new ArrayList<Long>();
            interval.add(startMillis);
            interval.add(endMillis);
            build.add(interval);
            return build;
        } else {
            ArrayList<Long> interval = new ArrayList<Long>();
            interval.add(startMillis);
            build.add(interval);
        }

        while (cur.moveToNext()) {
            if (cur != null) {
                String title = null;
                long beginVal = 0;
                long endVal = 0;

                // Get the field values
                beginVal = cur.getLong(PROJECTION_BEGIN_INDEX);
                endVal = cur.getLong(PROJECTION_END_INDEX);

                build.get(build.size() - 1).add(beginVal);
                ArrayList<Long> interval = new ArrayList<Long>();
                interval.add(endVal);
                build.add(interval);
            } else {
                System.out.println("Event not found");
            }
        }
        build.get(build.size() - 1).add(endMillis);

        //now filter through bad times and remove ones that are not good
        for (int i = 0; i < build.size(); i++) {
            if ((build.get(i).get(1) - build.get(i).get(0)) / 60000L < 30) {
                build.remove(i);
                i--;
            }
        }

        return build;
    }

    /*
        fills out the list with bad weather start times in the new few hours
     */
    public void getWeather() {

        String url = "https://api.openweathermap.org/data/2.5/forecast?q=irvine&appid=4843f8fbd4876cc07f77a0730a5302b1";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONArray list = response.getJSONArray("list");
                    for (int i = 0; i < 8; i++) {
                        JSONObject time = list.getJSONObject(i);
                        JSONObject main = time.getJSONObject("main");
                        String temp = main.getString("temp");
                        double x = Double.parseDouble(temp);
                        double t = (x * 9 / 5) - 459.67;

                        JSONArray weather = time.getJSONArray("weather");
                        String description = weather.getJSONObject(0).getString("main");

                        if (description.equals("Rain") || t >= 90.0) {
                            String dateString = time.getString("dt_txt");
                            try {
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Calendar cal = Calendar.getInstance();
                                cal.setTime(sdf.parse(dateString));
                                badWeatherTimes.add(cal.getTimeInMillis());
                            } catch (ParseException e) {
                                System.out.println("Can't find the date time");
                            }
                        }
                    }
                    getIntervals();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR");
            }
        });
        mQueue.add(jsonObjectRequest);
    }

    /*
        returns a list of good weather times
     */
    public ArrayList<ArrayList<Long>> getWeatherTimes() {
        ArrayList<ArrayList<Long>> build = new ArrayList<ArrayList<Long>>();

        // debug only
//        Calendar myCal = Calendar.getInstance();
//        long startMillis = myCal.getTimeInMillis();
//        myCal.add(Calendar.DATE, 1);
//        long endMillis = myCal.getTimeInMillis();

        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), (int) ((cal.get(Calendar.HOUR_OF_DAY) + 1.5) / 3.0) * 3 + 9, 0, 0);
        long startMillis = cal.getTime().getTime();
        cal.add(Calendar.DATE, 1);
        long endMillis = cal.getTimeInMillis();

        Calendar newCal = Calendar.getInstance();
        newCal.setTimeInMillis(startMillis);
        Log.i(TAG, "start time interval " + newCal.getTime().toString());
        newCal.setTimeInMillis(endMillis);
        Log.i(TAG, "end time interval " + newCal.getTime().toString());

        if (badWeatherTimes.size() == 0) {
            ArrayList<Long> interval = new ArrayList<Long>();
            interval.add(startMillis);
            interval.add(endMillis);
            build.add(interval);
            return build;
        } else {

            for (int i = 0; i < badWeatherTimes.size(); i++) {
                Long badWeatherStart = badWeatherTimes.get(i);
                if (startMillis < badWeatherStart) {
                    ArrayList<Long> interval = new ArrayList<>();
                    if ((badWeatherStart - startMillis) / 60000L >= 30) {
                        interval.add(startMillis);
                        interval.add(badWeatherStart);
                        build.add(interval);
                    }
                    startMillis = badWeatherStart + 10800000L;
                    for (int k = i + 1; k < badWeatherTimes.size() && badWeatherTimes.get(k) == startMillis; k++) {
                        startMillis = startMillis + 10800000L;
                        i++;
                    }
                } else {
                    startMillis = badWeatherStart + 10800000L;
                }
            }
            if (startMillis < endMillis && (endMillis - startMillis) / 60000L >= 30) {
                ArrayList<Long> interval = new ArrayList<>();
                interval.add(startMillis);
                interval.add(endMillis);
                build.add(interval);
            }


        }
//        } else {
//            ArrayList<Long> interval = new ArrayList<Long>();
//            interval.add(startMillis);
//            build.add(interval);
//        }
//
//        boolean addEnd = true;
//        for (int i = 0; i < badWeatherTimes.size(); i++) {
//            //if the weather is not in range of the day
//            if (startMillis < badWeatherTimes.get(i) && badWeatherTimes.get(i) + 10800000L < endMillis) {
//                build.get(build.size() - 1).add(badWeatherTimes.get(i));
//                ArrayList<Long> interval = new ArrayList<Long>();
//                interval.add(badWeatherTimes.get(i) + 10800000L);
//                build.add(interval);
//
//            }
//            else if(startMillis < badWeatherTimes.get(i) && badWeatherTimes.get(i) < endMillis){
//                build.get(build.size() - 1).add(badWeatherTimes.get(i));
//                addEnd = false;
//            }
//        }
//        if (addEnd){
//            build.get(build.size() - 1).add(endMillis);
//        }


//        for (int i = 0; i < build.size(); i++) {
//            if ((build.get(i).get(1) - build.get(i).get(0)) / 60000L < 30) {
//                build.remove(i);
//                i--;
//            }
//        }

        return build;
    }

    /*
        based on free time from calendar and good weather intervals, return times where user can run
        store these times in sharedprefs
     */
    public ArrayList<ArrayList<Long>> getIntervals() {
        ArrayList<ArrayList<Long>> calTimes = getCalendarTimes();
        ArrayList<ArrayList<Long>> weatherTimes = getWeatherTimes();

        ArrayList<ArrayList<Long>> build = new ArrayList<ArrayList<Long>>();
        //if no good times
        if (calTimes.size() == 0 || weatherTimes.size() == 0) {
            return build;
        }

        Queue<ArrayList<Long>> freeIntervals = new LinkedList<ArrayList<Long>>();

        for (int i = 0; i < calTimes.size(); i++) {
            freeIntervals.add(calTimes.get(i));
        }
        for (int i = 0; i < calTimes.size(); i++) {
            ArrayList<Long> scheduleInterval = freeIntervals.remove();
            for (int j = 0; j < weatherTimes.size(); j++) {
                ArrayList<Long> weatherInterval = weatherTimes.get(j);
                if (weatherInterval.get(0) >= scheduleInterval.get(1)) {
                    break;
                } else {
                    ArrayList<Long> interval = new ArrayList<Long>();
                    interval.add(Math.max(weatherInterval.get(0), scheduleInterval.get(0)));
                    interval.add(Math.min(weatherInterval.get(1), scheduleInterval.get(1)));
                    freeIntervals.add(interval);
                }
            }
        }

        while (!freeIntervals.isEmpty()) {
            build.add(freeIntervals.remove());
        }

        ArrayList<ArrayList<String>> badWeatherTimeStrings = new ArrayList<>();
        for (Long i : badWeatherTimes) {
            ArrayList<String> timeInt = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(i);
            timeInt.add(cal.getTime().toString());
            cal.setTimeInMillis(i + 10800000L);
            timeInt.add(cal.getTime().toString());
            badWeatherTimeStrings.add(timeInt);
        }
        Log.i(TAG, "bad weather intervals " + badWeatherTimes.toString());
        Log.i(TAG, "bad weather intervals " + badWeatherTimeStrings.toString());

        ArrayList<ArrayList<String>> weatherTimeStrings = new ArrayList<>();
        for (ArrayList<Long> i : weatherTimes) {
            ArrayList<String> timeInt = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(i.get(0));
            timeInt.add(cal.getTime().toString());
            cal.setTimeInMillis(i.get(1));
            timeInt.add(cal.getTime().toString());
            weatherTimeStrings.add(timeInt);
        }
        Log.i(TAG, "weather intervals " + weatherTimes.toString());
        Log.i(TAG, "weather intervals " + weatherTimeStrings.toString());

        ArrayList<ArrayList<String>> calendarIntervalStrings = new ArrayList<>();
        for (ArrayList<Long> i : calTimes) {
            ArrayList<String> timeInt = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(i.get(0));
            timeInt.add(cal.getTime().toString());
            cal.setTimeInMillis(i.get(1));
            timeInt.add(cal.getTime().toString());
            calendarIntervalStrings.add(timeInt);
        }
        Log.i(TAG, "calendar intervals " + calTimes.toString());
        Log.i(TAG, "calendar intervals " + calendarIntervalStrings.toString());

        ArrayList<ArrayList<String>> times = new ArrayList<>();
        for (ArrayList<Long> i : build) {
            ArrayList<String> timeInt = new ArrayList<>();
            Calendar cal = Calendar.getInstance();
            cal.setTimeInMillis(i.get(0));
            timeInt.add(cal.getTime().toString());
            cal.setTimeInMillis(i.get(1));
            timeInt.add(cal.getTime().toString());
            times.add(timeInt);
        }
        Log.i(TAG, "intervals " + build.toString());
        Log.i(TAG, "intervals " + times.toString());

        setAlarms(build);
        return build;

    }

    /*
        for each interval of free time, set an alarm to notify user
     */
    public void setAlarms(List<ArrayList<Long>> intervals) {
        Log.i(TAG, "set alarms intervals " + intervals.toString());
        Log.i(TAG, "set notification alarms called");

        Calendar cal = Calendar.getInstance();

        for (ArrayList<Long> interval : intervals) {
            cal.setTimeInMillis(interval.get(0));
            Log.i(TAG, "set notification alarm for " + cal.getTime().toString());

            Intent notifyUserIntent = new Intent(this, NotifyUserReceiver.class);
            PendingIntent notifyUserPendingIntent = PendingIntent.getBroadcast(this, 0, notifyUserIntent, 0);

            // debugging
//            alarmManager.setExact(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + 5000, notifyUserPendingIntent);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, interval.get(0) , notifyUserPendingIntent);
        }
    }
}
