package com.bayarchain.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.bayarchain.MainActivities.ScrollingActivity;
import com.bayarchain.R;
import com.google.android.gms.gcm.GcmListenerService;

import com.bayarchain.SessionManagement.SessionManager;

public class PushNotificationService extends GcmListenerService{

    String contract_id;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        NotificationCompat.Builder mBuilder, mBuilder2;
        NotificationManager mNotifyMgr;
        int requestID = (int) System.currentTimeMillis();
        int mNotificationId = 000;
        SessionManager session = new SessionManager(this);

        String message = data.getString("message");
        String title = data.getString("title");
        Log.d("Notification Received", title +"   "+ message);

        if(title.equals("CONTRACT")) {
            Intent notificationIntent = new Intent(getApplicationContext(), ScrollingActivity.class);
            contract_id = message.substring(13, 53).trim();
            Log.d("Trimmed Notification", contract_id);
            PendingIntent contentIntent;
            notificationIntent = new Intent(getApplicationContext(), ContractReceived.class);
            notificationIntent.putExtra("KEY_CONTRACT_ID", contract_id);

            session.storeMessage(message);

            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Bayar Chain")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_list))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo_list)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));

            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId++, mBuilder.build());
        }
        else if(title.trim().equals("PAYMENT")) {
            Intent notificationIntent = new Intent(getApplicationContext(), ScrollingActivity.class);
            Log.d("Trimmed Notification", "Hello");
            PendingIntent contentIntent =  PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            notificationIntent = new Intent(getApplicationContext(), PaymentReceived.class);
            notificationIntent.putExtra("KEY_PAYMENT", message);
            Log.d("PAYMENT_NOTI", message);
            session.storeMessage(message);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            mBuilder2 = new NotificationCompat.Builder(this)
                    .setContentTitle("Bayar Chain")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_list))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo_list)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId++, mBuilder2.build());
        }

        }
}
