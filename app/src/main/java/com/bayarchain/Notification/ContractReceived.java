package com.bayarchain.Notification;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import com.bayarchain.R;
import com.bayarchain.SessionManagement.SessionManager;

public class ContractReceived extends AppCompatActivity {
    RequestQueue queue;
    private TextView payment_rec;
    private Button acknowledge;
    private Button reject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contract_received);

        payment_rec = (TextView)findViewById(R.id.payment_rec_textview);
        acknowledge = (Button)findViewById(R.id.acknowldge);
        reject = (Button)findViewById(R.id.reject);


        SessionManager session = new SessionManager(this);
        String key = session.returnStoredMessage();
        final String contract_id = key.substring(13, 53).trim();

        Log.d("HELLLLLLLLOOOOOO", key);
        payment_rec.setText(key);
        acknowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContractReceived.this.finish();
                AddReceiverToContract(contract_id);
            }
        });

        reject.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {

            }
        });
    }
    private void AddReceiverToContract(String contract_id) {
        queue = Volley.newRequestQueue(this);
        SessionManager session = new SessionManager(this);
        HashMap<String, String> map = session.getUserDetails();
        if (contract_id.length() == 40) {
            Log.d("Hello", contract_id);
            Log.d("Hello", map.get(SessionManager.KEY_NAME));
            Log.d("Hello", map.get(SessionManager.KEY_PASS));

            String url =getResources().getString(R.string.apiurl) +
                    "control=notify" +
                    "&id=" + contract_id +
                    "&genname=" + map.get(SessionManager.KEY_NAME) +
                    "&password=" + map.get(SessionManager.KEY_PASS);
            Log.d("URL: " , url);
            final String TAG = "Inside receiving notification";
            StringRequest createContract = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {

                public void onResponse(String response) {
                    Log.d(TAG, response.toString().trim());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            createContract.setRetryPolicy(new DefaultRetryPolicy(20000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(createContract);
        }
    }
}
