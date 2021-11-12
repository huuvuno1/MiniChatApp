package com.vuhung.minichatapp.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.BaseResponse;
import com.vuhung.minichatapp.utils.Constant;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        // get token
        SharedPreferences preferences = getBaseContext().getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
        String token = preferences.getString("token", "");
        if (!"".equals(token)) {
            Map<String, String> jwtObject = new HashMap<>();
            jwtObject.put("token", token);
            ApiService.apiService.auth(jwtObject).enqueue(new Callback<BaseResponse<String>>() {
                @Override
                public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> api_response) {
                    BaseResponse<String> response = api_response.body();
                    if (response.getStatus() == 200) {
                        Intent intent = new Intent(StartActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else if (response.getStatus() == 8888) {
                        startActivity(new Intent(StartActivity.this, UpdateFullnameActivity.class));
                    }
                    else {
                        startActivity(new Intent(StartActivity.this, LoginActivity.class));
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                    startActivity(new Intent(StartActivity.this, LoginActivity.class));
                }
            });
        }
        else {
            startActivity(new Intent(StartActivity.this, LoginActivity.class));
        }
        finish();
    }
}