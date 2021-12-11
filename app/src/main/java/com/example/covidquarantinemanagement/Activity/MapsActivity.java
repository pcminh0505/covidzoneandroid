package com.example.covidquarantinemanagement.Activity;
import com.example.covidquarantinemanagement.Util.Helper;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.databinding.ActivityMapsBinding;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private static final int MY_PERMISSION_REQUEST_LOCATION = 99;
    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    protected FusedLocationProviderClient fusedLocationClient;
    protected com.google.android.gms.location.LocationRequest mLocationRequest;

    private static final long UPDATE_INTERVAL = 10*1000; // 10s
    private static final long FASTEST_INTERVAL = 2*1000; // 2s

    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final CollectionReference zones = db.collection("zones");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    public void onMapReady(GoogleMap googleMap) {
        requestPermission();
        mMap = googleMap;
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(MapsActivity.this);


        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                // Move to RegisterZone Activity
                Intent i = new Intent(MapsActivity.this, RegisterZoneActivity.class);
                i.putExtra("latitude",latLng.latitude);
                i.putExtra("longitude",latLng.longitude);
                startActivity(i);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                Toast.makeText(MapsActivity.this, marker.toString(), Toast.LENGTH_SHORT).show();
                return false;
            }
        });

        startLocationUpdate();

    }

    @Override
    protected void onResume() {
        super.onResume();
//        new GetZones();

    }

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        fusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                super.onLocationResult(locationResult);
                Location location = locationResult.getLastLocation();
                Toast.makeText(MapsActivity.this, "(" + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
            }
        }, null);
    }

//    private void onLocationChanged(Location lastLocation) {
//        String message = "Updated location" + Double.toString((lastLocation.getLatitude())) + ". " + Double.toString(lastLocation.getLongitude());
//        LatLng newLoc = new LatLng((lastLocation.getLatitude()), lastLocation.getLongitude());
//        mMap.addMarker(new MarkerOptions().position(newLoc).title("New Location"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
//        Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
//    }

    private void requestPermission() {
        ActivityCompat.requestPermissions(MapsActivity.this, new String[] {
                Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_LOCATION);
    }

    @SuppressLint("MissingPermission")
    public void getPosition(View view) {
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions()
                                .position(latLng)
                                .title("My Location")
                                .icon(Helper.bitmapDescriptorFromVector(MapsActivity.this, R.drawable.zone_marker)));
                        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,15));
                        Toast.makeText(MapsActivity.this, "(" + location.getLatitude() + ", " + location.getLongitude(), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}