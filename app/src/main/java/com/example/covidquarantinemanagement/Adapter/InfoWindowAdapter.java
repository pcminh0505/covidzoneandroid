package com.example.covidquarantinemanagement.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.covidquarantinemanagement.R;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class InfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private Context context;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public InfoWindowAdapter(Context context) {
        this.context = context;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        FirebaseUser mUser = mAuth.getCurrentUser();
        View view = LayoutInflater.from(context).inflate(R.layout.layout_info_window,null);

        TextView tvTitle = (TextView) view.findViewById(R.id.zone_title);
        TextView tvAddress = (TextView) view.findViewById(R.id.zone_address);
        TextView tvLeaderName = (TextView) view.findViewById(R.id.zone_leader_name);
        TextView tvContact = (TextView) view.findViewById(R.id.zone_leader_contact);
        TextView tvCapacity = (TextView) view.findViewById(R.id.zone_capacity);
        TextView tvId = (TextView) view.findViewById(R.id.zone_id);

        TextView tvLoginReminder = (TextView) view.findViewById(R.id.zone_login_reminder);
        if (mUser != null) {
            tvLoginReminder.setVisibility(View.GONE);
        }

        String snippet = marker.getSnippet();
        String[] data = snippet.split("\\|");

        // data[0]: Address
        // data[1]: Capacity
        // data[2]: Leader Name
        // data[3]: Leader Contact
        // data[4]: Zone ID

        tvTitle.setText(marker.getTitle());
        tvAddress.setText(data[0]);
        tvCapacity.setText("Capacity: " + data[1]);
        tvLeaderName.setText("Leader: "+ data[2]);
        tvContact.setText("Contact: "+ data[3]);

//        new Handler(Looper.getMainLooper()).postDelayed(() -> {
//            if (zoneLeader.get(0) != null || zoneLeader.get(1) != null) {
//                tvLeaderName.setText("Leader: "+ zoneLeader.get(0));
//                tvContact.setText("Contact: "+ zoneLeader.get(1));
//            }
//        }, 2000);

        return view;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
