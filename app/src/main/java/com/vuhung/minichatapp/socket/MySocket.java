package com.vuhung.minichatapp.socket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vuhung.minichatapp.activity.UserActivity;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.utils.Constant;

import java.net.URISyntaxException;
import java.util.List;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class MySocket {
    private static Socket instance;
    private static boolean isListening = false;
    private MySocket() {}

     public static Socket getInstanceSocket() {
        if (instance == null) {
            synchronized (MySocket.class) {
                if (instance == null) {
                    try {
                        instance = IO.socket(Constant.DOMAIN);
                        instance.connect();
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    public static void start(String jwtToken) {
        Socket socket = MySocket.getInstanceSocket();
        if (socket != null && !isListening) {
            socket.emit("register", jwtToken);
            doListen();
            isListening = true;
        }
    }

    private static void doListen() {

        instance.on("message_to", data -> {
            System.out.println("Co tin nhan gui den");
            /*
                check tab tin nhắn đang mở
                    - nếu đúng user đó thì render
                    - ngược lại push notification
             */

        });

        instance.on("typing", data -> {
            System.out.println("dang typing");
            /*
                nếu đang nhắn đúng user đó thì hiển thị typing
             */
        });

        instance.on("user_status", data -> {
            System.out.println("on status");
            // update lại list user
        });
    }

    public static void fetchAllUsers() {
//        instance.on("fetch_all_user", data -> {
//            Log.e("test", data[0].toString());
//            Gson gson = new Gson();
//            List<User> users = gson.fromJson(data[0].toString(), new TypeToken<List<User>>(){}.getType());
//        });
    }

    public static void stop() {
        if (instance != null) {
            instance.disconnect();
            isListening = false;
            instance = null;
        }
    }
}
