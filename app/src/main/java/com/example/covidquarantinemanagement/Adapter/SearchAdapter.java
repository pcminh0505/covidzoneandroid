package com.example.covidquarantinemanagement.Adapter;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.covidquarantinemanagement.Activity.MapsActivity;
import com.example.covidquarantinemanagement.Model.Zone;
import com.example.covidquarantinemanagement.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

// Reference: https://www.youtube.com/watch?v=9xpvAjirN2s
public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.SearchViewHolder> implements Filterable {

    private List<Zone> mListZones;
    private List<Zone> mListZonesOld;
    private Context mContext;
    private Activity mActivity;

    public SearchAdapter(Activity activity, List<Zone> mListZones) {
        this.mActivity = activity;
        this.mListZones = mListZones;
        this.mListZonesOld = mListZones;
    }


    @NonNull
    @Override
    public SearchViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_info_window, parent, false);
        return new SearchViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SearchViewHolder holder, int position) {
        Zone zone = mListZones.get(position);
        if (zone == null) {
            return;
        }

        String address = zone.getZoneStreetAddress()
                + " " + zone.getZoneLevel3Address()
                + " " + zone.getZoneLevel2Address()
                + " " + zone.getZoneLevel1Address();
        holder.zoneName.setText(zone.getZoneName());
        holder.zoneAddress.setText("Address: " + address);
        holder.zoneCapacity.setText("Capacity: "+String.valueOf(zone.getZoneCapacity()));

        holder.zoneLeader.setVisibility(View.GONE);
        holder.zoneContact.setVisibility(View.GONE);

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickGoToDetail(zone);
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

    public class SearchViewHolder extends RecyclerView.ViewHolder {

        private TextView zoneName;
        private TextView zoneAddress;
        private TextView zoneLeader;
        private TextView zoneContact;
        private TextView zoneCapacity;
        private LinearLayout layoutItem;
        public SearchViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item);
            zoneName = itemView.findViewById(R.id.zone_title);
            zoneAddress = itemView.findViewById(R.id.zone_address);
            zoneLeader = itemView.findViewById(R.id.zone_leader_name);
            zoneContact = itemView.findViewById(R.id.zone_leader_contact);
            zoneCapacity = itemView.findViewById(R.id.zone_capacity);

        }

    }
    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String strSearch = constraint.toString();
                if (strSearch.isEmpty()) {
                    mListZones = mListZonesOld;
                }
                else {
                    List<Zone> list = new ArrayList<>();
                    for (Zone z : mListZonesOld) {
                        if (z.getZoneName().toLowerCase().contains(strSearch.toLowerCase())) {
                            list.add(z);
                        }
                    }
                    mListZones = list;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = mListZones;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mListZones = (List<Zone>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}
