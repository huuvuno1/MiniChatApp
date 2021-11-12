package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.huawei.hmf.tasks.OnFailureListener;
import com.huawei.hmf.tasks.OnSuccessListener;
import com.huawei.hmf.tasks.Task;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.support.account.AccountAuthManager;
import com.huawei.hms.support.account.request.AccountAuthParams;
import com.huawei.hms.support.account.request.AccountAuthParamsHelper;
import com.huawei.hms.support.account.result.AuthAccount;
import com.huawei.hms.support.account.service.AccountAuthService;
import com.huawei.hms.support.api.entity.common.CommonConstant;
import com.huawei.hms.support.hwid.ui.HuaweiIdAuthButton;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.api.ApiService;
import com.vuhung.minichatapp.model.BaseResponse;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.utils.Constant;
import com.vuhung.minichatapp.utils.DeviceTokenUtil;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    TextView createNewAccount, textMessage;
    EditText inpEmail, inpPassword;
    MaterialButton signIn;
    HuaweiIdAuthButton huaweiIdAuthButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getView();
        setListener();

        Intent intent = getIntent();
        String message = intent.getStringExtra("register_success");
        if (message != null) {
            textMessage.setTextColor(Color.GREEN);
            textMessage.setText(message);
            textMessage.setVisibility(View.VISIBLE);
        }
    }

    private void getView() {
        createNewAccount = findViewById(R.id.textCreateNewAccount);
        signIn = findViewById(R.id.buttonSignIn);
        huaweiIdAuthButton = findViewById(R.id.HuaweiIdAuthButton);
        textMessage = findViewById(R.id.textMessage);
        inpEmail = findViewById(R.id.inputEmail);
        inpPassword = findViewById(R.id.inputPassword);
    }

    private void setListener() {
        // create a new account
        createNewAccount.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });

        // login
        signIn.setOnClickListener(v -> {
            String email = inpEmail.getText().toString();
            String password = inpPassword.getText().toString();
            // validate
            if (email.length() < 5 || password.length() < 5) {
                textMessage.setVisibility(View.VISIBLE);
                textMessage.setText("Wrong email or password");
                return;
            }
            User user = new User();
            user.setEmail(email);
            user.setPassword(password);
            signIn.setEnabled(false);
            ApiService.apiService.signIn(user).enqueue(new Callback<BaseResponse<String>>() {
                @Override
                public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> api_response) {
                    BaseResponse<String> response = api_response.body();
                    signIn.setEnabled(true);
                    if (response.getStatus() == 200) {
                        // save token
                        SharedPreferences preferences = getBaseContext().getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.putString("token", response.getData());
                        editor.commit();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    else {
                        textMessage.setVisibility(View.VISIBLE);
                        textMessage.setTextColor(Color.RED);
                        textMessage.setText("Wrong email or password");
                    }
                }

                @Override
                public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                    textMessage.setVisibility(View.VISIBLE);
                    textMessage.setTextColor(Color.RED);
                    textMessage.setText("Network error");
                    signIn.setEnabled(true);
                }
            });
        });

        // huawei
        huaweiIdAuthButton.setOnClickListener(v -> {
            silentSignInByHwId();
        });
    }

    // AccountAuthService provides a set of APIs, including silentSignIn, getSignInIntent, and signOut.
    private AccountAuthService mAuthService;

    // Specify the user information to be obtained after user authorization.
    private AccountAuthParams mAuthParam;

    // Define the request code for signInIntent.
    private static final int REQUEST_CODE_SIGN_IN = 1000;

    // Define the log flag.
    private static final String TAG = "Accountdfsdsa";

    /**
     * Silent sign-in: If a user has authorized your app and signed in, no authorization or sign-in screen will appear during subsequent sign-ins, and the user will directly sign in.
     * After a successful silent sign-in, the HUAWEI ID information will be returned in the success event listener.
     * If the user has not authorized your app or signed in, the silent sign-in will fail. In this case, your app will show the authorization or sign-in screen to the user.
     */
    private void silentSignInByHwId() {
        //  1. Use AccountAuthParams to specify the user information to be obtained after user authorization, including the user ID (OpenID and UnionID), email address, and profile (nickname and picture).
        // 2. By default, DEFAULT_AUTH_REQUEST_PARAM specifies two items to be obtained, that is, the user ID and profile.
        // 3. If your app needs to obtain the user's email address, call setEmail().
        // 4. To support authorization code-based HUAWEI ID sign-in, use setAuthorizationCode(). All user information that your app is authorized to access can be obtained through the relevant API provided by the Account Kit server.
        mAuthParam = new AccountAuthParamsHelper(AccountAuthParams.DEFAULT_AUTH_REQUEST_PARAM)
                .setEmail()
                .setId()
                .setIdToken()
                .setProfile()
                .setUid()
                .setAccessToken()
                .setAuthorizationCode()
                .createParams();

        // Use AccountAuthParams to build AccountAuthService.
        mAuthService = AccountAuthManager.getService(this, mAuthParam);

        // Sign in with a HUAWEI ID silently.
        Task<AuthAccount> task = mAuthService.silentSignIn();
        task.addOnSuccessListener(new OnSuccessListener<AuthAccount>() {
            @Override
            public void onSuccess(AuthAccount authAccount) {
                Log.e(TAG, "success");
                // The silent sign-in is successful. Process the returned AuthAccount object to obtain the HUAWEI ID information.
                dealWithResultOfSignIn(authAccount);
            }
        });
        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(Exception e) {
                Log.e(TAG, "fail " +  e.getMessage());
                // The silent sign-in fails. Your app will call getSignInIntent() to show the authorization or sign-in screen.
                if (e instanceof ApiException) {
                    ApiException apiException = (ApiException) e;
                    Intent signInIntent = mAuthService.getSignInIntent();
                    // If your app appears in full screen mode when a user tries to sign in, that is, with no status bar at the top of the device screen, add the following parameter in the intent:
                    // intent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
                    // Check the details in this FAQ.
                    signInIntent.putExtra(CommonConstant.RequestParams.IS_FULL_SCREEN, true);
                    startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
                }
            }
        });
    }

    /**
     * Process the returned AuthAccount object to obtain the HUAWEI ID information.
     *
     * @param authAccount AuthAccount object, which contains the HUAWEI ID information.
     */
    private void dealWithResultOfSignIn(AuthAccount authAccount) {
        Log.i(TAG,"getAccessToken:"+authAccount.getAccessToken());
        Map<String, String> body = new HashMap<>();
        body.put("token", authAccount.getAccessToken());
        ApiService.apiService.signInWithTokenHuawei(body).enqueue(new Callback<BaseResponse<String>>() {
            @Override
            public void onResponse(Call<BaseResponse<String>> call, Response<BaseResponse<String>> response) {
                // status 8888: missing fullname
                SharedPreferences preferences = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("token", response.body().getData());
                editor.commit();

                DeviceTokenUtil.sendDeviceTokenToServer(getApplicationContext());
                if (response.body().getStatus() == 8888) {
                    startActivity(new Intent(getApplicationContext(), UpdateFullnameActivity.class));
                } else {
                    startActivity(new Intent(getApplicationContext(), MainActivity.class));
                }
            }

            @Override
            public void onFailure(Call<BaseResponse<String>> call, Throwable t) {
                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_SHORT);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Log.i(TAG, "onActivitResult of sigInInIntent, request code: " + REQUEST_CODE_SIGN_IN);
            Task<AuthAccount> authAccountTask = AccountAuthManager.parseAuthResultFromIntent(data);
            if (authAccountTask.isSuccessful()) {
                // The sign-in is successful, send access token to server
                AuthAccount authAccount = authAccountTask.getResult();
                dealWithResultOfSignIn(authAccount);
            } else {
                // The sign-in fails. Find the cause from the status code. For more information, please refer to Error Codes.
                Log.e(TAG, "sign in failed : " +((ApiException)authAccountTask.getException()).getStatusCode());
                Toast.makeText(getApplicationContext(), "Login fail", Toast.LENGTH_SHORT);
            }
        }
    }
}