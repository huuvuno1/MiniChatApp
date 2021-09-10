package com.vuhung.minichatapp.socket;

import java.net.URISyntaxException;

import io.socket.client.IO;
import io.socket.client.Socket;

public class MySocket {
    private static Socket instance;
    private MySocket() {}

     public static Socket getInstance() {
        if (instance == null) {
            synchronized (MySocket.class) {
                if (instance == null) {
                    try {
                        instance = IO.socket("http://192.168.1.2:3000");
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return instance;
    }

    public static void start() {
        Socket socket = MySocket.getInstance();
        MySocket mySocket = new MySocket();
        if (socket != null) {
            mySocket.doListen();
        }
    }

    private void doListen() {
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
}
