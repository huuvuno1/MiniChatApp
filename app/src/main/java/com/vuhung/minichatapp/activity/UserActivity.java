package com.vuhung.minichatapp.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;

import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.adapters.UsersAdapter;
import com.vuhung.minichatapp.model.User;

import java.util.ArrayList;
import java.util.List;


public class UserActivity extends AppCompatActivity {

    private AppCompatImageView imageView;
    private RecyclerView rcvData;
    private UsersAdapter usersAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);
        imageView = findViewById(R.id.imageBack1);
        rcvData = findViewById(R.id.usersRecyclerView);
        usersAdapter = new UsersAdapter(this, getListUser());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, RecyclerView.VERTICAL, false);
        rcvData.setLayoutManager(linearLayoutManager);
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        rcvData.addItemDecoration(itemDecoration);
        rcvData.setAdapter(usersAdapter);
        setListeners();
    }

    private List<User> getListUser() {
        List<User> list = new ArrayList<>();
        list.add(new User(R.drawable.ic_launcher_foreground,"alo","Manh Hung"));
        list.add(new User(R.drawable.ic_launcher_background,"Where are you from?","Huu Vu"));
        list.add(new User(R.drawable.ic_launcher_foreground,"How are you?","Duc Cuong"));
        list.add(new User(R.drawable.ic_launcher_foreground,"Hello","Van lap"));
        list.add(new User(R.drawable.ic_launcher_background,"Hi","Van Quang"));
        list.add(new User(R.drawable.ic_launcher_foreground,"Good Bye!","Quoc Cuong"));
        list.add(new User(R.drawable.ic_launcher_foreground,"Good Morning","Tien Dung"));
        list.add(new User(R.drawable.ic_launcher_background,"Say Hello","Nguyen Nga"));
        list.add(new User(R.drawable.ic_launcher_foreground,"Thank You","Tien Dat"));
        list.add(new User(R.drawable.ic_launcher_foreground,"Thanks","Thu Huyen"));
        list.add(new User(R.drawable.ic_launcher_background,"chào bạn","Thanh Hung"));
        list.add(new User(R.drawable.ic_launcher_foreground,"cảm ơn bạn","Van Quyet"));
        return list;
    }

    private void setListeners() {
        imageView.setOnClickListener(view -> onBackPressed());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(usersAdapter != null) {
            usersAdapter.release();
        }
    }
}