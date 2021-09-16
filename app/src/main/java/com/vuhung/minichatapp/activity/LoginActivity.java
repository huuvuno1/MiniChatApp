package com.vuhung.minichatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.JwtResponse;
import com.vuhung.minichatapp.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView createNewAccount, message;
    MaterialButton buttonSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        buttonSignIn = findViewById(R.id.buttonSignIn);
        message = findViewById(R.id.textMessage);
        createNewAccount = findViewById(R.id.textCreateNewAccount);
        setLister();
    }

    private void setLister() {
        createNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // login
        buttonSignIn.setOnClickListener(v -> {
            buttonSignIn.setEnabled(false);
            String email = ((EditText)findViewById(R.id.inputEmail)).getText().toString();
            String password = ((EditText)findViewById(R.id.inputPassword)).getText().toString();
            if (verifyInput(email, password)) {
                User user = new User();
                user.setEmail(email);
                user.setPassword(password);
                ApiService.apiService.signIn(user).enqueue(new Callback<JwtResponse>() {
                    @Override
                    public void onResponse(Call<JwtResponse> call, Response<JwtResponse> response) {
                        JwtResponse jwtResponse = response.body();
                        if (jwtResponse.getStatus() == 200) {
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                        else {
                            buttonSignIn.setEnabled(true);
                            message.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onFailure(Call<JwtResponse> call, Throwable t) {
                        t.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Login false!", Toast.LENGTH_SHORT).show();
                        buttonSignIn.setEnabled(true);
                    }
                });
            }
        });
    }

    private boolean verifyInput(String username, String password) {
        // do something...
        return true;
    }
}