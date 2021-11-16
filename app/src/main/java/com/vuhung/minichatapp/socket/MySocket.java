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
        if (socket != null) {
            socket.emit("register", jwtToken);
        }
    }

    public static void stop() {
        if (instance != null) {
            instance.disconnect();
            instance = null;
        }
    }
}
