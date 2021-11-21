package com.india.chat.samwaad.Notifications;

import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.india.chat.samwaad.R;

public class FirebaseMessaging extends FirebaseMessagingService {

    String Title, message;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d("msg", "message received");
        Title  = remoteMessage.getData().get("Title");
        message = remoteMessage.getData().get("Message");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext())
                .setSmallIcon(R.drawable.ic_back_arrow)
                .setContentTitle(Title)
                .setContentText(message);
        NotificationManager manager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        manager.notify(0, builder.build());
    }


}
