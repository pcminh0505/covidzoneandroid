package com.example.covidquarantinemanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;
import com.example.covidquarantinemanagement.R;

import android.os.Bundle;
import android.widget.Button;

public class RegisterZoneActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_zone);

        Button submitButton = (Button) findViewById(R.id.submitButton);


    }
}