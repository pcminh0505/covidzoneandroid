package com.example.covidquarantinemanagement.Adapter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidquarantinemanagement.Activity.MapsActivity;
import com.example.covidquarantinemanagement.Activity.ViewZoneDetailActivity;
import com.example.covidquarantinemanagement.Model.User;
import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.R;
import com.example.covidquarantinemanagement.Util.DatabaseHandler;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Reference: https://www.youtube.com/watch?v=9xpvAjirN2s
public class ZoneAdapter extends RecyclerView.Adapter<ZoneAdapter.ZoneViewHolder> {

    private List<Zone> mListZones;
    private Context context;
    private String mViewType;
    private Activity mActivity;
    private User zoneLeader = new User();

    public ZoneAdapter(Activity activity,Context context, List<Zone> mListZones, String type) {
        this.mActivity = activity;
        this.context = context;
        this.mListZones = mListZones;
        this.mViewType = type;
    }

    @NonNull
    @Override
    public ZoneViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_window, parent, false);
        return new ZoneViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ZoneViewHolder holder, int position) {
        Zone zone = mListZones.get(position);
        if (zone == null) {
            return;
        }

        ArrayList<String> zoneLeaderData = new ArrayList<>();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DatabaseHandler.getSingleUserOnDatabase(db,context,zone.getZoneLeader(),zoneLeaderData);


        String address = zone.getZoneStreetAddress()
                + " " + zone.getZoneLevel3Address()
                + " " + zone.getZoneLevel2Address()
                + " " + zone.getZoneLevel1Address();

        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            if (zoneLeaderData != null) {
                // Add to current user object
                zoneLeader.setId(zoneLeaderData.get(2));
                zoneLeader.setName(zoneLeaderData.get(0));
                zoneLeader.setPhone(zoneLeaderData.get(1));

                holder.zoneName.setText(zone.getZoneName());
                holder.zoneAddress.setText("Address: " + address);
                holder.zoneCapacity.setText("Capacity: "+String.valueOf(zone.getZoneCapacity()));


                holder.zoneLeader.setText("Leader: " + zoneLeaderData.get(0));
                holder.zoneContact.setText("Leader Contact: " + zoneLeaderData.get(1));
                holder.zoneLeader.setVisibility(View.VISIBLE);
                holder.zoneContact.setVisibility(View.VISIBLE);
            }
            else {
                Toast.makeText(context, "Connection time out - Can't get data", Toast.LENGTH_SHORT).show();
            }
        }, 2000);

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mViewType.equals("volunteer")) {
                    onClickGoToDetail(zone);
                }
                else {
                    Intent i = new Intent(mActivity.getApplicationContext(), ViewZoneDetailActivity.class);
                    i.putExtra("zoneId",zone.getZoneId());
                    mActivity.startActivity(i);
                }

//
            }
        });
    }

    private void onClickGoToDetail(Zone zone) {
        Intent i = new Intent(mActivity.getApplicationContext(), MapsActivity.class);
        i.putExtra("latitude", zone.getZoneLatitude());
        i.putExtra("longitude", zone.getZoneLongitude());
        mActivity.setResult(RESULT_OK, i);
        mActivity.finish();
    }

    @Override
    public int getItemCount() {
        if (mListZones != null) {
            return mListZones.size();
        }
        return 0;
    }

    public class ZoneViewHolder extends RecyclerView.ViewHolder {

        private TextView zoneName;
        private TextView zoneAddress;
        private TextView zoneLeader;
        private TextView zoneContact;
        private TextView zoneCapacity;
        private LinearLayout layoutItem;

        public ZoneViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            zoneName = itemView.findViewById(R.id.zone_title);
            zoneAddress = itemView.findViewById(R.id.zone_address);
            zoneLeader = itemView.findViewById(R.id.zone_leader_name);
            zoneContact = itemView.findViewById(R.id.zone_leader_contact);
            zoneCapacity = itemView.findViewById(R.id.zone_capacity);
        }
    }
}