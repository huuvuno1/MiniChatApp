package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.vuhung.minichatapp.R;

public class LoginActivity extends AppCompatActivity {

    TextView createNewAccount;
    MaterialButton signIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createNewAccount = findViewById(R.id.textCreateNewAccount);
        signIn = findViewById(R.id.buttonSignIn);
        setListener();
    }

    private void setListener() {
        createNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
        signIn.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }
}