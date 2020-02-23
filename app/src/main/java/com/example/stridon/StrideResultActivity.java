package com.example.stridon;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class StrideResultActivity extends AppCompatActivity {

    private TextView stepsView;
    private TextView distanceView;
    private TextView paceView;
    private TextView timeView;
    private Button homeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride_result);

        Intent intent = getIntent();

        long totalTime = intent.getLongExtra("totalTime",0);
        int stepCount = intent.getIntExtra("stepCount",0);
        float distance = intent.getFloatExtra("distance",0);

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
        rawSeconds  = rawSeconds / 1000;
        float rawMinutes = rawSeconds / 60;
        float pace = distance / rawMinutes;

        paceView.setText("Pace: " +  pace + "meters/min");
    }

    private void goToHome() {
        Intent homeIntent = new Intent(this, HomeActivity.class);
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(homeIntent);
    }
}
