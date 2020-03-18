package com.example.stridon;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.stridon.extras.MyGoogleOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = LoginActivity.class.getSimpleName();

    private SignInButton signInButton;

    private GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;
    private static final int MY_CAL_REQ = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Log.i(TAG, "login on create");

        signInButton = findViewById(R.id.signin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        Log.i(TAG, ":)");
        mGoogleSignInClient = GoogleSignIn.getClient(this, MyGoogleOptions.gso);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "login on start");
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Log.i(TAG, "login activity account is null");
        } else {
            Log.i(TAG, "login activity account for " + account.getEmail());
        }

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        Log.i("tag", "on new Intent");
//
//        if (intent != null) {
//            if (intent.hasExtra("classFrom")) {
//                Log.i("tag", "intent is " + intent.getStringExtra("classFrom"));
//                Log.i("tag", "account for " + account.getEmail());
//                signOut();
//            }
//        }
//    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == RC_SIGN_IN) {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                Log.i(TAG, "call handle signin");
                handleSignInResult(task);
            }
        }
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            Log.i(TAG, "signed in!");
            account = completedTask.getResult(ApiException.class);
            Log.i(TAG, "calling to go settings");

            // todo ask for calendar and location permissions and THEN call go to settings
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
            } else {
                goToSettings();
            }

        } catch (ApiException e) {
            Log.i(TAG, "signInResult:failed code=" + e.getStatusCode());
        }
    }

    private void goToSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        Log.i(TAG, "go to settings intent");
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(settingsIntent);
    }

    //    private void signOut(){
//        mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
//            @Override
//            public void onComplete(@NonNull Task<Void> task) {
//                Log.i("tag", "revoked assess");
//                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        Log.i("tag", "signed out!");
//                        account = null;
//                    }
//                });
//            }
//        });
//    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_CAL_REQ: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    goToSettings();
                } else {
                    ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_CALENDAR}, MY_CAL_REQ);
                }
            }
        }
    }
}
