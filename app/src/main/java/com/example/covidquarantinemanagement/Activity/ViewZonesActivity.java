package com.example.covidquarantinemanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.example.covidquarantinemanagement.R;

public class ViewZonesActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_zones);

        Intent i = getIntent();
        String type =  (String) i.getExtras().get("type");

        if (type.equals("leader")) {
            getSupportActionBar().setTitle("Leader Zones");
        }
        else {
            getSupportActionBar().setTitle("Volunteer Zones");
        }
    }
}