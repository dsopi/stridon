package com.example.stridon;

import android.app.Application;

import com.facebook.stetho.Stetho;

public class StridonApplication extends Application {
    public void onCreate() {
        super.onCreate();
        Stetho.initializeWithDefaults(this);
    }
}
