package com.example.stridon;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.stridon.SQLite.StrideDatabaseHelper;
import com.example.stridon.extras.MyGoogleOptions;
import com.example.stridon.extras.PersonalModelSharedPrefs;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements OnMapReadyCallback, StrideRecFragment.StrideRecListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final String NOTIF_CHANNEL_ID = "Stride notification channel";

    private GoogleSignInClient mGoogleSignInClient;

    private FragmentManager fragmentManager = getSupportFragmentManager();
    private PersonalModelSharedPrefs personalModelSharedPrefs;

    //These variables are used to detect
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private boolean mLocationPermissionGranted;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private final LatLng mDefaultLocation = new LatLng(33.647173, -117.829006);
    private Location mLastKnownLocation;
    private static final int DEFAULT_ZOOM = 15;
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    private GoogleMap mMap;
    private RequestQueue mQueue;
    private CameraPosition mCameraPosition;

    private double user_lat;
    private double user_lng;

    private Stride currStride;
//    private String encodedLine;

    private StrideDatabaseHelper strideDatabaseHelper;

    private ArrayList<Stride> strideList = new ArrayList<Stride>();
    private int pityCounter = 5;

    StrideRecFragment strideRecFragment = null;

    Button startStrideButton;
    TextView weatherTextView;

    private String API_KEY = "4843f8fbd4876cc07f77a0730a5302b1";
    public double temp_today;
    public JsonObjectRequest Weather;

    HashMap<String, Integer> weekdayMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        personalModelSharedPrefs = PersonalModelSharedPrefs.getInstance(this.getApplicationContext());


        startStrideButton = findViewById(R.id.startStrideButton);
        weatherTextView = findViewById(R.id.weatherTextView);

        startStrideButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToStride(currStride);
            }
        });

        mGoogleSignInClient = GoogleSignIn.getClient(this, MyGoogleOptions.gso);

        //Log.i(TAG, "home activity account for " + GoogleSignIn.getLastSignedInAccount(this).getEmail());

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), MyGoogleOptions.fitnessOptions)) {
            Log.i(TAG, "currently has google fit permissions");
        } else {
            Log.i(TAG, "currently doesn't have google fit permissions");
        }

        //User Location
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }


        mQueue = Volley.newRequestQueue(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        strideDatabaseHelper = StrideDatabaseHelper.getInstance(this);

        FragmentTransaction transaction = fragmentManager.beginTransaction();
        // at first recyclerview will have empty list
        // once all the strides have been retrieved, it will notify recyclerview of new list
        strideRecFragment = StrideRecFragment.newInstance(strideList.toArray(new Stride[strideList.size()]));
        transaction.add(R.id.strideRecFragContainer, strideRecFragment);
        transaction.commit();

        createNotificationChannel();

        weekdayMap.put("Sunday", 1);
        weekdayMap.put("Monday", 2);
        weekdayMap.put("Tuesday", 3);
        weekdayMap.put("Wednesday", 4);
        weekdayMap.put("Thursday", 5);
        weekdayMap.put("Friday", 6);
        weekdayMap.put("Saturday", 7);
    }

    @Override
    protected void onStart() {
        super.onStart();
        Weather = getWeather();
        //Load up the map
        SupportMapFragment mapFragment = (SupportMapFragment) fragmentManager
                .findFragmentById(R.id.mapFragment);
        mapFragment.getMapAsync(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.common_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.settingsOption:
                Log.i(TAG, "settings");
                goToSettings();
                return true;
            case R.id.storeStride:
                Log.i(TAG, "store stride");
                storeStride();
                return true;
            case R.id.retrieveStride:
                Log.i(TAG, "retrieve stride");
                retrieveStride();
                return true;
            case R.id.deleteStrides:
                Log.i(TAG, "delete strides");
                deleteStrides();
                return true;
            case R.id.buildModel:
                Log.i(TAG, "build model");
                buildModel();
                return true;
            case R.id.logoutOption:
                Log.i(TAG, "logout");
                signOut();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        //assign the google map to mMap
        mMap = googleMap;
        mMap.clear();
        getLocationPermission();
        if (mLocationPermissionGranted) {
            updateLocationUI();
//            getDeviceLocation();
            buildModel();
        }

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
                getDeviceLocation();
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
                                user_lat = mLastKnownLocation.getLatitude();
                                user_lng = mLastKnownLocation.getLongitude();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(user_lat, user_lng), DEFAULT_ZOOM));
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

    private void getFourPoints(double distance, String strideType) {
        double s_dis = distance / 2.0;
        double lat_dis = (0.3 + (Math.random() * .4)) * s_dis;
        double lng_dis = s_dis - lat_dis;
        if (Math.random() < .5) {
            lat_dis = lat_dis * -1.0;
        }
        if (Math.random() < .5) {
            lng_dis = lng_dis * -1.0;
        }

        ArrayList<Double> lat_array = new ArrayList<Double>();
        ArrayList<Double> lng_array = new ArrayList<Double>();

        lat_array.add(user_lat + lat_dis / 69.0);
        lng_array.add(user_lng);

        lat_array.add(user_lat + lat_dis / 69.0);
        lng_array.add(user_lng + lng_dis / 59.0);

        lat_array.add(user_lat);
        lng_array.add(user_lng + lng_dis / 59.0);

        getDirectionUrl(lat_array, lng_array, distance, strideType);
    }

    private void getDirectionUrl(ArrayList<Double> lat_array, ArrayList<Double> lng_array, double distance, String strideType) {

        //Uses string building to make the url to pass to the volley
        String direction_url = "https://maps.googleapis.com/maps/api/directions/json?";
        direction_url = direction_url + "origin=" + user_lat + "," + user_lng + "&";
        direction_url = direction_url + "destination=" + user_lat + "," + user_lng + "&";
        direction_url = direction_url + "mode=walking&waypoints=";

        for (int i = 0; i < lat_array.size(); i++) {
            direction_url = direction_url + "via:" + lat_array.get(i).toString() + "%2C" + lng_array.get(i).toString();
            if (i < lat_array.size() - 1) {
                direction_url = direction_url + "%7C";
            }
        }
        direction_url = direction_url + "&key=" + getString(R.string.google_maps_key);

        System.out.println(direction_url);

        //Now that the url is built, will go make the polyline
        getPolyline(direction_url, distance, strideType);
    }

    private void getPolyline(String url, final double myDistance, final String strideType) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            //Parse the JSON to get the encoded Polyline
                            JSONArray routes = response.getJSONArray("routes");
                            JSONObject path = routes.getJSONObject(0);
                            JSONObject overview = path.getJSONObject("overview_polyline");
                            String encoded_line = overview.getString("points");

                            //Parse the JSON to get the actual distance
                            JSONArray legs = path.getJSONArray("legs");
                            JSONObject path2 = legs.getJSONObject(0);
                            JSONObject distance = path2.getJSONObject("distance");
                            int meters = distance.getInt("value");

                            double actualDistance = meters / 1609.0;
                            actualDistance = Math.round(actualDistance * 100.0) / 100.0;
                            System.out.println("actualDistance: " + actualDistance);

                            int estDuration = 0;
                            if (strideType.equals("Run")) {
                                double avgDistanceOfRun = personalModelSharedPrefs.getDistanceOfRuns();
                                double avgDurationOfRun = personalModelSharedPrefs.getDurationOfRuns();
//                                double avgDurationOfRun = personalModelSharedPrefs.getDurationOfRuns()/60.0; // in hour
                                double avgSpeedOfRun = avgDistanceOfRun / avgDurationOfRun;
                                estDuration = (int) (actualDistance / avgSpeedOfRun);
//                                estDuration = (int) (actualDistance / (avgSpeedOfRun / 60)); // in minutes
                            } else {
                                double avgDistanceOfWalk = personalModelSharedPrefs.getDistanceOfWalks();
                                double avgDurationOfWalk = personalModelSharedPrefs.getDurationOfWalks();
//                                double avgDurationOfWalk = personalModelSharedPrefs.getDurationOfWalks()/60.0;
                                double avgSpeedOfWalk = avgDistanceOfWalk / avgDurationOfWalk;
                                estDuration = (int) (actualDistance / avgSpeedOfWalk);
//                                estDuration = (int) (actualDistance / (avgSpeedOfWalk / 60));
                            }

                            // TODO update with actual values in Stride
                            Stride newStride = new Stride(user_lat, user_lng, encoded_line, actualDistance, estDuration, strideType, temp_today, "Monday", 0); // day,time, distance, duration will be overwritten
                            strideList.add(newStride);

                            //draw the initial line
                            if (strideList.size() == 1) {
                                currStride = strideList.get(0);
//                                encodedLine = encoded_line;
                                drawPolyline(currStride.getEncodedPolyline());
                            }
                            if (strideList.size() >= 6) {
                                linkStrideListToRecFragment();
                            }


                        } catch (JSONException e) {
                            System.out.println("Error: Could not retrieve the Polyline from Direction Url");
                            pityCounter--;
                            if (pityCounter > 0) {
                                getFourPoints(myDistance, strideType);
                            }
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });

        mQueue.add(request);
    }


    private void drawPolyline(String encoded) {
        List<LatLng> points = PolyUtil.decode(encoded);
        Polyline trail = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .addAll(points));

    }

    private void generateStrides(String strideType, double runDistance, double walkDistance) {
        strideList.clear();
        if (strideType.equals("Run")){
            for (int i = 0; i < 4; i++) {
                getFourPoints(runDistance, strideType);
            }
            for (int i = 0; i < 2; i++ ){
                getFourPoints(walkDistance, "Walk");
            }
        } else {
            for (int i = 0; i < 4; i++) {
                getFourPoints(walkDistance, strideType);
            }
            for (int i = 0; i < 2; i++ ){
                getFourPoints(runDistance, "Run");
            }
        }
    }

    private void signOut() {
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "revoked assess");
                mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.i(TAG, "signed out!");
                        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(loginIntent);
                    }
                });
            }
        });
    }

    private void goToSettings() {
        Intent settingsIntent = new Intent(this, SettingsActivity.class);

        Log.i(TAG, "go to settings intent");
        settingsIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(settingsIntent);
    }

    private void goToStride(Stride stride) {
        Intent strideIntent = new Intent(this, StrideActivity.class);

        strideIntent.putExtra("stride", stride);
//        strideIntent.putExtra("ENCODED", stride.getEncodedPolyline());

        Log.i(TAG, "go to Stride intent");
        startActivity(strideIntent);
    }

    private void storeStride() {
        ArrayList<Stride> strides = new ArrayList<>();

        // runs
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Calendar.JANUARY, 6, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1, 15, "Run", 76, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.JANUARY, 10, 7, 30, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.2, 15, "Run", 68, "Friday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.JANUARY, 13, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2, 19, "Run", 75, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.JANUARY, 20, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.3, 21, "Run", 74, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.JANUARY, 24, 7, 30, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.5, 25, "Run", 62, "Friday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.JANUARY, 27, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.1, 20, "Run", 70, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.FEBRUARY, 3, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.2, 20, "Run", 64, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.FEBRUARY, 7, 7, 30, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 3, 30, "Run", 65, "Friday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.FEBRUARY, 10, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.4, 25, "Run", 60, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.FEBRUARY, 14, 7, 30, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.7, 26, "Run", 60, "Friday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.FEBRUARY, 24, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2, 18, "Run", 64, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.FEBRUARY, 28, 7, 30, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.5, 24, "Run", 66, "Friday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.MARCH, 2, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 3.2, 35, "Run", 66, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.MARCH, 7, 9, 30, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.5, 25, "Run", 68, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.MARCH, 8, 9, 30, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 4, 45, "Run", 61, "Monday", calendar.getTimeInMillis()));
        calendar.set(Calendar.YEAR, Calendar.MARCH, 9, 8, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2.8, 30, "Run", 59, "Monday", calendar.getTimeInMillis()));

        // walks
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, Calendar.JANUARY, 4, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", .9, 17, "Walk", 59, "Saturday", cal.getTimeInMillis()));

        cal.set(Calendar.YEAR, Calendar.JANUARY, 7, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.8, 23, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.JANUARY, 8, 17, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 0.8, 16, "Walk", 59, "Wednesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.JANUARY, 14, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1, 18, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.JANUARY, 18, 10, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", .4, 9, "Walk", 59, "Saturday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.JANUARY, 21, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.8, 20, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.JANUARY, 22, 12, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.4, 25, "Walk", 59, "Wednesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.JANUARY, 28, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.2, 23, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.JANUARY, 29, 12, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1, 20, "Walk", 59, "Wednesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 4, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1, 17, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 5, 12, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.1, 18, "Walk", 59, "Wednesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 11, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.2, 20, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 12, 12, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.2, 20, "Walk", 59, "Wednesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 18, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1, 17, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 19, 12, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", .6, 13, "Walk", 59, "Wednesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 25, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.1, 19, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.FEBRUARY, 27, 12, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 2, 40, "Walk", 59, "Thursday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.MARCH, 3, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1.8, 36, "Walk", 59, "Tuesday", cal.getTimeInMillis()));
        cal.set(Calendar.YEAR, Calendar.MARCH, 10, 15, 0, 0);
        strides.add(new Stride(33.6405, 117.8443, "encoded polyline", 1, 19, "Walk", 59, "Tuesday", cal.getTimeInMillis()));


        for (Stride stride : strides) {
            StrideDatabaseHelper.StoreStrideTask storeStrideTask = new StrideDatabaseHelper.StoreStrideTask(strideDatabaseHelper, new StrideDatabaseHelper.StoreStrideTask.StoreStrideTaskListener() {
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
    }

    private void retrieveStride() {
        StrideDatabaseHelper.GetLast10Strides getLast10Strides = new StrideDatabaseHelper.GetLast10Strides(strideDatabaseHelper, null);
        getLast10Strides.execute();
    }

    private void deleteStrides() {
        StrideDatabaseHelper.DeleteStrides deleteStrides = new StrideDatabaseHelper.DeleteStrides(strideDatabaseHelper);
        deleteStrides.execute();
    }


    @Override
    public void onStrideRecSelected(Stride stride) {
        mMap.clear();
        currStride = stride;
        drawPolyline(stride.getEncodedPolyline());
//        encodedLine = stride.getEncodedPolyline();
    }

    private void linkStrideListToRecFragment() {
        if (strideRecFragment != null) {
            Log.i(TAG, "striderec is not null");
            Log.i(TAG, "stride rec " + strideList.toString());
            strideRecFragment.setStrideRecs(strideList.toArray(new Stride[strideList.size()]));
        } else {
            Log.i(TAG, "striderec is null");

        }
//        FragmentTransaction transaction = fragmentManager.beginTransaction();
//        Stride[] strideRecs = strideList.toArray(new Stride[strideList.size()]);
//        transaction.add(R.id.strideRecFragContainer, StrideRecFragment.newInstance(strideRecs));
//        transaction.commit();

    }

    public JsonObjectRequest getWeather() {
        String url = "https://api.openweathermap.org/data/2.5/weather?q=irvine&appid=" + API_KEY;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject main = response.getJSONObject("main");
                    String temp = main.getString("temp");

                    double x = Double.parseDouble(temp);
                    double t = (x * 9 / 5) - 459.67;

                    temp_today = t; // in Fahrenheit
                    if (temp_today != 0)
                        weatherTextView.setText((int) temp_today + " °F");
                    else
                        weatherTextView.setText("");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("ERROR");
            }
        });
        mQueue.add(jsonObjectRequest);
        return jsonObjectRequest;
    }

    private void buildModel() {

        getDeviceLocation();
        getNumberOfStepsTakenToday();
        StrideDatabaseHelper.GetLast10Strides getLast10Strides = new StrideDatabaseHelper.GetLast10Strides(strideDatabaseHelper, new StrideDatabaseHelper.GetLast10Strides.GetLast10StridesListener() {
            @Override
            public void onStridesReceived(List<Stride> strides) {
                calculateModel(strides);
            }
        });
        getLast10Strides.execute();
    }

    private void calculateModel(List<Stride> strides) {


        if (strides.size() < 10) {
            // todo
            // if not enough strides, base on user settings

        } else {
            HashSet<Integer> walkDays = new HashSet<>();
            HashSet<Integer> runDays = new HashSet<>();

            double runDistance = 0;
            double walkDistance = 0;
            int runDuration = 0;
            int walkDuration = 0;
            long lastStrideTime = strides.get(0).getTime();

            for (int i = 0; i < strides.size(); i++) {
                Stride s = strides.get(i);
                if (s.getStrideType().equals("Run")) {
                    runDays.add(weekdayMap.get(s.getDay()));
                    runDistance = runDistance + s.getDistance();
                    runDuration = runDuration + s.getDuration();
                } else {
                    walkDays.add(weekdayMap.get(s.getDay()));
                    walkDistance = walkDistance + s.getDistance();
                    walkDuration = walkDuration + s.getDuration();
                }
            }

            double avgRunDistance = runDistance / 10;
            double avgWalkDistance = walkDistance / 10;
            int avgRunDuration = runDuration / 10;
            int avgWalkDuration = walkDuration / 10;


            personalModelSharedPrefs.setDaysOfRuns((new ArrayList<Integer>(runDays)).toString().replace("[", "").replace("]", ""));
            personalModelSharedPrefs.setDaysOfWalks((new ArrayList<Integer>(walkDays)).toString().replace("[", "").replace("]", ""));
            personalModelSharedPrefs.setDistanceOfRuns((float) avgRunDistance);
            personalModelSharedPrefs.setDistanceOfWalks((float) avgWalkDistance);
            personalModelSharedPrefs.setDurationOfRuns(avgRunDuration);
            personalModelSharedPrefs.setDurationOfWalks(avgWalkDuration);
            personalModelSharedPrefs.setLastStrideTime(lastStrideTime);

        }

        getRecommendations();
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Stride notifications";
            String description = "notifications for users to go on a Stride";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIF_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    /*
         get numbers of steps taken that day          // get move distance for that day (miles)

         get location
         get height, weight, age, and recommended numbers of steps per day    // change this to recommended distance per day (miles)
         which Stride you prefer that day (if it's both, always just prefer to run first then walk)
         average duration and distance for that type of Stride
         time since last Stride

         first determine type of Stride
         based on current day, you determine type of Stride, but
         if the user has already taken a run Stride, is already above the recommended step count, the Stride recommmended should be a walk
         whichever Stride is dominant for this recommendation, we should still give some options for the other type of Stride

         get duration for that Stride
         based on current interval time, determine how long this Stride could potentially be
         if interval time is < avg duration, base distance on interval time duration
         else base distance on avg duration for that type of Stride

         get distance for that Stride
         get average speed for that user, and use it and duration to calculate distance
//         multiply distance by 1.1 if user has gone on a Stride
//         based on the days they usually go on that Stride, if they didn't go on that Stride the last day, then
//         multiply distance by 1-(0.02)

         get distance for the other type of Stride by the same calculations

         return distance for that Stride and other Stride
 */
    private void getRecommendations() {
        long numStepsToday = personalModelSharedPrefs.getNumStepsTakenThisDay();
        double currLat = user_lat;
        double currLong = user_lng;
        double height = personalModelSharedPrefs.getHeight();
        double weight = personalModelSharedPrefs.getWeight();
        double age = personalModelSharedPrefs.getAge();
        int recommendedSteps = 10000; // baseline for recommended steps per day
        double bmi = 703.0 * weight / (height * height);
        if (bmi > 30) { // obese
            recommendedSteps = (int) (0.8 * recommendedSteps);
        } else if (bmi > 25) {
            recommendedSteps = (int) (0.9 * recommendedSteps);
        }
        if (age > 50) {
            recommendedSteps = (int) (0.75 * recommendedSteps);
        }

        boolean runToday = false;
        boolean walkToday = false;
        Calendar calendar = Calendar.getInstance();
        int today = calendar.get(Calendar.DAY_OF_WEEK);
        for (String i : personalModelSharedPrefs.getDaysOfRuns().split(", ")) {
            if (Integer.valueOf(i) == today) {
                runToday = true;
                break;
            }
        }
        for (String i : personalModelSharedPrefs.getDaysOfRuns().split(", ")) {
            if (Integer.valueOf(i) == today) {
                walkToday = true;
                break;
            }
        }

        String recommendedStride = "Walk";
        if (walkToday)
            recommendedStride = "Walk";
        if (runToday)
            recommendedStride = "Run";

        Calendar cal = Calendar.getInstance();
        int todayDate = cal.get(Calendar.DAY_OF_YEAR);
        cal.setTimeInMillis(personalModelSharedPrefs.getLastRunStrideTime());
        if (cal.get(Calendar.DAY_OF_YEAR) == todayDate || numStepsToday > recommendedSteps) {
            recommendedStride = "Walk";
        }

        float avgDistanceOfRun = personalModelSharedPrefs.getDistanceOfRuns();
        double avgDurationOfRun = personalModelSharedPrefs.getDurationOfRuns();
        float avgDistanceOfWalk = personalModelSharedPrefs.getDistanceOfWalks();
        double avgDurationOfWalk = personalModelSharedPrefs.getDurationOfWalks();

        // intervals of free time. if the user opens app not during these free intervals, default to 30 min
        ArrayList<ArrayList<Long>> notificationTimes = personalModelSharedPrefs.getNotificationTimes();
        Calendar rightNowCal = Calendar.getInstance();
        Long rightNow = rightNowCal.getTimeInMillis();
        long freeDuration = 30 * 60 * 1000; // default 30 minutes in milliseconds
        for (ArrayList<Long> interval : notificationTimes) {
            if (rightNow >= interval.get(0) && rightNow < interval.get(1)) {
                freeDuration = interval.get(1) - interval.get(0);
            }
        }

        // calculate distance for Run
        double durationOfRun = freeDuration/(60.0*1000); // in minutes
        if (freeDuration > avgDurationOfRun) { // if user has an hour but usually runs for 30 min, only recommend 30 min run
            durationOfRun = avgDurationOfRun;
        }
        double avgSpeedOfRun = avgDistanceOfRun / avgDurationOfRun;
        double distanceOfRun = avgSpeedOfRun * durationOfRun;


        // calculate distance for Walk
        double durationOfWalk = freeDuration/(60.0*1000);
        if (freeDuration > avgDurationOfWalk) {
            durationOfWalk = avgDurationOfWalk;
        }
        double avgSpeedOfWalk = avgDistanceOfWalk / avgDurationOfWalk;
        double distanceOfWalk = avgSpeedOfWalk * durationOfWalk;

        generateStrides(recommendedStride, distanceOfRun, distanceOfWalk);

    }

    private void getNumberOfStepsTakenToday() {
        Task<DataSet> StepsResponse =
                Fitness.getHistoryClient(this, GoogleSignIn.getLastSignedInAccount(this))
                        .readDailyTotalFromLocalDevice(DataType.TYPE_STEP_COUNT_DELTA).addOnSuccessListener(new OnSuccessListener<DataSet>() {
                    @Override
                    public void onSuccess(DataSet dataSet) {
                        long total = dataSet.isEmpty()
                                ? 0
                                : dataSet.getDataPoints().get(0).getValue(Field.FIELD_STEPS).asInt();

                        Log.i(TAG, "MY STEPS TODAY " + Long.toString(total));
                        personalModelSharedPrefs.setNumStepsTakenThisDay(total);
                    }
                });
    }
}
