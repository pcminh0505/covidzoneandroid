package com.example.covidquarantinemanagement.Util;

import static android.content.ContentValues.TAG;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.covidquarantinemanagement.Activity.RegisterZoneActivity;
import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.Model.User;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class DatabaseHandler {
    public static void createZoneOnDatabase(FirebaseFirestore db, Context context,
                                            ProgressDialog pd, String zoneLeader,
                                            String zoneName, int zoneCapacity,
                                            String zoneId, String zoneLevel1Address,
                                            String zoneLevel2Address, String zoneLevel3Address,
                                            String zoneStreetAddress,
                                            Double zoneLatitude, Double zoneLongitude) {

        // set title of progress bar
        pd.setTitle("Creating new zone on Firestore Database");

        // show progress when user press add button
        pd.show();

        // Create a new zone
        HashMap<String, Object> data = new HashMap<>();
        data.put("zoneId", zoneId);

        // Put zone data into temp data HashMap
        // Strings
        data.put("zoneName", zoneName);
        data.put("zoneLeader", zoneLeader);
        data.put("zoneLevel1Address", zoneLevel1Address);
        data.put("zoneLevel2Address", zoneLevel2Address);
        data.put("zoneLevel3Address", zoneLevel3Address);
        data.put("zoneStreetAddress", zoneStreetAddress);
        // Doubles
        data.put("zoneLatitude", Double.toString(zoneLatitude));
        data.put("zoneLongitude", Double.toString(zoneLongitude));
        // Integers
        data.put("zoneCapacity", Integer.toString(zoneCapacity));

        // Initialize (string) array
        String zoneCurrentVolunteer = "";
        String zoneTestData = "";
        data.put("zoneCurrentVolunteer", zoneCurrentVolunteer);
        data.put("zoneTestData", zoneTestData);

        // Add a new document with a generated ID
        db.collection("zones").document(zoneId).set(data)
                .addOnSuccessListener(documentReference -> {
                    // this will be called when data added successfully
                    pd.dismiss();
                    Toast.makeText(context, "Uploaded zone successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // this will be called when there is an error while adding
                    pd.dismiss();
                    Toast.makeText(context, "Uploaded zone failed!", Toast.LENGTH_SHORT).show();
                });
    }

    public static void getZonesOnDatabase(FirebaseFirestore db, ProgressDialog pd, ArrayList<Zone> zonesContainer) {
        // Set title of progress bar
        pd.setTitle("Loading zones ...");

        // Show progress when user press add button
        pd.show();

        // Add a new document with a generated ID
        db.collection("zones")
                .get() // Get data from Firestore
                .addOnCompleteListener(task -> {
                    // Dismiss progress action
                    pd.dismiss();

                    // Loop through document and add into zones container
                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        Zone model = new Zone(
                                doc.getString("zoneId"),
                                doc.getString("zoneLeader"),
                                doc.getString("zoneName"),
                                Integer.parseInt(doc.getString("zoneCapacity")),
                                doc.getString("zoneLevel1Address"),
                                doc.getString("zoneLevel2Address"),
                                doc.getString("zoneLevel3Address"),
                                doc.getString("zoneStreetAddress"),
                                Double.parseDouble(doc.getString("zoneLatitude")),
                                Double.parseDouble(doc.getString("zoneLongitude")),
                                doc.getString("zoneCurrentVolunteers"),
                                doc.getString("zoneTestData")
                        );
                        zonesContainer.add(model);
                    }
                })
                .addOnFailureListener(e -> {
                    // dismiss progress action
                    pd.dismiss();
                });
    }

    public static void createUserOnDatabase(FirebaseFirestore db, Context context, ProgressDialog pd,
                                            String userId,
                                            String userName,
                                            String userPhone) {

        // show progress when user press add button
        pd.show();

        // Create a new user
        HashMap<String, Object> data = new HashMap<>();

        // put zone data into temp data HashMap
        data.put("userId", userId);
        data.put("userName", userName);
        data.put("userPhone", userPhone);

        // Add a new document with a generated ID
        db.collection("users").document(userId).set(data)
                .addOnSuccessListener(documentReference -> {
                    // this will be called when data added successfully
                    pd.dismiss();
                    Toast.makeText(context, "Signed up successfully!", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // this will be called when there is an error while adding
                    pd.dismiss();
                    Toast.makeText(context, "Signed up failed!", Toast.LENGTH_SHORT).show();
                });
    }
//
    public static void getSingleUserOnDatabase(FirebaseFirestore db, Context context, String uid, ArrayList<String> currentUser) {
        DocumentReference docRef = db.collection("users").document(uid);


        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    // Document found in the offline cache
                    DocumentSnapshot document = task.getResult();
                    String dtbUserName = document.getString("userName");
                    String dtbUserPhone = document.getString("userPhone");

                    if (dtbUserName == null || dtbUserPhone == null) {
                        Toast.makeText(context, "User has not been registered! Please try again", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // Hide middle phone number
                        dtbUserPhone = dtbUserPhone.replaceAll("\\d(?=(?:\\D*\\d){4})", "*");
                        currentUser.add(dtbUserName);
                        currentUser.add(dtbUserPhone);
                    }
//                    tvName.setText(dtbUserName);
//                    tvPhone.setText(dtbUserPhone);
                } else {
                    Log.d(TAG, "Cached get failed: ", task.getException());
                }
            }
        });
    }

    public static void checkRegisteredUser(FirebaseFirestore db, ProgressDialog pd, String phone, ArrayList<Integer> isRegistered ) {
        // set title of progress bar
        pd.setTitle("Checking phone number...");

        // show progress when user press search button
        pd.show();

        // search from database
        db.collection("users")
                .get()
                .addOnCompleteListener(task -> {
                    // dismiss progress dialog
                    pd.dismiss();

                    // loop through document and add into modelList
                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
                        if (Objects.requireNonNull(doc.getString("userPhone")).equals(phone)) {
                            isRegistered.add(0);
                            break;
                        }
                    }
                    isRegistered.add(1);
                })
                .addOnFailureListener(e -> {
                    pd.dismiss();
                });
    }
}
