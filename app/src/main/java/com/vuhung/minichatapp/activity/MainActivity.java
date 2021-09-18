package com.vuhung.minichatapp.activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.fragment.ChatFragment;
import com.vuhung.minichatapp.fragment.UsersFragment;
import com.vuhung.minichatapp.socket.MySocket;

public class MainActivity extends AppCompatActivity {

    BottomNavigationView bottomNavigationView;
    TextView titleNav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // start socket
        MySocket.start();
        titleNav = findViewById(R.id.title_home);
        bottomNavigationView = findViewById(R.id.bottom_nav);
        bottomNavigationView.setOnItemSelectedListener(handleNavSelected);
        bottomNavigationView.setSelectedItemId(R.id.tab_chat);

    }

    private NavigationBarView.OnItemSelectedListener handleNavSelected = new NavigationBarView.OnItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment = null;
            switch (item.getItemId()) {
                case R.id.tab_chat:
                    fragment = new ChatFragment();
                    loadFragment(fragment);
                    titleNav.setText("Chat");
                    return true;
                case R.id.tab_users:
                    fragment = new UsersFragment();
                    titleNav.setText("All User");
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    private void loadFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_main, fragment);
        // transaction.addToBackStack(null);
        transaction.commit();
    }
}