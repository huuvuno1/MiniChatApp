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

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.activity.ChatActivity;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.model.UserChat;

import java.util.List;

public class UserChatAdapter extends RecyclerView.Adapter<UserChatAdapter.UserChatViewHolder>{
    private Context context;
    private List<UserChat> users;

    public UserChatAdapter(Context context, List<UserChat> users) {
        this.context = context;
        this.users = users;
    }

    @NonNull
    @Override
    public UserChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_container_user_chat, parent, false);
        return new UserChatViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserChatViewHolder holder, int position) {
        if (users == null || users.size() == 0)
            return;
        UserChat user = users.get(position);
        holder.textChat.setText(user.getContent());
        holder.textName.setText(user.getFullName());
        holder.textUsername.setText(user.getUsername());
        holder.layoutItem.setOnClickListener(v -> goToChat(user));
    }

    private void goToChat(UserChat user) {
        Intent intent = new Intent(context, ChatActivity.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("object_user", user);
        intent.putExtras(bundle);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class UserChatViewHolder extends RecyclerView.ViewHolder {
        ConstraintLayout layoutItem;
        ImageView imageProfile;
        TextView textName, textChat, textUsername;

        public UserChatViewHolder(@NonNull View itemView) {
            super(itemView);
            layoutItem = itemView.findViewById(R.id.layout_item_user_chat);
            imageProfile = itemView.findViewById(R.id.imageProfile);
            textName = itemView.findViewById(R.id.textName);
            textUsername = itemView.findViewById(R.id.text_username_hidden);
            textChat = itemView.findViewById(R.id.textChat);
        }
    }
}
