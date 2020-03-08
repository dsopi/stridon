package com.example.stridon;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.util.Calendar;

public class DeviceBootReceiver extends BroadcastReceiver {

    public static PendingIntent alarmIntent;
    public static AlarmManager alarmManager;

    public DeviceBootReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
            setAlarm(context);
        }
    }

    public void setAlarm(Context context) {
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        }
        if (alarmIntent == null) {
            Intent receiverIntent = new Intent(context, BuildModelReceiver.class);
            alarmIntent = PendingIntent.getBroadcast(context, 0, receiverIntent, 0);
        }

        Calendar calendar = Calendar.getInstance();
        // 11:59 pm
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        Log.i("device boot receiver ", calendar.getTime().toString());


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), 6000, alarmIntent);
        Toast.makeText(context, "ALARM SET in device boot receiver", Toast.LENGTH_SHORT).show();
        Log.i("device boot receiver", "set alarm");

        // todo set the notification alarms here too
    }
}