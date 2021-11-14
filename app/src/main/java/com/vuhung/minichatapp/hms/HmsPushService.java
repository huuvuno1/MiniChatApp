package com.vuhung.minichatapp.hms;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.gson.Gson;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.vuhung.minichatapp.MyApplication;
import com.vuhung.minichatapp.R;
import com.vuhung.minichatapp.activity.MainActivity;
import com.vuhung.minichatapp.model.Notify;

public class HmsPushService extends HmsMessageService {
    static Integer test = 1;

    @Override
    public void onNewToken(String s) {
        Log.e("newtoken", s);
        super.onNewToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.e("received", "oke em " + remoteMessage.getData());

        Gson gson = new Gson();
        Notify notify = gson.fromJson(remoteMessage.getData(), Notify.class);

        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification pNotification = new NotificationCompat.Builder(this, MyApplication.CHANEL_CHAT_ID)
                .setContentText(notify.getBody())
                .setContentTitle(notify.getTitle())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent).build();

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (manager != null) {
            manager.notify(1, pNotification);
        }

    }
}