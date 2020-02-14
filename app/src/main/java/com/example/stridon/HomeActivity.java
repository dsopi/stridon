package com.example.stridon;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
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
        implements OnMapReadyCallback {

    private Button signOutButton;

    private GoogleSignInClient mGoogleSignInClient;

    private static final String TAG = HomeActivity.class.getSimpleName();

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        signOutButton = findViewById(R.id.signout);

        // TODO this signout button is just here for testing, eventually it will be moved
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        mGoogleSignInClient = GoogleSignIn.getClient(this, MyGoogleOptions.gso);

        Log.i("tag", "home activity account for " + GoogleSignIn.getLastSignedInAccount(this).getEmail());

        if (GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), MyGoogleOptions.fitnessOptions)) {
            Log.i("tag", "currently has google fit permissions");
        } else {
            Log.i("tag", "currently doesn't have google fit permissions");
        }

        //User Location
        if (savedInstanceState != null) {
            mLastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            mCameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        //Load up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mQueue = Volley.newRequestQueue(this);

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);


    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        //assign the google map to mMap
        mMap = googleMap;

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
                                getFourPoints();

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

    private void getFourPoints() {

        double s_dis = 1.0;

        s_dis = s_dis/2.0;
        double lat_dis = (0.3 + (Math.random() * .4)) * s_dis;
        double lng_dis = s_dis - lat_dis;
        if (Math.random() < .5){
            lat_dis = lat_dis * -1.0;
        }
        if (Math.random() < .5){
            lng_dis = lng_dis * -1.0;
        }

        ArrayList<Double> lat_array = new ArrayList<Double>();
        ArrayList<Double> lng_array = new ArrayList<Double>();

        lat_array.add(user_lat + lat_dis/69.0);
        lng_array.add(user_lng);

        lat_array.add(user_lat + lat_dis/69.0);
        lng_array.add(user_lng + lng_dis/59.0);

        lat_array.add(user_lat);
        lng_array.add(user_lng + lng_dis/59.0);

        getDirectionUrl(lat_array,lng_array);
    }

    private void getDirectionUrl(ArrayList<Double> lat_array, ArrayList<Double> lng_array){

        //Uses string building to make the url to pass to the volley
        String direction_url = "https://maps.googleapis.com/maps/api/directions/json?";
        direction_url = direction_url + "origin=" + user_lat + "," + user_lng + "&";
        direction_url = direction_url + "destination=" + user_lat + "," + user_lng + "&";
        direction_url = direction_url + "mode=walking&waypoints=";

        for(int i = 0; i < lat_array.size();i++){
            direction_url = direction_url + "via:" + lat_array.get(i).toString() + "%2C" + lng_array.get(i).toString();
            if(i < lat_array.size()-1){
                direction_url = direction_url + "%7C";
            }
        }
        direction_url = direction_url + "&key=" + getString(R.string.google_maps_key);

        System.out.println(direction_url);

        //Now that the url is built, will go make the polyline
        getPolyline(direction_url);
    }

    private void getPolyline(String url){
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
                            drawPolyline(encoded_line);

                        } catch (JSONException e) {
                            System.out.println("Error: Could not retrieve the Polyline from Direction Url");
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


    private void drawPolyline(String encoded){
        List<LatLng> points = PolyUtil.decode(encoded);
        Polyline trail = mMap.addPolyline(new PolylineOptions()
                .clickable(false)
                .addAll(points));

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
                        Intent loginIntent = new Intent(HomeActivity.this, LoginActivity.class);
                        loginIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                        startActivity(loginIntent);
                    }
                });
            }
        });
    }
}
