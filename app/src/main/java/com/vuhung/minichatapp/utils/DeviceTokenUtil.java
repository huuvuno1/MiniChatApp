package com.vuhung.minichatapp.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import com.huawei.agconnect.config.AGConnectServicesConfig;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.BaseResponse;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class DeviceTokenUtil {
    private static final String TAG = "token_device";

    public static void sendDeviceTokenToServer(Context context) {
        // Create a thread.
        Log.e(TAG, "run get token");
        new Thread() {
            @Override
            public void run() {
                try {
                    // Obtain the app ID from the agconnect-service.json file.
                    String appId = AGConnectServicesConfig.fromContext(context).getString("client/app_id");
                    // Set tokenScope to HCM.
                    String tokenScope = "HCM";
                    String deviceToken = HmsInstanceId.getInstance(context).getToken(appId, tokenScope);
                    Log.i(TAG, "get token: " + deviceToken);

                    // Check whether the token is empty.
                    if(!TextUtils.isEmpty(deviceToken)) {
                        SharedPreferences preferences = context.getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, Context.MODE_PRIVATE);
                        preferences.edit().putString("device_token", deviceToken).commit();

                        String jwtToken = preferences.getString("token", "");
                        Map<String, String> body = new HashMap<>();
                        body.put("jwt_token", jwtToken);
                        body.put("device_token", deviceToken);
                        ApiService.apiService.sendDeviceTokenToServer(body).enqueue(new Callback<BaseResponse<String>>() {
                            @Override
                            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                                Log.e(TAG, "send token success");
                            }

                            @Override
                            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                                Log.e(TAG, "send fail");
                            }
                        });
                    }
                } catch (ApiException e) {
                    e.printStackTrace();
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }
}
