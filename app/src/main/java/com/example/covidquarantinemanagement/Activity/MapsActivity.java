package com.example.covidquarantinemanagement.Activity;

// Reference: https://developers.google.com/maps/documentation/android-sdk/current-place-tutorial
// Github: https://github.com/googlemaps/android-samples/blob/c6a1b5ddb5fd69997815105ffec8eb1ba70d4d8a/tutorials/java/CurrentPlaceDetailsOnMap/app/src/main/java/com/example/currentplacedetailsonmap/MapsActivityCurrentPlace.java

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
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

import com.example.covidquarantinemanagement.Adapter.InfoWindowAdapter;
import com.example.covidquarantinemanagement.Model.User;
import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.Util.DatabaseHandler;
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
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

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

    // Current Zones on Maps
    private ArrayList<Zone> zones = new ArrayList<>();
    private User currentUser = new User();
    private ProgressDialog pd;

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

        // Init process dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        DatabaseHandler.getZonesOnDatabase(db,pd,zones);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (zones != null) {
                for (Zone z : zones) {
                    ArrayList<String> zoneLeader = new ArrayList<>();
                    DatabaseHandler.getSingleUserOnDatabase(db, MapsActivity.this, z.getZoneLeader(), zoneLeader);

                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        if (zoneLeader != null) {
                            LatLng zLatLng = new LatLng(z.getZoneLatitude(),z.getZoneLongitude());
                            MarkerOptions markerOptions = new MarkerOptions()
                                    .position(zLatLng)
                                    .icon(bitmapDescriptorFromVector(this,R.drawable.zone_marker))
                                    .title(z.getZoneName())
                                    .snippet(z.getZoneStreetAddress() + " " + z.getZoneLevel3Address() + " "
                                            + z.getZoneLevel2Address() + " " + z.getZoneLevel1Address() + "|"
                                            + z.getZoneCapacity()
                                            + "|" + zoneLeader.get(0) + "|" + zoneLeader.get(1)
                                            + "|" + z.getZoneId());
                            map.addMarker(markerOptions);
                        }
                        else {
                            Toast.makeText(MapsActivity.this, "Connection time out - Can't get user", Toast.LENGTH_SHORT).show();
                        }
                    }, 2000);
                }
            }
            else {
                Toast.makeText(MapsActivity.this, "Connection time out - Can't get zone", Toast.LENGTH_SHORT).show();
            }
        }, 2000);
        System.out.println(zones);


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
            }
        });

        // Add Button + Listener
        ImageButton addButton = (ImageButton) findViewById(R.id.add_site_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mUser != null) {
                    Intent i = new Intent(MapsActivity.this, RegisterZoneActivity.class);
//                ArrayList<String> existedList = Helper.createSymbolList(mCrypto);
//                i.putExtra("existedList",existedList);
                    startActivityForResult(i,100);
                }
                else {
                    Toast.makeText(MapsActivity.this, "Please login to create a new zone", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Search Button + Listener
        ImageButton searchButton = (ImageButton) findViewById(R.id.search_site_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MapsActivity.this, SearchActivity.class);
                System.out.println(zones);
                i.putExtra("zonesList",zones);
                startActivityForResult(i,200);
            }
        });
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
        String userId = mUser.getUid();

        // Edit navigation view
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        // Edit header
        View headerView = navigationView.getHeaderView(0);

        TextView logInSignUp = (TextView) headerView.findViewById(R.id.loginsignuptext);
        logInSignUp.setVisibility(View.INVISIBLE);
        LinearLayout currentUserInfo = (LinearLayout) headerView.findViewById(R.id.current_user_info_box);
        TextView currentUserPhone = (TextView) headerView.findViewById(R.id.current_user_phone);
        TextView currentUserName = (TextView) headerView.findViewById(R.id.current_user_name);

        ArrayList<String> tempUser = new ArrayList<>();
        DatabaseHandler.getSingleUserOnDatabase(db,MapsActivity.this,userId,tempUser);
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (tempUser != null) {
                currentUserName.setText(tempUser.get(0));
                // Hide middle phone number
                currentUserPhone.setText(tempUser.get(1).replaceAll("\\d(?=(?:\\D*\\d){4})", "*"));
                // Add to current user object
                currentUser.setId(tempUser.get(2));
                currentUser.setName(tempUser.get(0));
                currentUser.setPhone(tempUser.get(1));
            }
            else {
                Toast.makeText(MapsActivity.this, "Connection time out - Can't get data", Toast.LENGTH_SHORT).show();
            }
        }, 2000);

        currentUserInfo.setVisibility(View.VISIBLE);

        // Edit menu
        Menu menuView = navigationView.getMenu();
        MenuItem logoutItem = menuView.findItem(R.id.nav_logout);
        MenuItem adminPanel = menuView.findItem(R.id.nav_admin);
        MenuItem volunteerList = menuView.findItem(R.id.nav_volunteer);
        MenuItem leaderList = menuView.findItem(R.id.nav_leader);

        // Set logout button visible
        logoutItem.setVisible(true);


        // Set admin to be visible to admin
        if (userId.equals("Qx9BmJdK1nhTeX2ebjGPPGQHDEG2")) {
            adminPanel.setVisible(true);
        }


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

        volunteerList.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(MapsActivity.this , ViewZonesActivity.class);
                i.putExtra("type","volunteer");
                startActivity(i);
                return true;
            }
        });

        leaderList.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent i = new Intent(MapsActivity.this , ViewZonesActivity.class);
                i.putExtra("type","leader");
                startActivity(i);
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

        map.setInfoWindowAdapter(new InfoWindowAdapter(MapsActivity.this));

        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                System.out.println("Click roi neeeeeeeeeeee");

                String snippet = marker.getSnippet();
                String[] data = snippet.split("\\|");
                // data[3]: Leader Contact
                // data[4] = zoneID

                System.out.println(data[3]);
