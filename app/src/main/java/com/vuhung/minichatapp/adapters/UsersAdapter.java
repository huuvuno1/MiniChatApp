package com.vuhung.minichatapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.makeramen.roundedimageview.RoundedImageView;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.activity.ChatActivity;
import com.vuhung.minichatapp.activity.UserActivity;
import com.vuhung.minichatapp.model.User;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.UserViewHolder> {
    private Context mContext;
    private List<User> mListUser;

    public UsersAdapter(Context mContext, List<User> mListUser) {
        this.mContext = mContext;
        this.mListUser = mListUser;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user,parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
         final User user = mListUser.get(position);
        if(user == null) {
            return;
        }
        holder.imageProfile.setImageResource(user.getResourceId());
        holder.textName.setText(user.getName());
        holder.textEmail.setText(user.getEmail());

        holder.layoutItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onClickGoToDetail(user);
            }
        });
    }

    private void onClickGoToDetail(User user){
        Intent intent = new Intent(mContext, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        mContext.startActivity(intent);
    }
    public void release() {
        mContext = null;
    }
    @Override
    public int getItemCount() {
        if(mListUser != null) {
            return mListUser.size();
        }
        return 0;
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {

        private ConstraintLayout layoutItem;
        private ImageView imageProfile;
        private TextView textName, textEmail;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textEmail = itemView.findViewById(R.id.textEmail);
            layoutItem = itemView.findViewById(R.id.layout_item1);
        }
    }
}
