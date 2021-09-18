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

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.adapters.MessageAdapter;
import com.vuhung.minichatapp.model.Message;
import com.vuhung.minichatapp.model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatActivity extends AppCompatActivity {

    private EditText edtMessage;
    private AppCompatImageView btnSend;
    private RecyclerView rcvMessage;
    private AppCompatImageView btnBack;
    private MessageAdapter messageAdapter;
    private List<Message> mListMessage;
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
        User user = (User) bundle.get("object_user");
        TextView txtNameChat = findViewById(R.id.textNameChat);
        txtNameChat.setText(user.getFullName());

        //Hiển thị tin nhắn Chat

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        rcvMessage.setLayoutManager(linearLayoutManager);
        mListMessage = new ArrayList<>();
        messageAdapter = new MessageAdapter();
        messageAdapter.setData(mListMessage);

        rcvMessage.setAdapter(messageAdapter);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage();
            }
        });
        setListeners();
    }

    private void sendMessage() {
        String strMessage = edtMessage.getText().toString().trim();
        if(TextUtils.isEmpty(strMessage)) {
            return;
        }
        mListMessage.add(new Message(strMessage));
        messageAdapter.notifyDataSetChanged(); //load lai du lieu
        rcvMessage.scrollToPosition(mListMessage.size()-1);

        edtMessage.setText("");
    }

    private void setListeners() {
        btnBack.setOnClickListener(view -> onBackPressed());
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}