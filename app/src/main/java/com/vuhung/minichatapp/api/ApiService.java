package com.vuhung.minichatapp.api;

import static com.vuhung.minichatapp.utils.Constant.DOMAIN;

import com.vuhung.minichatapp.model.BaseResponse;
import com.vuhung.minichatapp.model.User;

import java.util.Map;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    ApiService apiService = new Retrofit.Builder()
            .baseUrl(DOMAIN)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService.class);

    @POST("api/v1/login")
    Call<BaseResponse<String>> signIn(@Body User user);

    @POST("api/v1/login-token-huawei")
    Call<BaseResponse<String>> signInWithTokenHuawei(@Body Map<String, String> jwtObject);

    @POST("api/v1/profile")
    Call<BaseResponse<User>> fetchProfile(@Body Map<String, String> jwtObject);

    @POST("api/v1/user/update")
    Call<BaseResponse<String>> updateProfile(@Body Map<String, String> jwtObject);

    @POST("api/v1/register")
    Call<BaseResponse<String>> signUp(@Body User user);

    @POST("api/v1/authentication")
    Call<BaseResponse<String>> auth(@Body Map<String, String> jwtObject);


    @POST("api/v1/add-device")
    Call<BaseResponse<String>> sendDeviceTokenToServer(@Body Map<String, String> deviceTokenObject);
}
