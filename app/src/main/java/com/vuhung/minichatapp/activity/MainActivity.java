package com.vuhung.minichatapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.BaseResponse;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.socket.MySocket;
import com.vuhung.minichatapp.utils.Constant;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "token_device";
    private AppCompatImageView signOut;
    private FloatingActionButton actionButton;
    TextView txtName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getView();
        setListeners();
        startSocket();
        //fillData();
    }

    private void startSocket() {
        // make sure singleton
        SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        String token = preferences.getString("token", "");
        MySocket.start(token);
        MySocket.getInstanceSocket().on("my_info", data -> {
            User user = new Gson().fromJson(data[0].toString(), User.class);
            Constant.MY_USERNAME = user.getUsername();
            this.runOnUiThread(() -> {
                this.txtName.setText(user.getFullName());
            });
        });
    }

    private void getView() {
        signOut = findViewById(R.id.imageSignOut);
        actionButton = findViewById(R.id.fabNewChat);
        txtName = findViewById(R.id.textName);
    }

    private void fillData() {
        SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        String full_name = preferences.getString("full_name", "");
        if ("".equals(full_name)) {
            String token = preferences.getString("token", "");
            Map<String, String> body = new HashMap<>();
            body.put("token", token);
            ApiService.apiService.fetchProfile(body).enqueue(new Callback<BaseResponse<User>>() {
                @Override
                public void onResponse(Call<BaseResponse<User>> call, Response<BaseResponse<User>> response) {
                    String name = response.body().getData().getFullName();
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("full_name", name).commit();
                    txtName.setText(name);
                }

                @Override
                public void onFailure(Call<BaseResponse<User>> call, Throwable t) {
                    Log.e(TAG, "fetch profile api not call");
                }
            });
        }
        else
            txtName.setText(full_name);
    }

    private void setListeners() {
        signOut.setOnClickListener(v-> {
            Intent intent = new Intent(this, LoginActivity.class);
            // clear old data
            SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
            preferences.edit().clear().commit();
            MySocket.stop();
            startActivity(intent);
            finish();
        });
        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
        });
    }

    private void sendRegTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }
}