package com.example.stridon;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.dpro.widgets.WeekdaysPicker;
import com.example.stridon.extras.MyGoogleOptions;
import com.example.stridon.extras.PersonalModelSharedPrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;

import java.util.List;


public class SettingsActivity extends AppCompatActivity {
    private PersonalModelSharedPrefs personalModelSharedPrefs;

    private static final String TAG = SettingsActivity.class.getSimpleName();

    private RadioGroup goesOnRuns;
    private RadioGroup goesOnWalks;
    private Button finishButton;
    private WeekdaysPicker runWeekdaysPicker;
    private WeekdaysPicker walkWeekdaysPicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        personalModelSharedPrefs = PersonalModelSharedPrefs.getInstance(this.getApplicationContext());

        goesOnRuns = findViewById(R.id.goesOnRuns);
        goesOnWalks = findViewById(R.id.goesOnWalks);
        finishButton = findViewById(R.id.finish);

        runWeekdaysPicker = findViewById(R.id.timeOfRuns);
        walkWeekdaysPicker = findViewById(R.id.timeOfWalks);

        /**
         * TODO the values for each input should be pre-filled with previously entered values in
         * {@link PersonalModelSharedPrefs}
         */


        goesOnRuns.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                goesOnRunsClicked(group, checkedId);
            }
        });

        goesOnWalks.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                goesOnWalksClicked(group, checkedId);
            }
        });

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSettings();
                goToHome();
            }
        });

        Log.i(TAG, "settings activity account for " + GoogleSignIn.getLastSignedInAccount(this).getEmail());

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), MyGoogleOptions.fitnessOptions)) {
            Log.i(TAG, "currently has google fit permissions");
        } else {
            Log.i(TAG, "currently doesn't have google fit permissions");

        }
    }


    private void goesOnRunsClicked(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.yesRuns:
                addRunEditTexts();
                break;
            case R.id.noRuns:
                deleteRunEditTexts();
                break;
            default:
                break;
        }
    }

    // TODO ADD CHECKS TO MAKE SURE USER DOESN'T ENTER IN OUTRAGEOUS INPUT FOR NUMBER OF RUNS,
    // TODO DURATION OF RUNS, AND
    // TODO CHANGE TIME OF RUNS TO BE A TIME PICKER
    private void addRunEditTexts() {
        LinearLayout ll = findViewById(R.id.runLinearLayout);

        if (findViewById(R.id.numberOfRuns) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("# number of runs per week");
            et.setId(R.id.numberOfRuns);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(et);
        }

        if (findViewById(R.id.durationOfRuns) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("duration of runs in minutes");
            et.setId(R.id.durationOfRuns);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(et);
        }

        if (findViewById(R.id.distanceOfRuns) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("distance of runs in miles");
            et.setId(R.id.distanceOfRuns);
            et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ll.addView(et);
        }

    }

    private void deleteRunEditTexts() {
        LinearLayout ll = findViewById(R.id.runLinearLayout);

        if (findViewById(R.id.numberOfRuns) != null) {
            ll.removeView(findViewById(R.id.numberOfRuns));
        }

        if (findViewById(R.id.durationOfRuns) != null) {
            ll.removeView(findViewById(R.id.durationOfRuns));
        }

        if (findViewById(R.id.distanceOfRuns) != null) {
            ll.removeView(findViewById(R.id.distanceOfRuns));
        }

//        if (findViewById(R.id.timeOfRuns) != null) {
//            ll.removeView(findViewById(R.id.timeOfRuns));
//        }
    }

    private void goesOnWalksClicked(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.yesWalks:
//                Log.i("tag", "yes walk ");
                addWalkEditTexts();
                break;
            case R.id.noWalks:
//                Log.i("tag", "no walk ");
                deleteWalkEditTexts();
                break;
            default:
                break;
        }
    }

    // TODO ADD CHECKS TO MAKE SURE USER DOESN'T ENTER IN OUTRAGEOUS INPUT FOR NUMBER OF WALKS,
    // TODO DURATION OF WALKS, AND
    // TODO CHANGE TIME OF WALKS TO BE A TIME PICKER
    private void addWalkEditTexts() {
        LinearLayout ll = findViewById(R.id.walkLinearLayout);

        if (findViewById(R.id.numberOfWalks) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("# number of walks per week");
            et.setId(R.id.numberOfWalks);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(et);
        }

        if (findViewById(R.id.durationOfWalks) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("duration of walks in minutes");
            et.setId(R.id.durationOfWalks);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(et);
        }

        if (findViewById(R.id.distanceOfWalks) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("distance of walks in miles");
            et.setId(R.id.distanceOfWalks);
            et.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL);
            ll.addView(et);
        }

