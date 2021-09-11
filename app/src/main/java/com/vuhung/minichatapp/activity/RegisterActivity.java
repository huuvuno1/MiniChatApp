package com.vuhung.minichatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vuhung.minichatapp.R;

public class RegisterActivity extends AppCompatActivity {

    TextView signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        signIn = findViewById(R.id.textSignIn);
        setListener();
    }

    private void setListener() {
        signIn.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}