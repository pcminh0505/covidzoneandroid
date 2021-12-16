package com.example.covidquarantinemanagement.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidquarantinemanagement.Adapter.UserInfoAdapter;
import com.example.covidquarantinemanagement.Adapter.ZoneAdapter;
import com.example.covidquarantinemanagement.Model.User;
import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.Util.DatabaseHandler;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewZoneDetailActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private Button exportVolunteerListButton;
    private Button addDataButton;

    private String zoneId;


    private UserInfoAdapter userInfoAdapter ;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private ProgressDialog pd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_zone_detail);

        Intent i = getIntent();
        zoneId =  (String) i.getExtras().get("zoneId");

        // Init process dialog
        pd = new ProgressDialog(this);
        pd.setTitle("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        addDataButton = findViewById(R.id.add_test_data_button);

        addDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Show register dialog
                final Dialog dialog = new Dialog(ViewZoneDetailActivity.this);
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

                EditText sendDataEditText = dialog.findViewById(R.id.friend_registration);
                sendDataEditText.setHint("Enter the test data with format: Date - No of test people");
                Button btnRegister = dialog.findViewById(R.id.btn_register);

                btnRegister.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // Update the volunteer list
                        String data = sendDataEditText.getText().toString();
                        ArrayList<String> appendRequest = new ArrayList<>();
                        appendRequest.add(data);
                        // Submitted friend's number
                        if (!TextUtils.isEmpty(data)) {
                            // Suppose they enter the correct number
                            Toast.makeText(ViewZoneDetailActivity.this,"Please enter the data", Toast.LENGTH_SHORT).show();
                        }
                        // Push to zone data
                        DatabaseHandler.addVolunteer(db, pd, ViewZoneDetailActivity.this ,zoneId,appendRequest);
                        dialog.dismiss();
                    }
                });
            }
        });


        List<String> dataList = new ArrayList<>();
        List<User> userLists = new ArrayList<>();

        TextView hintText = findViewById(R.id.hint_text_user);

        if (dataList.size() == 0) {
            hintText.setVisibility(View.VISIBLE);
        }


        DatabaseHandler.getVolunteerList(db, pd, dataList, zoneId);

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (dataList != null) {
                for (String data :
                        dataList) {
                    if (data.startsWith("+")) {
                        User tmpUser = new User("","Guest",data);
                        userLists.add(tmpUser);
                    }
                    else {
                        ArrayList<String> userData = new ArrayList<>();
                        DatabaseHandler.getSingleUserOnDatabase(db, ViewZoneDetailActivity.this, data, userData);

                        new Handler(Looper.getMainLooper()).postDelayed(() -> {
                            if (userData != null) {
                                User tmpUser = new User(userData.get(2), userData.get(0), userData.get(1));
                                System.out.println(tmpUser);
                                userLists.add(tmpUser);

                            }
                            else {
                                Toast.makeText(ViewZoneDetailActivity.this, "Connection time out - Can't get user", Toast.LENGTH_SHORT).show();
                            }
                        }, 2000);
                    }
                }
                new Handler(Looper.getMainLooper()).postDelayed(() -> {
                    //  Run Recycleview
                    System.out.println(userLists);
                    recyclerView = findViewById(R.id.volunteer_info_recycleview);
                    LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
                    recyclerView.setLayoutManager(linearLayoutManager);

                    userInfoAdapter = new UserInfoAdapter(ViewZoneDetailActivity.this, ViewZoneDetailActivity.this,userLists);
                    recyclerView.setAdapter(userInfoAdapter);

                    RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
                    recyclerView.addItemDecoration(itemDecoration);
                    hintText.setVisibility(View.GONE);
                }, 2000);
            }
            else {
                Toast.makeText(ViewZoneDetailActivity.this, "Connection time out - Can't get data", Toast.LENGTH_SHORT).show();
            }
        }, 2000);




    }
}