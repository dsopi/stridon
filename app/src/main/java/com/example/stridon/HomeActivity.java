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
import androidx.core.app.JobIntentService;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity
        implements OnMapReadyCallback, StrideRecFragment.StrideRecListener {

    private static final String TAG = HomeActivity.class.getSimpleName();
    public static final String NOTIF_CHANNEL_ID = "Stride notification channel";

    private GoogleSignInClient mGoogleSignInClient;

    private FragmentManager fragmentManager = getSupportFragmentManager();

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
    private double newDistance = 1.0;
    private int pityCounter = 5;

    StrideRecFragment strideRecFragment = null;

    Button startStrideButton;
    TextView weatherTextView;

    private String API_KEY = "4843f8fbd4876cc07f77a0730a5302b1";
    public double temp_today;
    public JsonObjectRequest Weather;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
            case R.id.logoutOption:
                Log.i(TAG, "logout");
                signOut();
                return true;
            case R.id.storeStride:
                Log.i(TAG, "store stride");
                storeStride();
                return true;
            case R.id.retrieveStride:
                Log.i(TAG, "retrieve stride");
                retrieveStride();
                return true;
            case R.id.buildModel:
                Log.i(TAG, "build model");
                buildModel();
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
        updateLocationUI();
        getDeviceLocation();
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
                                user_lat = mLastKnownLocation.getLatitude();
                                user_lng = mLastKnownLocation.getLongitude();
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(user_lat, user_lng), DEFAULT_ZOOM));
                                generateStrides(1.0);

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

    private void getFourPoints(double distance) {
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

        getDirectionUrl(lat_array, lng_array);
    }

    private void getDirectionUrl(ArrayList<Double> lat_array, ArrayList<Double> lng_array) {

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
        getPolyline(direction_url);
    }

    private void getPolyline(String url) {
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

                            Stride newStride = new Stride(actualDistance, "Run", encoded_line);
                            strideList.add(newStride);

                            //draw the initial line
                            if (strideList.size() == 1) {
                                currStride = strideList.get(0);
//                                encodedLine = encoded_line;
                                drawPolyline(currStride.getEncodedPolyline());
                            }
                            if (strideList.size() >= 5) {
                                linkStrideListToRecFragment();
                            }


                        } catch (JSONException e) {
                            System.out.println("Error: Could not retrieve the Polyline from Direction Url");
                            pityCounter--;
                            if (pityCounter > 0) {
                                getFourPoints(newDistance);
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

    private void generateStrides(double distance) {
        newDistance = distance;
        strideList.clear();
        for (int i = 0; i < 5; i++) {
            getFourPoints(distance);
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

        strideIntent.putExtra("ENCODED", stride.getEncodedPolyline());

        Log.i(TAG, "go to Stride intent");
        startActivity(strideIntent);
    }

    private void storeStride() {

        Stride stride = new Stride(1, "run",
                "encoded polyline");
        StrideDatabaseHelper.StoreStrideTask storeStrideTask = new StrideDatabaseHelper.StoreStrideTask(strideDatabaseHelper);
        storeStrideTask.execute(stride);
    }

    private void retrieveStride() {

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
                        weatherTextView.setText((int)temp_today + " °F");
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
        Intent serviceIntent = new Intent(this, BuildModelService.class);
        BuildModelService.enqueueWork(this, serviceIntent);
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
}
