package com.vuhung.minichatapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.model.UserChat;

import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder>{
    private Context context;
    private List<UserChat> users;

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user_chat, parent, false);
        return new UserChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class UserChatViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout constraintLayout;
        ImageView imageProfile;
        TextView textName, textChat;

        public UserChatViewHolder(@NonNull View itemView) {
            super(itemView);
            constraintLayout = itemView.findViewById(R.id.layout_item_user_chat);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textChat = itemView.findViewById(R.id.textChat);
        }
    }
}
