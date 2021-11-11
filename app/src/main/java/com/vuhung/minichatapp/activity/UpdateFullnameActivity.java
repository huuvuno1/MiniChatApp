package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.BaseResponse;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.utils.Constant;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFullnameActivity extends AppCompatActivity {
    Button btnUpdate;
    EditText inpName;
    TextView txtMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_fullname);
        getView();
        setListener();
    }

    private void setListener() {
        btnUpdate.setOnClickListener(v -> {
            if (inpName.getText().toString().length() > 5) {
                SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
                String token = preferences.getString("token", "");
                Map<String, String> body = new HashMap<>();
                body.put("token", token);
                body.put("fullname", inpName.getText().toString());
                ApiService.apiService.updateProfile(body).enqueue(new Callback<BaseResponse<String>>() {
                    @Override
                    public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                    }

                    @Override
                    public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                        Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT);
                    }
                });
            } else {
                txtMessage.setText("Name must contain at least 5 characters");
            }
        });
    }

    private void getView() {
        btnUpdate = findViewById(R.id.buttonChangeName);
        inpName = findViewById(R.id.inputName);
        txtMessage = findViewById(R.id.textMessage);
    }
}