package com.example.stridon;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import java.util.ArrayList;
import java.util.Calendar;

public class BuildModelService extends JobIntentService {

    private static final String TAG = "Build Model Service";
    private static final int JOB_ID = 1;

    public BuildModelService() {
        super();
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        Log.i(TAG, "my service is running " + Calendar.getInstance().getTime().getTime());

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
     */
    public ArrayList<ArrayList<Long>> getIntervals() {
        return null;
    }

    /*
        for each interval of free time, set an alarm to notify user
     */
    public void setAlarms() {

    }
}
