package com.example.stridon;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    private SignInButton signInButton;

    private GoogleSignInAccount account;
    private GoogleSignInClient mGoogleSignInClient;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.i("tag", "on create");

        signInButton = findViewById(R.id.signin);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        mGoogleSignInClient = GoogleSignIn.getClient(this, MyGoogleOptions.gso);

    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i("tag", "on start");
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Log.i("tag", "main activity account is null");
        } else {
            Log.i("tag", "main activity account for " + account.getEmail());
            // TODO if user is already signed in, take to main page (skip settings too)
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
                Log.i("tag", "call handle signin");
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
            Log.i("tag", "signed in!");
            account = completedTask.getResult(ApiException.class);
            Log.i("tag", "calling to go settings");
            goToSettings();
        } catch (ApiException e) {
            Log.i("tag", "signInResult:failed code=" + e.getStatusCode());
        }
    }
    private void goToSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);
        Log.i("tag", "go to settings intent");
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

}
