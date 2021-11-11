package com.vuhung.minichatapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.BaseResponse;
import com.vuhung.minichatapp.model.User;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {
    EditText inpEmail, inpName, inpPassword, inpRepassword;
    TextView signIn, textMessage;
    Button btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getView();
        setListener();
    }

    private void getView() {
        signIn = findViewById(R.id.textSignIn);
        textMessage = findViewById(R.id.textMessage);
        inpEmail = findViewById(R.id.inputEmail);
        inpName = findViewById(R.id.inputName);
        inpPassword = findViewById(R.id.inputPassword);
        inpRepassword = findViewById(R.id.inputConfirmPassword);
        btnRegister = findViewById(R.id.buttonRegister);
    }

    private void setListener() {
        btnRegister.setOnClickListener(v -> {
            // validate
            String email = inpEmail.getText().toString();
            String password = inpPassword.getText().toString();
            String repassword = inpRepassword.getText().toString();
            String name = inpName.getText().toString();
            String message = null;
            if (!repassword.equals(password)) {
                message = "Repassword mismatched";
            }
            if (password.length() < 5) {
                message = "Password must contain at least 5 characters";
            }
            if (!(email.length() > 5 && email.contains("@")))
                message = "Invalid email";
            if (name.length() < 5 && !email.contains("@"))
                message = "Invalid name";
            if (message != null) {
                textMessage.setText(message);
                textMessage.setVisibility(View.VISIBLE);
                return;
            }

            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            user.setFullName(name);
            btnRegister.setEnabled(false);
            ApiService.apiService.signUp(user).enqueue(new Callback<BaseResponse<String>>() {
                @Override
                public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                    if (response.body().getStatus() == 200) {
                        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        intent.putExtra("register_success", "Register success!");
                        startActivity(intent);
                    }
                    else {
                        textMessage.setText("Failsak jsadf error");
                    }
                    btnRegister.setEnabled(true);
                }

                @Override
                public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                    textMessage.setText("Network error");
                    btnRegister.setEnabled(true);
                }
            });
        });
        signIn.setOnClickListener(v -> {
            startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
        });
    }
}