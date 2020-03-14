package com.example.stridon.extras;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class PersonalModelSharedPrefs {

    private static String personalModelFile = "personal_model";

    // the following attributes are set in the settings page at the start of the app
    private static final String height = "height"; // centimeters
    private static final String weight = "weight"; // pounds
    private static final String age = "age"; // years
    // type of Stride doesn't have to be explicitly stored

    private static final String daysOfRuns = "daysOfRuns"; // string of days seperated by commas
    private static final String durationOfRuns = "durationOfRuns"; // minutes
    //    private static final String startTimeOfRuns = "startTimeOfRuns";
//    private static final String avgSpeedOfRuns = "avgSpeedOfRuns"; // miles per hour
    private static final String distanceOfRuns = "distanceOfRuns";
    private static final String daysOfWalks = "daysOfWalks";
    private static final String durationOfWalks = "durationOfWalks"; // minutes
    //    private static final String startTimeOfWalks = "startTimeOfWalks";
//    private static final String avgSpeedOfWalks = "avgSpeedOfWalks"; // miles per hour
    private static final String distanceOfWalks = "distanceOfWalks";

    private static final String numStepsTakenThisDay = "numStepsTakenThisDay";
    private static final String avgNumStepsTakenPerDay = "avgNumStepsTakenPerDay"; // todo how to keep track of this
    private static final String lastStrideTime = "lastStrideTime";
    private static final String lastRunStrideTime = "lastRunStrideTime";
    private static final String lastWalkStrideTime = "lastWalkStrideTime";
    private static final String notificationTimes = "notificationtimes";

    private static PersonalModelSharedPrefs personalModelSharedPrefs;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static synchronized PersonalModelSharedPrefs getInstance(Context context) {
        if (personalModelSharedPrefs == null) {
            // not sure if this is the best way to get context
            personalModelSharedPrefs = new PersonalModelSharedPrefs(context.getApplicationContext());
        }
        return personalModelSharedPrefs;
    }

    private PersonalModelSharedPrefs(Context context) {
        prefs = context.getSharedPreferences(personalModelFile, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setHeight(float userHeight) {
        editor.putFloat(height, userHeight).apply();
    }

    public float getHeight() {
        return prefs.getFloat(height, -1);
    }

    public void setWeight(int userWeight) {
        editor.putFloat(weight, userWeight).apply();
    }

    public float getWeight() {
        return prefs.getFloat(weight, -1);
    }

    public void setAge(int userAge) {
        editor.putFloat(age, userAge).apply();
    }

    public float getAge() {
        return prefs.getFloat(age, -1);
    }

//    public void setTotalRunSrides(int userTotalRunStrides) {
//        editor.putInt(totalRunSrides, userTotalRunStrides).apply();
//    }
//
//    public int getTotalRunStrides() {
//        return prefs.getInt(totalRunSrides, 1);
//    }
//
//    public void setTotalWalkStrides(int userTotalWalkStrides) {
//        editor.putInt(totalWalkStrides, userTotalWalkStrides).apply();
//    }
//
//    public int getTotalWalkStrides() {
//        return prefs.getInt(totalWalkStrides, 1);
//    }
//
//    public void setNumRunsPerWeek(int userNumRunsPerWeek) {
//        editor.putInt(numRunsPerWeek, userNumRunsPerWeek).apply();
//    }
//
//    public int getNumRunsPerWeek() {
//        return prefs.getInt(numRunsPerWeek, -1);
//    }

    public void setDaysOfRuns(String userDaysOfRuns) {
        editor.putString(daysOfRuns, userDaysOfRuns).apply();
    }

    public String getDaysOfRuns() {
        return prefs.getString(daysOfRuns, "");
    }

    public void setDurationOfRuns(int userDurationOfRuns) {
        editor.putInt(durationOfRuns, userDurationOfRuns).apply();
    }

    public int getDurationOfRuns() {
        return prefs.getInt(durationOfRuns, -1);
    }

    public void setDistanceOfRuns(float userDistanceOfRuns) {
        editor.putFloat(distanceOfRuns, userDistanceOfRuns).apply();
    }

    public float getDistanceOfRuns() {
        return prefs.getFloat(distanceOfRuns, -1);
    }

//    // TODO how to store time?
//    public void setStartTimeOfRuns(String userStartTimeOfRuns) {
//        editor.putString(startTimeOfRuns, userStartTimeOfRuns).apply();
//    }
//
//    // TODO what is default value
//    public String getStartTimeOfRuns() {
//        return prefs.getString(startTimeOfRuns, "");
//    }
//
//    public void setAvgSpeedOfRuns(float userAvgSpeedOfRuns) {
//        editor.putFloat(avgSpeedOfRuns, userAvgSpeedOfRuns).apply();
//    }
//
//    public float getAvgSpeedOfRuns() {
//        return prefs.getFloat(avgSpeedOfRuns, -1);
//    }
//
//    public void setNumWalksPerWeek(int userNumWalksPerWeek) {
//        editor.putInt(numWalksPerWeek, userNumWalksPerWeek).apply();
//    }
//
//    public int getNumWalksPerWeek() {
//        return prefs.getInt(numWalksPerWeek, -1);
//    }

    public void setDaysOfWalks(String userDaysOfWalks) {
        editor.putString(daysOfWalks, userDaysOfWalks).apply();
    }

    public String getDaysOfWalks() {
        return prefs.getString(daysOfWalks, "");
    }

    public void setDurationOfWalks(int userDurationOfWalks) {
        editor.putInt(durationOfWalks, userDurationOfWalks).apply();
    }

    public int getDurationOfWalks() {
        return prefs.getInt(durationOfWalks, -1);
    }

    public void setDistanceOfWalks(float userDistanceOfWalks) {
        editor.putFloat(distanceOfWalks, userDistanceOfWalks).apply();
    }

    public float getDistanceOfWalks() {
        return prefs.getFloat(distanceOfWalks, -1);
    }

//    // TODO how to store time?
//    public void setStartTimeOfWalks(String userStartTimeOfWalks) {
//        editor.putString(startTimeOfWalks, userStartTimeOfWalks).apply();
//    }
//
//    // TODO what is default value
//    public String getStartTimeOfWalks() {
//        return prefs.getString(startTimeOfWalks, "");
//    }
//
//    public void setAvgSpeedOfWalks(float userAvgSpeedOfWalks) {
//        editor.putFloat(avgSpeedOfWalks, userAvgSpeedOfWalks).apply();
//    }
//
//    public float getAvgSpeedOfWalks() {
//        return prefs.getFloat(avgSpeedOfWalks, -1);
//    }

    public void setNumStepsTakenThisDay(long userSetNumStepsTakenThisDay) {
        editor.putLong(numStepsTakenThisDay, userSetNumStepsTakenThisDay).apply();
    }

    // TODO does this need to be stored? couldn't be queried from google fit API?
    public long getNumStepsTakenThisDay() {
        return prefs.getLong(numStepsTakenThisDay, -1);
    }

    public void setAvgNumStepsTakenPerDay(int userAvgNumStepsTakenPerDay) {
        editor.putInt(avgNumStepsTakenPerDay, userAvgNumStepsTakenPerDay).apply();
    }

    public int getAvgNumStepsTakenPerDay() {
        return prefs.getInt(avgNumStepsTakenPerDay, -1);
    }

    public void setLastStrideTime(long userLastStrideTime) {
        editor.putLong(lastStrideTime, userLastStrideTime).apply();
    }

    public long getLastStrideTime() {
        return prefs.getLong(lastStrideTime, -1);
    }

    public void setLastRunStrideTime(long userLastRunStrideTime) {
        editor.putLong(lastRunStrideTime, userLastRunStrideTime).apply();
    }

    public long getLastRunStrideTime() {
        return prefs.getLong(lastRunStrideTime, -1);
    }

    public void setLastWalkStrideTime(long userLastWalkStrideTime) {
        editor.putLong(lastWalkStrideTime, userLastWalkStrideTime).apply();
    }

    public long getLastWalkStrideTime() {
        return prefs.getLong(lastWalkStrideTime, -1);
    }

    public void setNotificationTimes(ArrayList<ArrayList<Long>> intervals){
        String userNotificationTimes = "";
        String comma = "";
        for (ArrayList<Long> interval : intervals) {
            userNotificationTimes = userNotificationTimes + comma + "(" + interval.get(0) + " " + interval.get(1) + ")";
            if (TextUtils.isEmpty(comma)) {
                comma = ",";
            }
        }
        editor.putString(notificationTimes, userNotificationTimes).apply();
    }

    public ArrayList<ArrayList<Long>> getNotificationTimes(){
        ArrayList<ArrayList<Long>> notificationList = new ArrayList<>();
        String notifTimes = prefs.getString(notificationTimes, "");
        if (TextUtils.isEmpty(notifTimes)){
            return notificationList;
        }
        String[] notifSplit = notifTimes.split(",");
        for (String interval : notifSplit){
            String[] intervalSplit = interval.replace("(", "").replace(")","").split(" ");
            ArrayList<Long> newInterval = new ArrayList<>();
            newInterval.add(Long.valueOf(intervalSplit[0]));
            newInterval.add(Long.valueOf(intervalSplit[1]));
            notificationList.add(newInterval);
        }

        return notificationList;
    }
}
