package com.example.covidquarantinemanagement.Util;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.Toast;

import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.Model.User;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.UUID;

public class DatabaseHandler {
    public static void createZoneOnDatabase(FirebaseFirestore db, Context context,
                                            ProgressDialog pd, String zoneLeader,
                                            String zoneName, int zoneCapacity,
                                            String zoneId, String zoneLevel1Address,
                                            String zoneLevel2Address, String zoneLevel3Address,
                                            Double zoneLatitude, Double zoneLongitude) {

        // set title of progress bar
        pd.setTitle("Creating new zone on Firestore Database");

        // show progress when user press add button
        pd.show();

        // Create a new zone
        HashMap<String, Object> data = new HashMap<>();
        data.put("id", zoneId);

        // Put zone data into temp data HashMap
        // Strings
        data.put("zoneName", zoneName);
        data.put("zoneLeader", zoneLeader);
        data.put("zoneLevel1Address", zoneLevel1Address);
        data.put("zoneLevel2Address", zoneLevel2Address);
        data.put("zoneLevel3Address", zoneLevel3Address);
        // Doubles
        data.put("zoneLat", Double.toString(zoneLatitude));
        data.put("zoneLong", Double.toString(zoneLongitude));
        // Integers
        data.put("zoneMaxCapacity", Integer.toString(zoneCapacity));

        // Initialize blank array


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
//
//    public static void getAllzonesOnDatabase(FirebaseFirestore db, Context context, ProgressDialog pd, ArrayList<Zone> allzonesContainer) {
//        // set title of progress bar
//        pd.setTitle("Loading quarantined zones ...");
//
//        // show progress when user press add button
//        pd.show();
//
//        // Add a new document with a generated ID
//        db.collection("zones")
//                .get() // using get method to get data from fire store
//                .addOnCompleteListener(task -> {
//                    // dismiss progress action
//                    pd.dismiss();
//
//                    // loop through document and add into zones container
////                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
//////                        Zone model = new Zone(
//////                                doc.getString("id"),
//////                                doc.getString("zoneLeader"),
//////                                doc.getString("zoneType"),
//////                                doc.getString("zoneName"),
//////                                Double.parseDouble(Objects.requireNonNull(doc.getString("zoneLat"))),
//////                                Double.parseDouble(Objects.requireNonNull(doc.getString("zoneLong"))),
//////                                Integer.parseInt(Objects.requireNonNull(doc.getString("zoneMaxCapacity"))),
//////                                Double.parseDouble(Objects.requireNonNull(doc.getString("distanceToCurrentLocation"))),
//////                                Integer.parseInt(Objects.requireNonNull(doc.getString("peopleTestedNum"))),
//////                                Integer.parseInt(Objects.requireNonNull(doc.getString("peopleCurrentlyAtNum"))),
//////                                doc.getString("volunteersList")
//////                        );
////                        allzonesContainer.add(model);
////                    }
//
//                    // Toast failing message
//                    Toast.makeText(context, "Load zones successfully!", Toast.LENGTH_SHORT).show();
//                })
//                .addOnFailureListener(e -> {
//                    // dismiss progress action
//                    pd.dismiss();
//
//                    // Toast failing message
//                    Toast.makeText(context, "Load zones failed!", Toast.LENGTH_SHORT).show();
//                });
//    }
//
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
        data.put("userEmail", userPhone);

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
//    public static void verifyUserLogin(FirebaseFirestore db, Context context, ProgressDialog pd,
//                                       String userEmail, String userPassword,
//                                       ArrayList<Boolean> results,
//                                       ArrayList<User> data) {
//        // set title of progress bar
//        pd.setTitle("Verifying login ...");
//
//        // show progress when user press search button
//        pd.show();
//
//        // search from database
//        db.collection("users")
//                .get()
//                .addOnCompleteListener(task -> {
//                    // dismiss progress dialog
//                    pd.dismiss();
//
//                    // loop through document and add into modelList
//                    for (DocumentSnapshot doc : Objects.requireNonNull(task.getResult())) {
//                        if (Objects.requireNonNull(doc.getString("userEmail")).equals(userEmail)
//                                && Objects.requireNonNull(doc.getString("userPassword")).equals(userPassword)) {
//                            results.add(true);
////                            User userDataContainer = new User(
////                                    (String) doc.get("userId"),
////                                    (String) doc.get("userName"),
////                                    (String) doc.get("userPassword"),
////                                    (String) doc.get("userEmail"),
////                                    Integer.parseInt((String) Objects.requireNonNull(doc.get("userAge")))
////                            );
//                            System.out.println("USER DATA IS PRINTED HERE: " + userDataContainer.toString());
//                            data.add(userDataContainer);
//                            break;
//                        }
//                    }
//                })
//                .addOnFailureListener(e -> {
//                    pd.dismiss();
//                    results.add(false);
//                });
//        Toast.makeText(context, "Verified!", Toast.LENGTH_SHORT).show();
//    }
}
