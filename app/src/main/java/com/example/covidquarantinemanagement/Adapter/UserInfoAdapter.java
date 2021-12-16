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

// Reference: https://www.youtube.com/watch?v=9xpvAjirN2s
public class UserInfoAdapter extends RecyclerView.Adapter<UserInfoAdapter.UserInfoViewHolder> {

    private List<User> users;
    private Context context;
    private Activity mActivity;

    public UserInfoAdapter(Activity activity,Context context,  List<User> users) {
        this.mActivity = activity;
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserInfoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_user_info, parent, false);
        return new UserInfoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserInfoViewHolder holder, int position) {
        User mUser = users.get(position);

        if (mUser == null) {
            holder.hintText.setVisibility(View.VISIBLE);
            return;
        }
        holder.userName.setText(mUser.getName());
        holder.userPhone.setText(mUser.getPhone());
    }

    @Override
    public int getItemCount() {
        if (users != null) {
            return users.size();
        }
        return 0;
    }

    public class UserInfoViewHolder extends RecyclerView.ViewHolder {

        private TextView userName;
        private TextView userPhone;
        private TextView hintText;

        public UserInfoViewHolder(@NonNull View itemView) {
            super(itemView);
            hintText = itemView.findViewById(R.id.hint_text_user);
            userName = itemView.findViewById(R.id.volunteer_name);
            userPhone = itemView.findViewById(R.id.volunteer_phone);
        }
    }
}
