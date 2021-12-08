package com.vuhung.minichatapp.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import io.socket.client.Socket;
import pl.droidsonroids.gif.GifImageView;

public class ChatActivity extends AppCompatActivity {

    private EditText edtMessage;
    private AppCompatImageView btnSend;
    private RecyclerView rcvMessage;
    private AppCompatImageView btnBack;
    private MessageAdapter messageAdapter;
    private List<Message> mListMessage;
    private ImageView btnDelete;
    private GifImageView gifTyping;
    private User partner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        getViews();

        //lấy dữ liệu từ userAdapter
        Bundle bundle = getIntent().getExtras();
        Log.e("lsdfjsldkfjsdlk", "noi " + bundle);
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

        // get data
        bindDataFromSocket(partner.getUsername());

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendMessage(partner.getUsername());
            }
        });
        setListeners();
    }

    private void getViews() {
        //Anh xa View
        edtMessage = findViewById(R.id.inputMessage);
        btnSend = findViewById(R.id.buttonSend);
        rcvMessage = findViewById(R.id.chatRecyclerView);
        btnBack = findViewById(R.id.imageBack);
        gifTyping = findViewById(R.id.typing);
        btnDelete = findViewById(R.id.imageInfo);
    }

    private void bindDataFromSocket(String usernameFriend) {
        // start app from notification
        if (Constant.MY_USERNAME.equals("")) {
            String token = getSharedPreferences(Constant.SHARE_PREFERENCES_NAME, MODE_PRIVATE).getString("token", "");
            MySocket.start(token);
        }

        Socket socket = MySocket.getInstanceSocket();
        socket.emit("fetch_chat_history", usernameFriend);
        socket.on("fetch_chat_history", data -> {
            if (data == null || "null".equals(data[0]))
                return;
           ChatHistory chatHistory = new Gson().fromJson(data[0].toString(), ChatHistory.class);
           mListMessage.addAll(chatHistory.getMessages());
           this.runOnUiThread(() -> {
               this.messageAdapter.notifyDataSetChanged();
               this.rcvMessage.scrollToPosition(mListMessage.size()-1);
           });
        });

        socket.on("on_typing", data -> {
            if (data == null || "null".equals(data[0]))
                return;
            Map<String, String> body = new Gson().fromJson(data[0].toString(), new TypeToken<HashMap<String, String>>(){}.getType());
            if (partner.getUsername().equals(body.get("sender")) && "true".equals(body.get("typing"))) {
                this.runOnUiThread(() -> {
                    gifTyping.setVisibility(View.VISIBLE);
                    rcvMessage.scrollToPosition(mListMessage.size() - 1);
                });
            }
            else
                this.runOnUiThread(() -> {
                    gifTyping.setVisibility(View.GONE);
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
        message.setTime(new Date());
        mListMessage.add(message);
        messageAdapter.notifyDataSetChanged(); //load lai du lieu
        rcvMessage.scrollToPosition(mListMessage.size()-1);
        edtMessage.setText("");

        // send to server
        Socket socket = MySocket.getInstanceSocket();
        socket.emit("send_chat", new Gson().toJson(message));

        Map<String, String> body = new HashMap<>();
        body.put("receiver", partner.getUsername());
        body.put("typing", "false");
        socket.emit("on_typing", new Gson().toJson(body));
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
                    rcvMessage.scrollToPosition(mListMessage.size() - 1);
                }
            });
        });

        Map<String, String> body = new HashMap<>();
        body.put("receiver", partner.getUsername());


        // event for back
        btnBack.setOnClickListener(view -> {
            body.put("typing", "false");
            MySocket.getInstanceSocket().emit("on_typing", new Gson().toJson(body));
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        });

        // event typing
        edtMessage.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void afterTextChanged(Editable editable) {
                Log.e("sdlfkajflksadj", "typing: " + editable.toString());
                if (edtMessage.getText().toString().equals(""))
                    body.put("typing", "false");
                else
                    body.put("typing", "true");
                MySocket.getInstanceSocket().emit("on_typing", new Gson().toJson(body));
            }
        });

        btnDelete.setOnClickListener(v -> {
            AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
            builder1.setMessage("Are you sure you want to delete?");
            builder1.setCancelable(true);

            builder1.setPositiveButton(
                    "Yes",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            MySocket.getInstanceSocket().emit("delete_chat_history", partner.getUsername());
                            dialog.cancel();
                            finish();
                        }
                    });

            builder1.setNegativeButton(
                    "No",
                    new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });

            AlertDialog alert11 = builder1.create();
            alert11.show();
        });
    }

    private String getReadableDateTime(Date date) {
        return new SimpleDateFormat("MMMM dd, yyyy - hh:mm a", Locale.getDefault()).format(date);
    }
}