//        if (findViewById(R.id.timeOfWalks) == null) {
//            EditText et = new EditText(this);
//            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//            p.gravity = Gravity.CENTER_HORIZONTAL;
//            et.setLayoutParams(p);
//            et.setHint("time of walks");
//            et.setId(R.id.timeOfWalks);
//            ll.addView(et);
//        }
    }

    private void deleteWalkEditTexts() {
        LinearLayout ll = findViewById(R.id.walkLinearLayout);

        if (findViewById(R.id.numberOfWalks) != null) {
            ll.removeView(findViewById(R.id.numberOfWalks));
        }

        if (findViewById(R.id.durationOfWalks) != null) {
            ll.removeView(findViewById(R.id.durationOfWalks));
        }

        if (findViewById(R.id.distanceOfWalks) != null) {
            ll.removeView(findViewById(R.id.distanceOfWalks));
        }

//        if (findViewById(R.id.timeOfWalks) != null) {
//            ll.removeView(findViewById(R.id.timeOfWalks));
//        }
    }

    private void saveSettings() {
        // TODO do we want dialog to make fields required?

        EditText height = findViewById(R.id.heightEditText);
        if (!TextUtils.isEmpty(height.getText().toString()))
            personalModelSharedPrefs.setHeight(Float.valueOf(height.getText().toString()));

        EditText weight = findViewById(R.id.weightEditText);
        if (!TextUtils.isEmpty(weight.getText().toString()))
            personalModelSharedPrefs.setWeight(Integer.valueOf(weight.getText().toString()));

        EditText age = findViewById(R.id.ageEditText);
        if (!TextUtils.isEmpty(age.getText().toString()))
            personalModelSharedPrefs.setAge(Integer.valueOf(age.getText().toString()));

        EditText numberOfRuns = findViewById(R.id.numberOfRuns);
        if (numberOfRuns != null && !TextUtils.isEmpty(numberOfRuns.getText().toString())) {
            personalModelSharedPrefs.setNumRunsPerWeek(Integer.valueOf(numberOfRuns.getText().toString()));
        }

        EditText durationOfRuns = findViewById(R.id.durationOfRuns);
        EditText distanceOfRuns = findViewById(R.id.distanceOfRuns);

        if (durationOfRuns != null && distanceOfRuns != null && !TextUtils.isEmpty(distanceOfRuns.getText().toString()) && !TextUtils.isEmpty(durationOfRuns.getText().toString())) {
            int durationInMinutes = Integer.valueOf(durationOfRuns.getText().toString());
            personalModelSharedPrefs.setDurationOfRuns(durationInMinutes);
            float durationInHours = durationInMinutes / (float) 60;

            float avgSpeedOfRuns = Float.valueOf(distanceOfRuns.getText().toString()) / durationInHours;
            personalModelSharedPrefs.setAvgSpeedOfRuns(avgSpeedOfRuns);
        }

        storeRunDays();

        EditText numberOfWalks = findViewById(R.id.numberOfWalks);
        if (numberOfWalks != null && !TextUtils.isEmpty(numberOfWalks.getText().toString())) {
            personalModelSharedPrefs.setNumWalksPerWeek(Integer.valueOf(numberOfWalks.getText().toString()));
        }

        EditText durationOfWalks = findViewById(R.id.durationOfWalks);
        EditText distanceOfWalks = findViewById(R.id.distanceOfWalks);

        if (durationOfWalks != null && distanceOfWalks != null && !TextUtils.isEmpty(durationOfWalks.getText().toString()) && !TextUtils.isEmpty(distanceOfWalks.getText().toString())) {
            int durationInMinutes = Integer.valueOf(durationOfWalks.getText().toString());
            personalModelSharedPrefs.setDurationOfWalks(durationInMinutes);
            float durationInHours = durationInMinutes / (float) 60;

            float avgSpeedOfWalks = Float.valueOf(distanceOfWalks.getText().toString()) / durationInHours;
            personalModelSharedPrefs.setAvgSpeedOfWalks(avgSpeedOfWalks);
        }

        storeWalkDays();


        Log.i(TAG, "height " + personalModelSharedPrefs.getHeight());
        Log.i(TAG, "weight " + personalModelSharedPrefs.getWeight());
        Log.i(TAG, "age " + personalModelSharedPrefs.getAge());
        Log.i(TAG, "numRuns " + personalModelSharedPrefs.getNumRunsPerWeek());
        Log.i(TAG, "durationRuns " + personalModelSharedPrefs.getDurationOfRuns());
        Log.i(TAG, "speedRuns " + personalModelSharedPrefs.getAvgSpeedOfRuns());
        Log.i(TAG, "numWalks " + personalModelSharedPrefs.getNumWalksPerWeek());
        Log.i(TAG, "durationWalks " + personalModelSharedPrefs.getDurationOfWalks());
        Log.i(TAG, "speedWalks " + personalModelSharedPrefs.getAvgSpeedOfWalks());

        Log.i(TAG, "run days " + personalModelSharedPrefs.getStartTimeOfRuns());

    }

    private void goToHome() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(homeIntent);
    }

    private void storeRunDays() {
        List<String> days = runWeekdaysPicker.getSelectedDaysText();
        Log.i(TAG, days.toString().replace("[","").replace("]", ""));
        personalModelSharedPrefs.setStartTimeOfRuns(days.toString().replace("[","").replace("]", ""));
    }

    private void storeWalkDays() {
        List<String> days = walkWeekdaysPicker.getSelectedDaysText();
        Log.i(TAG, days.toString());
        personalModelSharedPrefs.setStartTimeOfWalks(days.toString().replace("[","").replace("]", ""));
    }

}
