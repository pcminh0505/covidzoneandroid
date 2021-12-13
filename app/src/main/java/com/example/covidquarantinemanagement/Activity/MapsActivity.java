package com.example.covidquarantinemanagement.Activity;

// Reference: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
// Github: https://github.com/googlemaps/android-samples/blob/c6a1b5ddb5fd69997815105ffec8eb1ba70d4d8a/tutorials/java/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private ActivityMapsBinding binding;

    // Setup Google Map
    private GoogleMap map;
    private CameraPosition cameraPosition;
    private static final String TAG = MapsActivity.class.getSimpleName();

    // Setup current location display
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 99;
    // A default location (Sydney, Australia) and default zoom to use when location permission is not granted.
    private final LatLng defaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    protected FusedLocationProviderClient fusedLocationProviderClient;
    private boolean locationPermissionGranted;
    private Location lastKnownLocation;

    // Keys for storing activity state.
    private static final String KEY_CAMERA_POSITION = "camera_position";
    private static final String KEY_LOCATION = "location";

    // Drawer
    private DrawerLayout mDrawerLayout;

    // Setup Firestore database
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Setup Firebase Authentication
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMapsBinding.inflate(getLayoutInflater());

        // Retrieve location and camera position from saved instance state.
        if (savedInstanceState != null) {
            lastKnownLocation = savedInstanceState.getParcelable(KEY_LOCATION);
            cameraPosition = savedInstanceState.getParcelable(KEY_CAMERA_POSITION);
        }

        // Retrieve the content view that renders the map.
        setContentView(binding.getRoot());

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawerLayout = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,mDrawerLayout,toolbar,R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Construct a FusedLocationProviderClient.
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Build the map.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mAuth = FirebaseAuth.getInstance();
        checkUserStatus();

        // Login Button
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);
        TextView logInSignUp = (TextView) headerView.findViewById(R.id.loginsignuptext);
        logInSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, LogInActivity.class);
                startActivityForResult(i,300);
                Toast.makeText(MapsActivity.this,"Clicked on Login button",Toast.LENGTH_LONG).show();
            }
        });

        // Add Button + Listener
        ImageButton addButton = (ImageButton) findViewById(R.id.add_site_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, RegisterZoneActivity.class);
//                ArrayList<String> existedList = Helper.createSymbolList(mCrypto);
//                i.putExtra("existedList",existedList);
                startActivityForResult(i,100);
            }
        });

        // Search Button + Listener
