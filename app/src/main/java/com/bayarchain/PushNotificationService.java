package com.bayarchain;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RelativeLayout;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.HashMap;

import SessionManagement.SessionManager;
import sprint3_ad.PaymentReceived;
import sprint3_ad.ScrollingActivity;

public class PushNotificationService extends GcmListenerService{

    String contract_id;
    RequestQueue queue;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        Intent notificationIntent = new Intent(getApplicationContext(), ScrollingActivity.class);
        NotificationCompat.Builder mBuilder;
        NotificationManager mNotifyMgr;
        int requestID = (int) System.currentTimeMillis();
        PendingIntent contentIntent =  PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        int mNotificationId = 000;
        SessionManager session = new SessionManager(this);

        String message = data.getString("message");
        String title = data.getString("title");
        Log.d("Notification Received", title +"   "+ message);

        if(title.equals("CONTRACT")) {
            contract_id = message.substring(13, 53).trim();
            Log.d("Trimmed Notification", contract_id);
            Notify(contract_id);
            notificationIntent = new Intent(getApplicationContext(), ScrollingActivity.class);
            notificationIntent.putExtra("KEY_CONTRACT_ID", contract_id);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        else if(title.trim().equals("PAYMENT")) {
            Log.d("Trimmed Notification", "Hello");
            notificationIntent = new Intent(getApplicationContext(), PaymentReceived.class);
            notificationIntent.putExtra("KEY_PAYMENT", message);
            Log.d("PAYMENT_NOTI", message);
            session.storeMessage(message);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        mBuilder = new NotificationCompat.Builder(this)
                    .setContentTitle("Bayar Chain")
                    .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.logo_list))
                    .setAutoCancel(true)
                    .setSmallIcon(R.drawable.logo_list)
                    .setContentText(message)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setContentIntent(contentIntent)
                    .setStyle(new NotificationCompat.BigTextStyle().bigText(message));
                    //.addAction(R.drawable.abc_btn_check_to_on_mtrl_015, "APPROVE", contentIntent)
                    //.addAction(R.drawable.abc_ic_clear_mtrl_alpha     , "REJECT" , contentIntent);
            mNotifyMgr = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            mNotifyMgr.notify(mNotificationId++, mBuilder.build());
        }


    private void Notify(String contract_id) {
        queue = Volley.newRequestQueue(this);
        SessionManager session = new SessionManager(this);
        HashMap<String, String> map = session.getUserDetails();
        if (contract_id.length() == 40) {
            Log.d("Hello", contract_id);
            Log.d("Hello", map.get(SessionManager.KEY_NAME));
            Log.d("Hello", map.get(SessionManager.KEY_PASS));

            String url = "http://bayarchain.southeastasia.cloudapp.azure.com/sam1.php?" +
                    "control=notify" +
                    "&id=" + contract_id +
                    "&genname=" + map.get(SessionManager.KEY_NAME) +
                    "&password=" + map.get(SessionManager.KEY_PASS);
            Log.d("URL: " , url);
            final String TAG = "Inside receiving notification";
            StringRequest createContract = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                public void onResponse(String response) {
                    Log.d(TAG, response.toString().trim());
                    //hidePDialog();
                    //received_contract_address = response.toString().trim();
                    ///Send_Notification(received_contract_address);

                    //Log.d(TAG, received_contract_address);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            // Adding request to request queue
            createContract.setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

            queue.add(createContract);
            //Log.d(TAG + "asdas", received_contract_address);
        }
    }
}
