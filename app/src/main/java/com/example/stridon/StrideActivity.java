package com.example.stridon;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.stridon.extras.MyGoogleOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.OnDataPointListener;
import com.google.android.gms.fitness.request.SensorRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class StrideActivity extends AppCompatActivity
        implements OnMapReadyCallback {

    private static final String TAG = StrideActivity.class.getSimpleName();

    //Location Provider
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(33.647173, -117.829006);
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 19;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";
    private CameraPosition mCameraPosition;

    //Timer
    private Chronometer stopwatch;
    private long updateTimer;
    private GoogleMap mMap;

    //Button
    private Button startButton;
    private Button finishButton;


    // step count and distance
    private TextView stepCountTextView;
    private TextView distanceTextView;
    private int stepCount = 0;
    private float distance = 0;
    // for stepcount and distance sensors will return the step count/distance
    // SINCE LAST READING therefore these booleans are used to
    // ignore the first reading
    boolean startedTrackingStepCount = false;
    boolean startedTrackingDistance = false;

    private GoogleSignInAccount account;

    private Stride stride;

    HashMap<Integer, String> weekdayMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stride);

        stepCountTextView = findViewById(R.id.stepsTextView);
        distanceTextView = findViewById(R.id.distanceTextView);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        stopwatch = findViewById(R.id.chronometer);
        stopwatch.setFormat("Time: %s");

        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);


        startButton = findViewById(R.id.startButton);
        startButton.setText("Start!");
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                start();
            }
        });

        finishButton = findViewById(R.id.finishButton);
        finishButton.setVisibility(View.GONE);
        finishButton.setText("Finish!");
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        weekdayMap.put(1, "Sunday");
        weekdayMap.put(2, "Monday");
        weekdayMap.put(3, "Tuesday");
        weekdayMap.put(4, "Wednesday");
        weekdayMap.put(5, "Thursday");
        weekdayMap.put(6, "Friday");
        weekdayMap.put(7, "Saturday");
    }

    @Override
    protected void onStart() {
        super.onStart();
        account = GoogleSignIn.getLastSignedInAccount(this);
        if (account == null) {
            Log.i(TAG, "account is null");
        } else {
            Log.i(TAG, "account for " + account.getEmail());
        }
        // TODO re ask for permission if needed, for now assume permission always granted
        if (GoogleSignIn.hasPermissions(account, MyGoogleOptions.fitnessOptions)) {
            Log.i("tag", "currently has google fit permissions");
        } else {
            Log.i("tag", "currently doesn't have google fit permissions");

        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //assign the google map to mMap
        mMap = googleMap;

        getLocationPermission();
        updateLocationUI();
        getDeviceLocation();

        Intent intent = getIntent();

        stride = intent.getParcelableExtra("stride");
//        String encoded = intent.getStringExtra("ENCODED");
        String encoded = stride.getEncodedPolyline();
        System.out.println("ENCODED: " + encoded);
        List<LatLng> points = PolyUtil.decode(encoded);
        Polyline trail = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .addAll(points));
    }

    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }

    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
                mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (mLocationPermissionGranted) {
                Task<Location> locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = task.getResult();
                            if (mLastKnownLocation != null) {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(mLastKnownLocation.getLatitude(),
                                                mLastKnownLocation.getLongitude()), DEFAULT_ZOOM));

                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    public void start() {
        Calendar c = Calendar.getInstance();
        stride.setTime(c.getTimeInMillis());
        stride.setDay(weekdayMap.get(c.get(Calendar.DAY_OF_WEEK)));

        stopwatch.start();
        stopwatch.setBase(SystemClock.elapsedRealtime());
        updateTimer = SystemClock.elapsedRealtime();
        stopwatch.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if ((SystemClock.elapsedRealtime() - updateTimer >= 10000)) {
                    getDeviceLocation();
                    updateTimer = SystemClock.elapsedRealtime();
                }
            }
        });
        finishButton.setVisibility(View.VISIBLE);
        startButton.setVisibility(View.GONE);

        stepCount = 0;
        distance = 0;
        Task<Void> sensorsResponse = Fitness.getSensorsClient(this, account)
                .add(
                        new SensorRequest.Builder()
                                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                                .setSamplingRate(1, TimeUnit.MILLISECONDS)
                                .build(),
                        new myStepCountListener());

        Task<Void> sensorsDistanceResponse = Fitness.getSensorsClient(this, account)
                .add(
                        new SensorRequest.Builder()
                                .setDataType(DataType.TYPE_DISTANCE_DELTA)
                                .setSamplingRate(1, TimeUnit.MILLISECONDS)
                                .build(),
                        new myDistanceListener());
    }

    private class myStepCountListener implements OnDataPointListener {
        @Override
        public void onDataPoint(DataPoint dataPoint) {
            Log.i(TAG, "on step count data point called");
            Log.i(TAG, dataPoint.toString());
            if (startedTrackingStepCount) {
                if (dataPoint != null) {
                    stepCount = stepCount + dataPoint.getValue(Field.FIELD_STEPS).asInt();
                    stepCountTextView.setText("current steps " + stepCount);
                }
            } else {
                startedTrackingStepCount = true;
            }
        }
    }

    private class myDistanceListener implements OnDataPointListener {
        @Override
        public void onDataPoint(DataPoint dataPoint) {
            Log.i(TAG, "on distance data point called");
            Log.i(TAG, dataPoint.toString());
            if (startedTrackingDistance) {
                if (dataPoint != null) {
                    distance = distance + dataPoint.getValue(Field.FIELD_DISTANCE).asFloat();
                    distanceTextView.setText("current distance " + distance);
                }
            } else {
                startedTrackingDistance = true;
            }
        }
    }

    //Will take the time and go to the results Screen
    public void finish() {
        long totalTime = SystemClock.elapsedRealtime() - stopwatch.getBase();

        stopwatch.stop();
        Intent resultsIntent = new Intent(this, StrideResultActivity.class);

        resultsIntent.putExtra("stepCount", stepCount);
        resultsIntent.putExtra("distance", distance/ 1609);
        resultsIntent.putExtra("totalTime", totalTime);
        resultsIntent.putExtra("stride", stride);
        resultsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(resultsIntent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // TODO remove listeners
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();

                Intent myIntent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivityForResult(myIntent, 0);

                return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
