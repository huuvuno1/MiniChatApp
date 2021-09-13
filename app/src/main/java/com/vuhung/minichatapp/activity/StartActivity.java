package com.vuhung.minichatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.vuhung.minichatapp.R;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        new Handler().postDelayed(() -> {
            // fetch api, check cookie

            // tam fix cung
            boolean isLogin = false;
            if (isLogin) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }
            else {
                startActivity(new Intent(this, LoginActivity.class));
            }

            finish();
        }, 1000);
    }
}