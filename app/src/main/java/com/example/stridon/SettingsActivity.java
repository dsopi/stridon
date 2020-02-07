package com.example.stridon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity {

    Button signOutButton;
    private RadioGroup goesOnRuns;
    private RadioGroup goesOnWalks;

    private GoogleSignInClient mGoogleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        signOutButton = findViewById(R.id.signout);
        goesOnRuns = findViewById(R.id.goesOnRuns);
        goesOnWalks = findViewById(R.id.goesOnWalks);

        // TODO this signout button is just here for testing, eventually it will be moved since signout doesnt need to be in settings page
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });

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


        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                .build();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .addExtension(fitnessOptions)
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        Log.i("tag", "settings activity account for " + GoogleSignIn.getLastSignedInAccount(this).getEmail());

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            Log.i("tag", "currently has google fit permissions");
        } else {
            Log.i("tag", "currently doesn't have google fit permissions");

        }
    }

    private void signOut() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i("tag", "revoked assess");
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i("tag", "signed out!");
                        Intent mainScreenIntent = new Intent(SettingsActivity.this, MainActivity.class);
                        mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(mainScreenIntent);
                    }
                });
            }
        });

//        Intent mainScreenIntent = new Intent(this, MainActivity.class);
//        mainScreenIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//        mainScreenIntent.putExtra("classFrom", SettingsActivity.class.toString());
//        startActivity(mainScreenIntent);
    }


    private void goesOnRunsClicked(RadioGroup group, int checkedId) {
        switch (checkedId) {
            case R.id.yesRuns:
//                Log.i("tag", "yes run ");
                addRunEditTexts();
                break;
            case R.id.noRuns:
//                Log.i("tag", "no run ");
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
            et.setHint("# duration of runs in minutes");
            et.setId(R.id.durationOfRuns);
            et.setInputType(InputType.TYPE_CLASS_NUMBER);
            ll.addView(et);
        }

        if (findViewById(R.id.timeOfRuns) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("time of runs");
            et.setId(R.id.timeOfRuns);
            et.setInputType(InputType.TYPE_CLASS_DATETIME);
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

        if (findViewById(R.id.timeOfRuns) != null) {
            ll.removeView(findViewById(R.id.timeOfRuns));
        }
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
            ll.addView(et);
        }

        if (findViewById(R.id.durationOfWalks) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("# duration of walks in minutes");
            et.setId(R.id.durationOfWalks);
            ll.addView(et);
        }

        if (findViewById(R.id.timeOfWalks) == null) {
            EditText et = new EditText(this);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            p.gravity = Gravity.CENTER_HORIZONTAL;
            et.setLayoutParams(p);
            et.setHint("time of walks");
            et.setId(R.id.timeOfWalks);
            ll.addView(et);
        }
    }

    private void deleteWalkEditTexts() {
        LinearLayout ll = findViewById(R.id.walkLinearLayout);

        if (findViewById(R.id.numberOfWalks) != null) {
            ll.removeView(findViewById(R.id.numberOfWalks));
        }

        if (findViewById(R.id.durationOfWalks) != null) {
            ll.removeView(findViewById(R.id.durationOfWalks));
        }

        if (findViewById(R.id.timeOfWalks) != null) {
            ll.removeView(findViewById(R.id.timeOfWalks));
        }
    }
}