//                System.out.println(mUser.getPhoneNumber());

                // Not login --> Disable info window touch
                if (mUser == null) {
                    Toast.makeText(MapsActivity.this, "You need to login to register for volunteer", Toast.LENGTH_SHORT).show();
                }
                // Current user (leader) click on his zone --> Dismiss registration
                else if (data[3].equals(mUser.getPhoneNumber())) {
                    Toast.makeText(MapsActivity.this, "You are the leader of this zone!", Toast.LENGTH_SHORT).show();
                }
                else {
                    // Show register dialog
                    final Dialog dialog = new Dialog(MapsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.layout_register_dialog);

                    Window window = dialog.getWindow();
                    if (window == null) {
                        return;
                    }

                    window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
                    window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    WindowManager.LayoutParams windowAttributes = window.getAttributes();
                    windowAttributes.gravity = Gravity.BOTTOM;
                    dialog.setCancelable(true);

                    EditText friendRegistration = dialog.findViewById(R.id.friend_registration);
                    Button btnCancel = dialog.findViewById(R.id.btn_cancel);
                    Button btnRegister = dialog.findViewById(R.id.btn_register);

                    btnRegister.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // Update the volunteer list
                            String friendNumber = friendRegistration.getText().toString();
                            ArrayList<String> appendRequest = new ArrayList<>();
                            appendRequest.add(mUser.getPhoneNumber());
                            // Submitted friend's number
                            if (!TextUtils.isEmpty(friendNumber)) {
                                // Suppose they enter the correct number
                                appendRequest.add(friendNumber);
                            }
                            // Push to volunteerList
                            DatabaseHandler.addVolunteer(db, pd, MapsActivity.this ,data[4],appendRequest);
                            dialog.dismiss();
                        }
                    });

                    btnCancel.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });

                    dialog.show();
                }
            }
        });
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

                DatabaseHandler.getZonesOnDatabase(db,pd,zones);
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    for (Zone z : zones) {
                        LatLng zLatLng = new LatLng(z.getZoneLatitude(),z.getZoneLongitude());
                        MarkerOptions markerOptions = new MarkerOptions()
                                .position(zLatLng)
                                .icon(bitmapDescriptorFromVector(this,R.drawable.zone_marker));
                        map.addMarker(markerOptions);
                    }
                }, 2000);

                LatLng newSite = new LatLng(newSiteLatitude, newSiteLongitude);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(newSite, 16));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(newSite, 15));
                Toast.makeText(MapsActivity.this, "New Site has been registered", Toast.LENGTH_LONG).show();
                }
            }
//         Move camera to searched zone
        else if (requestCode == 200) {
            if (resultCode == RESULT_OK) {
                double selectedLatitude = data.getDoubleExtra("latitude",0.00);
                double selectedLongitude = data.getDoubleExtra("longitude",0.00);
                LatLng selectedZone = new LatLng(selectedLatitude, selectedLongitude);
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(selectedZone, 16));
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(selectedZone, 15));
            }
        }
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