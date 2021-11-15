package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.adapters.UsersAdapter;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.socket.MySocket;

import java.util.ArrayList;
import java.util.List;

import io.socket.client.Socket;


public class UserActivity extends AppCompatActivity {
    private List<User> list;
    private AppCompatImageView imageView;
    private RecyclerView rcvData;
    private UsersAdapter usersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        getView();
        setListeners();
        bindDataToView();
    }

    private void bindDataToView() {
        list = new ArrayList<>();
        usersAdapter = new UsersAdapter(this, list);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvData.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvData.addItemDecoration(itemDecoration);
        rcvData.setAdapter(usersAdapter);
        getListUser();
    }

    private void getView() {
        imageView = findViewById(R.id.imageBack1);
        rcvData = findViewById(R.id.usersRecyclerView);
    }

    private void getListUser() {
        Socket socket = MySocket.getInstanceSocket();
        socket.emit("fetch_all_user");
        socket.on("fetch_all_user", data -> {
            Log.e("test", data[0].toString());
            Gson gson = new Gson();
            List<User> users = gson.fromJson(data[0].toString(), new TypeToken<List<User>>(){}.getType());
            list.addAll(users);
            this.runOnUiThread(() -> {
                this.usersAdapter.notifyDataSetChanged();
            });
        });

        socket.on("status_user", data -> {
            if (data[0] == null || "null".equals(data[0]))
                return;
            User user = new Gson().fromJson(data[0].toString(), User.class);
            list.forEach(u -> {
                if (u.getUsername().equals(user.getUsername()))
                    u.setOnline(user.isOnline());
            });
            this.runOnUiThread(() -> {
                this.usersAdapter.notifyDataSetChanged();
            });
        });
    }


    private void setListeners() {
        imageView.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(usersAdapter != null) {
            usersAdapter.release();
        }
    }
}