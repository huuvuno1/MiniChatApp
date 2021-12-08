package com.vuhung.minichatapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.adapters.UserChatAdapter;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.BaseResponse;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.model.UserChat;
import com.vuhung.minichatapp.socket.MySocket;
import com.vuhung.minichatapp.utils.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.socket.client.Socket;
import io.socket.emitter.Emitter;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "token_device";
    private AppCompatImageView signOut;
    private FloatingActionButton actionButton;
    private TextView txtName;
    private RecyclerView recyclerView;
    private UserChatAdapter userChatAdapter;
    List<UserChat> userChats;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getView();
        setListeners();

        //fillData();
        userChats = new ArrayList<>();
        userChatAdapter = new UserChatAdapter(this, userChats);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(itemDecoration);
        recyclerView.setAdapter(userChatAdapter);
        userChatAdapter.notifyDataSetChanged();
        recyclerView.scrollToPosition(userChats.size() - 1);

        startSocket();
    }

    @Override
    protected void onResume() {
        super.onResume();
        MySocket.getInstanceSocket().emit("fetch_user_chat");
        String token = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE).getString("token", "");
        MySocket.start(token);
    }

    private void startSocket() {
        // make sure singleton
        SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        String token = preferences.getString("token", "");
        MySocket.start(token);
        Socket socket = MySocket.getInstanceSocket();

        socket.on("my_info", data -> {
            if (data[0] == null || "null".equals(data[0]))
                return;
            User user = new Gson().fromJson(data[0].toString(), User.class);
            Constant.MY_USERNAME = user.getUsername();
            this.runOnUiThread(() -> {
                this.txtName.setText(user.getFullName());
            });
        });


        socket.on("fetch_user_chat", data -> {
            if (data[0] == null || "null".equals(data[0]))
                return;
            List<UserChat> users = new Gson().fromJson(data[0].toString(), new TypeToken<List<UserChat>>(){}.getType());
            userChats.clear();
            userChats.addAll(users);
            userChats.sort((u1, u2) -> u1.getTimeStamp().before(u2.getTimeStamp()) ? 1 : -1);
            runOnUiThread(() -> {
                this.userChatAdapter.notifyDataSetChanged();
            });
        });

        socket.on("update_content_user_chat", data -> {
            if (data[0] == null || "null".equals(data[0]))
                return;
            UserChat user = new Gson().fromJson(data[0].toString(), UserChat.class);
            boolean isExist = false;
            for (UserChat u : userChats) {
                if (u.getUsername().equals(user.getUsername())) {
                    isExist = true;
                    u.setContent(user.getContent());
                    u.setTimeStamp(user.getTimeStamp());
                    u.setOnline(user.isOnline());
                    break;
                }
            }
            if (!isExist) {
//                UserChat u = new UserChat();
//                u.setFullName(user.getFullName());
//                u.setUsername(user.getUsername());
//                u.setContent(user.getContent());
//                u.setTimeStamp(user.getTimeStamp());
                userChats.add(0, user);
            }

            userChats.sort((u1, u2) -> u1.getTimeStamp().before(u2.getTimeStamp()) ? 1 : -1);
            runOnUiThread(() -> {
                this.userChatAdapter.notifyDataSetChanged();
            });
        });

        socket.on("status_user", data -> {
            if (data[0] == null || "null".equals(data[0]))
                return;
            User user = new Gson().fromJson(data[0].toString(), User.class);
            userChats.forEach(u -> {
                if (u.getUsername().equals(user.getUsername()))
                    u.setOnline(user.isOnline());
            });
            this.runOnUiThread(() -> {
                this.userChatAdapter.notifyDataSetChanged();
            });
        });
    }

    private void getView() {
        signOut = findViewById(R.id.imageSignOut);
        actionButton = findViewById(R.id.fabNewChat);
        txtName = findViewById(R.id.textName);
        recyclerView = findViewById(R.id.userChatHistoryRecyclerView);
    }

    private void setListeners() {
        signOut.setOnClickListener(v-> {
            Intent intent = new Intent(this, LoginActivity.class);
            // clear old data
            SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
            String deviceToken = preferences.getString("device_token", "");
            preferences.edit().clear().commit();

            if (!"".equals(deviceToken))
                MySocket.getInstanceSocket().emit("logout", deviceToken);

            MySocket.stop();
            startActivity(intent);
            finish();
        });
        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        });
        findViewById(R.id.imageProfile).setOnClickListener(v -> {
            startActivity(new Intent(this, UpdateProfileActivity.class));
        });
    }

}