//        ImageButton searchButton = (ImageButton) findViewById(R.id.search_site_button);
//        searchButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent i = new Intent(MapsActivity.this, RegisterZoneActivity.class);
////                ArrayList<String> existedList = Helper.createSymbolList(mCrypto);
////                i.putExtra("existedList",existedList);
//                startActivityForResult(i,200);
//            }
//        });
    }

    private void checkUserStatus() {
        // Get current user
        mUser = mAuth.getCurrentUser();
        if (mUser != null) {
            updateLoginUI();
        }
    }

    private void updateLoginUI() {
        // User logged in
        String userPhone = mUser.getPhoneNumber();
        String userName =
        userPhone = userPhone.replaceAll("\\d(?=(?:\\D*\\d){4})", "*");

        // Edit navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // Edit header
        View headerView = navigationView.getHeaderView(0);

        TextView logInSignUp = (TextView) headerView.findViewById(R.id.loginsignuptext);
        logInSignUp.setVisibility(View.INVISIBLE);
        LinearLayout currentUserInfo = (LinearLayout) headerView.findViewById(R.id.current_user_info_box);
        TextView currentUserPhone = (TextView) headerView.findViewById(R.id.current_user_phone);
        TextView currentUserName = (TextView) headerView.findViewById(R.id.current_user_name);


        currentUserPhone.setText(userPhone);
        currentUserInfo.setVisibility(View.VISIBLE);

        // Edit menu
        Menu menuView = navigationView.getMenu();
        MenuItem logoutItem = menuView.findItem(R.id.nav_logout);
        logoutItem.setVisible(true);

        // Setup logout function
        logoutItem.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                mAuth.getInstance().signOut();
                finish();
                startActivity(getIntent());
                return true;
            }
        });
    }

    /**
     * Saves the state of the map when the activity is paused.
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (map != null) {
            outState.putParcelable(KEY_CAMERA_POSITION, map.getCameraPosition());
            outState.putParcelable(KEY_LOCATION, lastKnownLocation);
        }
        super.onSaveInstanceState(outState);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap map) {
        this.map = map;

        // Prompt the user for permission.
        getLocationPermission();

        // Turn on the My Location layer and the related control on the map.
        updateLocationUI();

        // Get the current location of the device and set the position of the map.
        getDeviceLocation();

        map.getUiSettings().setZoomControlsEnabled(true);

//        map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
//            @Override
//            public void onMapClick(LatLng latLng) {
//                // Move to RegisterZone Activity
//                Intent i = new Intent(MapsActivity.this, RegisterZoneActivity.class);
//                i.putExtra("latitude",latLng.latitude);
//                i.putExtra("longitude",latLng.longitude);
//                startActivity(i);
//            }
//        });
//
//        map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//            @Override
//            public boolean onMarkerClick(Marker marker) {
//                Toast.makeText(MapsActivity.this, marker.toString(), Toast.LENGTH_SHORT).show();
//                return false;
//            }
//        });
    }

    /**
     * Prompts the user for permission to use the device location.
     */
    private void getLocationPermission() {
        /*
         * Request location permission, so that we can get the location of the
         * device. The result of the permission request is handled by a callback,
         * onRequestPermissionsResult.
         */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            locationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }

    /**
     * Handles the result of the request for location permissions.
     */
    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        locationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                locationPermissionGranted = true;
            }
        }
        updateLocationUI();
    }

    /**
     * Updates the map's UI settings based on whether the user has granted location permission.
     */
    private void updateLocationUI() {
        if (map == null) {
            return;
        }
        try {
            if (locationPermissionGranted) {
                map.setMyLocationEnabled(true);
                map.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                map.setMyLocationEnabled(false);
                map.getUiSettings().setMyLocationButtonEnabled(false);
                lastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }

    private void getDeviceLocation() {
        /*
         * Get the best and most recent location of the device, which may be null in rare
         * cases when a location is not available.
         */
        try {
            if (locationPermissionGranted) {
                Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful()) {
                            // Set the map's camera position to the current location of the device.
                            lastKnownLocation = task.getResult();
                            if (lastKnownLocation != null) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                        new LatLng(lastKnownLocation.getLatitude(),
                                                lastKnownLocation.getLongitude()), DEFAULT_ZOOM));
                            }
                        } else {
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            map.moveCamera(CameraUpdateFactory
                                    .newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            map.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage(), e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Add new zone to database
        if (requestCode == 100) {
            if (resultCode == RESULT_OK) {
                double newSiteLatitude = data.getDoubleExtra("latitude",0.00);
                double newSiteLongitude = data.getDoubleExtra("longitude",0.00);

                LatLng newSite = new LatLng(newSiteLatitude, newSiteLongitude);
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(newSite)
                        .icon(bitmapDescriptorFromVector(this,R.drawable.zone_marker));
                map.addMarker(markerOptions);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(newSite, 16));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(newSite, 15));
                Toast.makeText(MapsActivity.this, "New Site has been registered", Toast.LENGTH_LONG).show();
                }
            }
        // Move camera to searched zone
//        else if (requestCode == 200) {
//            if (resultCode == RESULT_OK) {
//
//            }
//        }
        // User Login successfully
        else if (requestCode == 300) {
            if (resultCode == RESULT_OK) {
                checkUserStatus();
            }
        }
    }
    private static BitmapDescriptor bitmapDescriptorFromVector(Context context, int vectorZoneId) {
        Drawable vectorDrawable = ContextCompat.getDrawable(context, vectorZoneId);
        vectorDrawable.setBounds(0,0,
                vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(vectorDrawable.getIntrinsicWidth(), vectorDrawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        vectorDrawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onBackPressed() {
        if (mDrawerLayout.isDrawerOpen(GravityCompat.START)) {
            mDrawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}