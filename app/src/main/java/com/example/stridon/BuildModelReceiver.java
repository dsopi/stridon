package com.example.stridon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Calendar;

public class BuildModelReceiver extends BroadcastReceiver {

    private static final String TAG = "Build Model Receiver";


    public BuildModelReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "BUILD MODEL RECEIVER called ", Toast.LENGTH_SHORT).show();
        Calendar now = Calendar.getInstance();
        Log.i(TAG, "BUILD MODEL RECEIVER CALLED " + now.getTime().toString());

        Intent serviceIntent = new Intent(context, BuildModelService.class);
        BuildModelService.enqueueWork(context,serviceIntent);
        Log.i(TAG, "call start service");

//        pushNotification(context);
    }

    // todo move this somewhere else later
    private synchronized void pushNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HomeActivity.NOTIF_CHANNEL_ID)
                .setContentTitle("build model receiver")
                .setContentText("content").setSmallIcon(R.drawable.ic_launcher_foreground).
                        setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Much longer text that cannot fit one line...iadjfksmljndkleojh  fdhjcksxa g tg gt g gt gtf  gtfrf g gtr gt rgt rgtr gt g tg tr gr g g g gt gt gt g gt g g"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

// notificationId is a unique int for each notification that you must define
        notificationManager.notify(1, builder.build());
    }



}
