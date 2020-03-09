package com.example.stridon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class BuildModelService extends JobIntentService {

    private static final String TAG = "Build Model Service";
    private static final int JOB_ID = 1;

    private AlarmManager alarmManager;

    public BuildModelService() {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "my service is running " + Calendar.getInstance().getTime().getTime());

        alarmManager = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

//        ArrayList<ArrayList<Long>> intervals = new ArrayList<>();
//        for (int i = 0; i < 2; i++) {
//            ArrayList<Long> in = new ArrayList<>();
//            in.add(new Long(1));
//            intervals.add(in);
//        }
//        setAlarms(intervals);
    }

    public static void enqueueWork(Context ctx, Intent intent) {
        enqueueWork(ctx, BuildModelService.class, JOB_ID, intent);
    }

    /*
        returns list of bad weather interval start time
     */
    public ArrayList<Long> getWeather() {
        return null;
    }

    /*
        based on free time from calendar and good weather intervals, return times where user can run
        store these times in sharedprefs
     */
    public ArrayList<ArrayList<Long>> getIntervals() {
        return null;
    }

    /*
        for each interval of free time, set an alarm to notify user
     */
    public void setAlarms(List<ArrayList<Long>> intervals) {
        Log.i(TAG, "set notification alarms called");

        for (ArrayList<Long> interval : intervals) {
            Intent notifyUserIntent = new Intent(this, NotifyUserReceiver.class);
            PendingIntent notifyUserPendingIntent = PendingIntent.getBroadcast(this, 0, notifyUserIntent, 0);

            alarmManager.setExact(AlarmManager.RTC_WAKEUP, interval.get(0), notifyUserPendingIntent);
        }
    }
}
