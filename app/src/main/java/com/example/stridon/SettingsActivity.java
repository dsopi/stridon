package com.example.stridon;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class SettingsActivity extends AppCompatActivity {

    Button signOutButton;

    private GoogleSignInClient mGoogleSignInClient;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        signOutButton = findViewById(R.id.signout);

        // TODO this signout button is just here for testing, eventually it will be moved since signout doesnt need to be in settings page
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
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

    private void signOut(){
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
}
