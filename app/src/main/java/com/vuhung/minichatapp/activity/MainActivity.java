package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.vuhung.minichatapp.R;

public class MainActivity extends AppCompatActivity {

    private AppCompatImageView signOut;
    private FloatingActionButton actionButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        signOut = findViewById(R.id.imageSignOut);
        actionButton = findViewById(R.id.fabNewChat);
        setListeners();
    }

    private void setListeners() {
        signOut.setOnClickListener(v-> SignOut());
        actionButton.setOnClickListener(v -> {
            Intent intent = new Intent(this, UserActivity.class);
            startActivity(intent);
            finish();
        });
    }
    private void showToast(String message) {
        Toast.makeText(getApplicationContext(),message,Toast.LENGTH_SHORT).show();
    }

    private void SignOut() {
        showToast("Signing out . . .");
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}