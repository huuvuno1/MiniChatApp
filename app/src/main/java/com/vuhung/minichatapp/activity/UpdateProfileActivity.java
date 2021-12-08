package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.socket.MySocket;

import java.util.HashMap;
import java.util.Map;

import io.socket.client.Socket;

public class UpdateProfileActivity extends AppCompatActivity {

    EditText email, fullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);
        getViews();
        setListeners();
    }

    private void getViews() {
        email = findViewById(R.id.inputName);
        fullname = findViewById(R.id.inputEmail);
    }

    private void setListeners() {
        findViewById(R.id.buttonback).setOnClickListener(v -> {
            onBackPressed();
        });
        Socket socket = MySocket.getInstanceSocket();
        socket.emit("fetch_my_profile");

        socket.on("fetch_my_profile", data -> {
            User user = new Gson().fromJson(data[0].toString(), User.class);
            runOnUiThread(() -> {
                email.setText(user.getEmail());
                fullname.setText(user.getFullName());
            });
        });

        findViewById(R.id.buttonUpdate).setOnClickListener(v ->  {
            Map<String, String> data = new HashMap<>();
            data.put("email", email.getText().toString());
            data.put("fullname", fullname.getText().toString());
            socket.emit("update_profile",new Gson().toJson(data));
            Toast.makeText(getBaseContext(), "update profile successful", Toast.LENGTH_SHORT).show();
        });
    }
}