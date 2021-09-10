package com.vuhung.minichatapp.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.socket.MySocket;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start socket
        MySocket.start();
    }
}