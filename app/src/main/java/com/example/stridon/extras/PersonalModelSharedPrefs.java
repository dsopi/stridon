package com.example.stridon.extras;

import android.content.Context;
import android.content.SharedPreferences;

public class PersonalModelSharedPrefs {

    private static String personalModelFile = "personal_model";
    private static final String height = "height";
    private static final String weight = "weight";
    private static final String age = "age";
    // TODO type of Stride
    private static final String numRunsPerWeek = "numRunsPerWeek";
    private static final String durationOfRuns = "durationOfRuns";
    private static final String startTimeOfRuns = "startTimeOfRuns";
    private static final String avgSpeedOfRuns = "avgSpeedOfRuns";
    private static final String numWalksPerWeek = "numWalksPerWeek";
    private static final String durationOfWalks = "durationOfWalks";
    private static final String startTimeOfWalks = "startTImeOfWalks";
    private static final String avgSpeedOfWalks = "avgSpeedOfWalks";
    // TODO time of last meal
    // TODO avg time of meals
    private static final String numStepsTakenThisDay = "numStepsTakenThisDay";
    private static final String avgNumStepsTakenPerDay = "avgNumStepsTakenPerDay";

    private static PersonalModelSharedPrefs personalModelSharedPrefs;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    public static PersonalModelSharedPrefs getInstance(Context context) {
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

    public void setNumRunsPerWeek(int userNumRunsPerWeek){
        editor.putInt(numRunsPerWeek, userNumRunsPerWeek).apply();
    }

    public int getNumRunsPerWeek(){
        return prefs.getInt(numRunsPerWeek, -1);
    }

    public void setDurationOfRuns(int userDurationOfRuns){
        editor.putInt(durationOfRuns, userDurationOfRuns).apply();
    }

    public int getDurationOfRuns(){
        return prefs.getInt(durationOfRuns, -1);
    }

    // TODO how to store time?
    public void setStartTimeOfRuns(String userStartTimeOfRuns){
        editor.putString(startTimeOfRuns, userStartTimeOfRuns).apply();
    }

    // TODO what is default value
    public String getStartTimeOfRuns(){
        return prefs.getString(startTimeOfRuns, "");
    }

    public void setAvgSpeedOfRuns(float userAvgSpeedOfRuns){
        editor.putFloat(avgSpeedOfRuns, userAvgSpeedOfRuns).apply();
    }

    public float getAvgSpeedOfRuns(){
        return prefs.getFloat(avgSpeedOfRuns, -1);
    }

    public void setNumWalksPerWeek(int userNumWalksPerWeek){
        editor.putInt(numWalksPerWeek, userNumWalksPerWeek).apply();
    }

    public int getNumWalksPerWeek(){
        return prefs.getInt(numWalksPerWeek, -1);
    }

    public void setDurationOfWalks(int userDurationOfWalks){
        editor.putInt(durationOfWalks, userDurationOfWalks).apply();
    }

    public int getDurationOfWalks(){
        return prefs.getInt(durationOfWalks, -1);
    }

    // TODO how to store time?
    public void setStartTimeOfWalks(String userStartTimeOfWalks){
        editor.putString(startTimeOfWalks, userStartTimeOfWalks).apply();
    }

    // TODO what is default value
    public String getStartTimeOfWalks(){
        return prefs.getString(startTimeOfWalks, "");
    }

    public void setAvgSpeedOfWalks(float userAvgSpeedOfWalks){
        editor.putFloat(avgSpeedOfWalks, userAvgSpeedOfWalks).apply();
    }

    public float getAvgSpeedOfWalks(){
        return prefs.getFloat(avgSpeedOfWalks, -1);
    }

    public void setNumStepsTakenThisDay(int userSetNumStepsTakenThisDay) {
        editor.putInt(numStepsTakenThisDay, userSetNumStepsTakenThisDay).apply();
    }

    // TODO does this need to be stored? couldn't be queried from google fit API?
    public int getNumStepsTakenThisDay() {
        return prefs.getInt(numStepsTakenThisDay, -1);
    }

    public void setAvgNumStepsTakenPerDay(int userAvgNumStepsTakenPerDay) {
        editor.putInt(avgNumStepsTakenPerDay, userAvgNumStepsTakenPerDay).apply();
    }

    public int getAvgNumStepsTakenPerDay(){
        return prefs.getInt(avgNumStepsTakenPerDay, -1);
    }

}
