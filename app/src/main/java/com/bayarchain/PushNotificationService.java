package com.bayarchain;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

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

/**
 * Created by kundan on 10/22/2015.
 */
public class PushNotificationService extends GcmListenerService{

    String contract_id;
    RequestQueue queue;
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String message = data.getString("message");
        Log.d("Notification Received", message);
        contract_id = message.substring(13, message.length()).trim();
        Log.d("Trimmed Notification", contract_id);
        //createNotification(mTitle, push_msg);
        int requestID = (int) System.currentTimeMillis();
        Notify(contract_id);
        Intent notificationIntent = new Intent(getApplicationContext(), TabActivity.class);
        notificationIntent.putExtra("KEY_CONTRACT_ID", contract_id);
        notificationIntent.setAction("ACTION_1");
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);

        PendingIntent contentIntent = PendingIntent.getActivity(this, requestID, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                        .setContentTitle("Bayar Chain")
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setAutoCancel(true)
                        .setContentText(message)
                        .setDefaults(Notification.DEFAULT_ALL)
                        .setContentIntent(contentIntent);
        //mBuilder.addAction(R.drawable.ic_action_add, "click", contentIntent);
        int mNotificationId = 001;
        // Gets an instance of the NotificationManager service
        NotificationManager mNotifyMgr =
                (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Builds the notification and issues it.
        mNotifyMgr.notify(mNotificationId++, mBuilder.build());
        //Notify();
    }

    private void Notify(String contract_id) {
        queue = Volley.newRequestQueue(this);
        SessionManager session = new SessionManager(this);
        HashMap<String, String> map = session.getUserDetails();
        //Intent intent = getActivity().getIntent();
        //contract_id = intent.getStringExtra("KEY_CONTRACT_ID");
        if (contract_id.length() == 40) {
            Log.d("Hello", contract_id);
            Log.d("Hello", map.get(SessionManager.KEY_NAME));
            Log.d("Hello", map.get(SessionManager.KEY_PASS));

            String url = "http://bayarchain.southeastasia.cloudapp.azure.com/test8sprint.php?" +
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
