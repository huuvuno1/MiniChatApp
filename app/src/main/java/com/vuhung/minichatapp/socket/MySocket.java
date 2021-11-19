package com.vuhung.minichatapp.socket;

import com.vuhung.minichatapp.utils.Constant;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

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
        return instance; // singleton design pattern
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
