package com.example.covidquarantinemanagement;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.covidquarantinemanagement.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

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

        // Add a marker in rmit and move the camera
        LatLng rmit = new LatLng(10.73, 106.69);
        mMap.addMarker(new MarkerOptions().position(rmit).title("RMIT Vietnam"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(rmit));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(rmit,15));
        mMap.getUiSettings().setZoomControlsEnabled(true);
//        mMap.setMapStyle()

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title("hihi")
                        .icon(BitmapDescriptorFactory.defaultMarker())
                );
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

    @SuppressLint("MissingPermission")
    private void startLocationUpdate() {
        mLocationRequest = new com.google.android.gms.location.LocationRequest();
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        fusedLocationClient.requestLocationUpdates(mLocationRequest, new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                onLocationChanged(locationResult.getLastLocation());
            }
        }, null);
    }

    private void onLocationChanged(Location lastLocation) {
        String message = "Updated location" + Double.toString((lastLocation.getLatitude())) + ". " + Double.toString(lastLocation.getLongitude());
        LatLng newLoc = new LatLng((lastLocation.getLatitude()), lastLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(newLoc).title("New Location"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(newLoc));
        Toast.makeText(MapsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

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
                        LatLng lastLocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.moveCamera((CameraUpdateFactory.newLatLng(lastLocation)));
                        mMap.addMarker(new MarkerOptions()
                                .position(lastLocation)
                                .title("My Location"));
                        Toast.makeText(MapsActivity.this, location.getLatitude() + " ", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}