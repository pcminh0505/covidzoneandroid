package com.example.covidquarantinemanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.covidquarantinemanagement.Adapter.ZoneAdapter;
import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.Util.DatabaseHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class ViewZonesActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ZoneAdapter zoneAdapter ;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog pd;

    private TextView hintText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_zones);

        Intent i = getIntent();
        String type =  (String) i.getExtras().get("type");

        // Init process dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        List<Zone> dataList = new ArrayList<>();
        hintText = findViewById(R.id.hint_text_zone);
        if (dataList.size() == 0) {
            hintText.setVisibility(View.VISIBLE);
        }

        if (type.equals("leader")) {
            getSupportActionBar().setTitle("Leader Zones");
            // Get leader zone
            DatabaseHandler.getLeaderZones(db, pd, dataList, mAuth.getUid());
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (dataList != null) {
                    // Run recycleview
                    recyclerView = findViewById(R.id.volunteer_recycleview);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    zoneAdapter = new ZoneAdapter(ViewZonesActivity.this, ViewZonesActivity.this,dataList, type);
                    recyclerView.setAdapter(zoneAdapter);

                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(itemDecoration);
                }
                else {
                    Toast.makeText(ViewZonesActivity.this, "Connection time out - Can't get data", Toast.LENGTH_SHORT).show();
                }
                hintText.setVisibility(View.GONE);
            }, 2000);
        }
        else {
            getSupportActionBar().setTitle("Volunteer Zones");
            // Get volunteer zone
            DatabaseHandler.getVolunteerZones(db, pd, dataList, mAuth.getUid());
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (dataList != null) {
                    // Run recycleview
                    recyclerView = findViewById(R.id.volunteer_recycleview);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    zoneAdapter = new ZoneAdapter(ViewZonesActivity.this, ViewZonesActivity.this,dataList, type);
                    recyclerView.setAdapter(zoneAdapter);

                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(itemDecoration);
                }
                else {
                    Toast.makeText(ViewZonesActivity.this, "Connection time out - Can't get data", Toast.LENGTH_SHORT).show();
                }
                hintText.setVisibility(View.GONE);
            }, 2000);
        }






    }
}