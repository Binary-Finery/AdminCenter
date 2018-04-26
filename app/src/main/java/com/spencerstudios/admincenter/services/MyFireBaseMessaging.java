package com.spencerstudios.admincenter.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.spencerstudios.admincenter.Activities.PrimaryActvity;
import com.spencerstudios.admincenter.R;
import com.spencerstudios.admincenter.Utilities.PrefUtils;

public class MyFireBaseMessaging extends FirebaseMessagingService {

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        String senderId = remoteMessage.getNotification().getBody().split(" ")[0];
        String currentUser = PrefUtils.getUserPref(getApplicationContext(), "user", "");

        if (!senderId.equals(currentUser)) {
            showNotification(remoteMessage.getNotification());
        }else{
            Log.d("ON MESSAGE RECEIVED", "SENDER ID EQUALS CURRENT USER");
        }
    }

    private void showNotification(RemoteMessage.Notification notification) {
        Intent intent = new Intent(this, PrimaryActvity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, builder.build());
        }
    }
}