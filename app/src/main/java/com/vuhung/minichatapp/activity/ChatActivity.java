package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.adapters.MessageAdapter;
import com.vuhung.minichatapp.model.ChatHistory;
import com.vuhung.minichatapp.model.Message;
import com.vuhung.minichatapp.model.User;
import com.vuhung.minichatapp.socket.MySocket;
import com.vuhung.minichatapp.utils.Constant;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.socket.client.Socket;

public class ChatActivity extends AppCompatActivity {

    private EditText edtMessage;
    private AppCompatImageView btnSend;
    private RecyclerView rcvMessage;
    private AppCompatImageView btnBack;
    private MessageAdapter messageAdapter;
    private List<Message> mListMessage;
    private User partner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        //Anh xa View
        edtMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.buttonSend);
        rcvMessage = findViewById(R.id.chatRecyclerView);
        btnBack = findViewById(R.id.imageBack);

        //lấy dữ liệu từ userAdapter
        Bundle bundle = getIntent().getExtras();
        if(bundle == null) {
            return;
        }
        partner = (User) bundle.get("object_user");
        TextView txtNameChat = findViewById(R.id.textNameChat);
        txtNameChat.setText(partner.getFullName());

        //Hiển thị tin nhắn Chat

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMessage.setLayoutManager(linearLayoutManager);
        mListMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter();
        messageAdapter.setData(mListMessage);

        rcvMessage.setAdapter(messageAdapter);
        bindDataFromSocket(partner.getUsername());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(partner.getUsername());
            }
        });
        setListeners();
    }

    private void bindDataFromSocket(String usernameFriend) {
        Socket socket = MySocket.getInstanceSocket();
        socket.emit("fetch_chat_history", usernameFriend);
        socket.on("fetch_chat_history", data -> {
            if ("null".equals(data[0]))
                return;
           ChatHistory chatHistory = new Gson().fromJson(data[0].toString(), ChatHistory.class);
           mListMessage.addAll(chatHistory.getMessages());
           this.runOnUiThread(() -> {
               this.messageAdapter.notifyDataSetChanged();
               this.rcvMessage.scrollToPosition(mListMessage.size()-1);
           });
        });
    }

    private void sendMessage(String receiver) {
        String strMessage = edtMessage.getText().toString().trim();
        if(TextUtils.isEmpty(strMessage)) {
            return;
        }
        Message message = new Message();
        message.setContent(strMessage);
        message.setReceiver(receiver);
        message.setSender(Constant.MY_USERNAME);
        mListMessage.add(message);
        messageAdapter.notifyDataSetChanged(); //load lai du lieu
        rcvMessage.scrollToPosition(mListMessage.size()-1);
        edtMessage.setText("");

        // send to server
        Socket socket = MySocket.getInstanceSocket();
        socket.emit("send_chat", new Gson().toJson(message));
    }

    private void setListeners() {
        MySocket.getInstanceSocket().on("receive_message", data -> {
           if (data[0] == null || "null".equals(data[0]))
               return;
            Message message = new Gson().fromJson(data[0].toString(), Message.class);
            this.runOnUiThread(() -> {
                if (partner.getUsername().equals(message.getSender())) {
                    mListMessage.add(message);
                    messageAdapter.notifyDataSetChanged();
                }
            });
        });
        btnBack.setOnClickListener(view -> onBackPressed());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}