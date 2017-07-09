package com.wareproz.mac.kedco;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.google.firebase.messaging.RemoteMessage;

/**
 * Created by filipp on 5/23/2016.
 */
public class FirebaseMessagingService extends com.google.firebase.messaging.FirebaseMessagingService{

    String theactivity,message;
    // Context
    Context _context;
    //get globals
    Globals g = Globals.getInstance();


    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {

        this._context = getApplicationContext();

        //theactivity = remoteMessage.getData().get("activity");
        message = remoteMessage.getData().get("message");
        showNotification(message);
/*
        if (theactivity.equals("meterReading")) {
            message = remoteMessage.getData().get("message");
            showNotification(message);
        }
*/
    }

    private void showNotification(String message) {

        Intent i = new Intent(this,HomeActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(this,0,i, PendingIntent.FLAG_UPDATE_CURRENT);

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                .setAutoCancel(true)
                .setContentTitle("Kedco Workspace")
                .setContentText(message)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(message))
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentIntent(pendingIntent)
                .setVibrate(new long[] { 1000, 1000, 1000, 1000, 1000 })
                .setSound(alarmSound)
                .setPriority(Notification.PRIORITY_HIGH);

        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(0,builder.build());

    }

}
