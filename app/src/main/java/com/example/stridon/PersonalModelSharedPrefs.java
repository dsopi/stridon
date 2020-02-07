package com.example.stridon;

import android.content.Context;
import android.content.SharedPreferences;

public class PersonalModelSharedPrefs {

    private static String personalModelFile = "personal_model";
    private static final String height = "height";
    private static final String weight = "weight";
    private static final String age = "age";
    // TODO FILL OUT THE REST

    private static PersonalModelSharedPrefs personalModelSharedPrefs;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static PersonalModelSharedPrefs getSharedPreferences(Context context) {
        if (personalModelSharedPrefs == null) {
            // not sure if this is the best way to get context
            personalModelSharedPrefs = new PersonalModelSharedPrefs(context);
        }
        return personalModelSharedPrefs;
    }

    private PersonalModelSharedPrefs(Context context) {
        prefs = context.getSharedPreferences(personalModelFile, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void enterHeight(float userHeight) {
        editor.putFloat(height, userHeight).apply();
    }

    public float getHeight() {
        return prefs.getFloat(height, -1);
    }

    public void enterWeight(int userWeight) {
        editor.putFloat(weight, userWeight).apply();
    }

    public float getWeight() {
        return prefs.getFloat(weight, -1);
    }

    public void enterAge(int userAge) {
        editor.putFloat(age, userAge).apply();
    }

    public float getAgge() {
        return prefs.getFloat(age, -1);
    }

    // TODO getter and setters for the rest of the personal model
}
