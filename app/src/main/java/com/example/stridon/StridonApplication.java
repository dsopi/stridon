package com.example.stridon;

import android.app.Application;

import com.example.stridon.extras.PersonalModelSharedPrefs;
import com.facebook.stetho.Stetho;

public class StridonApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
        PersonalModelSharedPrefs personalModelSharedPrefs = PersonalModelSharedPrefs.getInstance(this.getApplicationContext());
    }
}
