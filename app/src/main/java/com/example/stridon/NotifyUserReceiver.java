package com.example.stridon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotifyUserReceiver extends BroadcastReceiver {
    private static final String TAG = "Notify User Receiver";

    private static int notification_id = 0;

    public NotifyUserReceiver() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "notify user on receive");
        pushNotification(context);
    }

    private synchronized void pushNotification(Context context) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, HomeActivity.NOTIF_CHANNEL_ID)
                .setContentTitle("time for a stride")
                .setContentText("content").setSmallIcon(R.drawable.ic_launcher_foreground).
                        setStyle(new NotificationCompat.BigTextStyle()
                                .bigText("Much longer text that cannot fit one line...iadjfksmljndkleojh  fdhjcksxa g tg gt g gt gtf  gtfrf g gtr gt rgt rgtr gt g tg tr gr g g g gt gt gt g gt g g"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT).setVisibility(NotificationCompat.VISIBILITY_PUBLIC).setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notification_id++, builder.build());
    }
}
