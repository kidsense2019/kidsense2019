package com.example.kidsense2019.fcmObjects;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.example.kidsense2019.MainActivity;
import com.example.kidsense2019.Session;
import com.example.kidsense2019.location.MapsActivity;
import com.example.kidsense2019.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;

public class myFirebaseMessagingService extends FirebaseMessagingService {

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        System.out.println("New Fb Token : " + s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        if(remoteMessage.getData() != null) {
            sendNotification(remoteMessage);
        }
    }

    private void sendNotification(RemoteMessage remoteMessage) {

        int uniqueId = 0;
        Intent intent = null;
        String NOTIFICATION_CHANNEL_ID = "kidsense", contentText = null, contentTitle = null;

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            @SuppressLint("WrongConstant")
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "kidsense Notification", NotificationManager.IMPORTANCE_MAX);

            notificationChannel.setDescription("Kidsense Channel");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,1000,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        Map<String, String> data = remoteMessage.getData();
        String content = data.get("content");
        switch (content) {
            case "Location" :
                // set Unique Id
                uniqueId = (int) (System.currentTimeMillis() & 0xfffffff);
                // intent
                intent = new Intent(this, MapsActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("content", content);
                intent.putExtra("name", data.get("name"));
                intent.putExtra("latitude", data.get("latitude"));
                intent.putExtra("longitude", data.get("longitude"));
                intent.putExtra("timestamp", data.get("timestamp"));
                // inbox style
                inboxStyle.setBigContentTitle(data.get("name"));
                inboxStyle.addLine("Lat : " + data.get("latitude"));
                inboxStyle.addLine("Lng : " + data.get("longitude"));
                inboxStyle.addLine("When : " + data.get("timestamp"));
                inboxStyle.setSummaryText(data.get("message"));
                // notif builder
                contentTitle = data.get("name");
                contentText = data.get("timestamp");
                break;
            case "Kid Account Auth" :
                // set Unique Id
                uniqueId = 1;
                // intent
                intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                // inbox style
                inboxStyle.setBigContentTitle(data.get("name"));
                inboxStyle.addLine("Code : " + data.get("code"));
                inboxStyle.setSummaryText("Kidsense");
                // notif builder
                contentTitle = data.get("name");
                contentText = data.get("code");
                break;
            default:
                break;
        }

        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueId, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        notificationBuilder.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_kidsense_logo)
                .setTicker("New Incoming " + content)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setStyle(inboxStyle)
                .setContentInfo(content)
                .setContentIntent(pendingIntent);

        notificationManager.notify(uniqueId,notificationBuilder.build());
    }
}
