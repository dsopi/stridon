package com.example.stridon;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stridon.SQLite.StrideDatabaseHelper;
import com.example.stridon.extras.PersonalModelSharedPrefs;

public class StrideResultActivity extends AppCompatActivity {

    private static final String TAG = "strideresultactivity";
    private TextView stepsView;
    private TextView distanceView;
    private TextView paceView;
    private TextView timeView;
    private Button homeButton;

    private Stride stride;

    private PersonalModelSharedPrefs personalModelSharedPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride_result);

        personalModelSharedPrefs = PersonalModelSharedPrefs.getInstance(this.getApplicationContext());

        Intent intent = getIntent();

        long totalTime = intent.getLongExtra("totalTime", 0);
        int stepCount = intent.getIntExtra("stepCount", 0);
        float distance = intent.getFloatExtra("distance", 0);
        stride = intent.getParcelableExtra("stride");

        stepsView = findViewById(R.id.stepsView);
        timeView = findViewById(R.id.timeView);
        distanceView = findViewById(R.id.distanceView);
        paceView = findViewById(R.id.paceView);
        homeButton = findViewById(R.id.homeButton);

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToHome();
            }
        });

        stepsView.setText("Steps : " + stepCount);
        distanceView.setText("Distance: " + distance);

        //time
        long totalSeconds = totalTime / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        timeView.setText("Time: " + minutes + ":" + seconds);

        //pace
        float rawSeconds = totalTime;
        rawSeconds = rawSeconds / 1000;
        float rawMinutes = rawSeconds / 60;
        float pace = distance / rawMinutes;

        paceView.setText("Pace: " + pace + " meters/min");

        stride.setDistance(distance);
        stride.setDuration((int) minutes);

        saveStride();
    }

    private void saveStride() {
        Log.i(TAG, "save stride " + stride.toString());
        StrideDatabaseHelper.StoreStrideTask storeStrideTask = new StrideDatabaseHelper.StoreStrideTask(StrideDatabaseHelper.getInstance(this), new StrideDatabaseHelper.StoreStrideTask.StoreStrideTaskListener() {
            @Override
            public void onStrideStored(Stride stride) {
                if (stride.getStrideType().equals("Run"))
                    personalModelSharedPrefs.setLastRunStrideTime(stride.getTime());
                else
                    personalModelSharedPrefs.setLastWalkStrideTime(stride.getTime());
            }
        });
        storeStrideTask.execute(stride);
    }

    private void goToHome() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(homeIntent);
    }
}
