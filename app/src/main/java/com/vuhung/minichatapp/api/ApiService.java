package com.vuhung.minichatapp.api;

import static com.vuhung.minichatapp.utils.Constant.DOMAIN;

import com.vuhung.minichatapp.model.JwtResponse;
import com.vuhung.minichatapp.model.User;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);

    @POST("api/v1/login")
    Call<JwtResponse> signIn(@Body User user);

    @POST("api/v1/register")
    Call<User> signUp(@Body User user);
}
