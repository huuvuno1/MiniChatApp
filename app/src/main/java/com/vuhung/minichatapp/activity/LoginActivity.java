package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.vuhung.minichatapp.R;

public class LoginActivity extends AppCompatActivity {

    TextView createNewAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        createNewAccount = findViewById(R.id.textCreateNewAccount);
        setLister();
    }

    private void setLister() {
        createNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}