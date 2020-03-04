package com.example.stridon;

import android.app.Application;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.util.Log;
import android.widget.Toast;

import com.example.stridon.extras.PersonalModelSharedPrefs;
import com.facebook.stetho.Stetho;

public class StridonApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        // initialize shared prefs now
        PersonalModelSharedPrefs personalModelSharedPrefs = PersonalModelSharedPrefs.getInstance(this.getApplicationContext());

        ComponentName receiver = new ComponentName(this.getApplicationContext(), DeviceBootReceiver.class);
        PackageManager pm = this.getPackageManager();

        pm.setComponentEnabledSetting(receiver,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);

        Toast.makeText(this, "my application call set alarm", Toast.LENGTH_SHORT).show();
        Log.i("APPLICATION", "my application call set alarm");
        DeviceBootReceiver deviceBootReciever = new DeviceBootReceiver();
        deviceBootReciever.setAlarm(this.getApplicationContext());

    }
}
