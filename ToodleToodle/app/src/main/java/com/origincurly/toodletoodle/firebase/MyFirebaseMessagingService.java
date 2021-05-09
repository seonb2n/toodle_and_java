package com.origincurly.toodletoodle.firebase;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.origincurly.toodletoodle.GlobalValue;
import com.origincurly.toodletoodle.IntroActivity;
import com.origincurly.toodletoodle.R;

import static com.origincurly.toodletoodle.GlobalValue.DEVICE_PREFERENCE_NAME;

public class MyFirebaseMessagingService extends FirebaseMessagingService implements GlobalValue {

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, token);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From:"+remoteMessage.getFrom());

        boolean isPushOn = (getDevicePreferences("push", 0) == 1);

        if (remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data payload:"+remoteMessage.getData());
            //handleNow();
        }

        if (remoteMessage.getNotification() != null && isPushOn) {
            sendNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    private void handleNow() {
        Log.d(TAG, "Short lived task is done.");
    }

    private void sendNotification(String messageTitle, String messageBody) {
        Intent intent = new Intent(this, IntroActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        int uniqueInt = (int) (System.currentTimeMillis() & 0xfffffff);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, uniqueInt, intent, PendingIntent.FLAG_ONE_SHOT);

        String channelId = getString(R.string.default_notification_channel_id);
        String channelName = getString(R.string.default_notification_channel_name);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, channelId)
                        .setSmallIcon(getNotificationIcon())
                        .setContentTitle(messageTitle)
                        .setContentText(messageBody)
                        .setAutoCancel(true)
                        .setSound(defaultSoundUri)
                        .setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_HIGH);
            notificationManager.createNotificationChannel(channel);
        }

        notificationManager.notify(0, notificationBuilder.build());
    }

    private int getNotificationIcon() {
        boolean useWhiteIcon = (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP);
        //TODO 아이콘 정하기
        return useWhiteIcon ? R.drawable.ic_star_3 : R.drawable.ic_star_3; // left white, right colored
    }

    public int getDevicePreferences(String key, int fault) {
        SharedPreferences pref = getSharedPreferences(DEVICE_PREFERENCE_NAME, Activity.MODE_PRIVATE);
        return pref.getInt(key, fault);
    }
}
