package com.example.stridon;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

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
}
