package com.bayarchain;

import android.app.ProgressDialog;
import android.content.Intent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;


import java.util.HashMap;

import Model.ID;
import Push.Content;
import Push.POST2GCM;
import SessionManagement.SessionManager;
import sprint3_ad.ScrollingActivity;

public class ContractDetails extends AppCompatActivity {

    private static final String TAG = "ContractDetails";
    HashMap<String, String> hash;
    SessionManager session;
    RequestQueue queue2;
    String str1,str2,str3,str4,str5,str6;
    private EditText Inputamount;
    ImageButton cash,Xfers;
    ProgressDialog pDialog;
    Button pay_final;
    String  notif_id ;
    private final String NOTIFICATION_TYPE = "PAYMENT";


    TextView  name, eventname, date, amt, status, payment_method;
    Button settle, settle_cash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_display);
        Intent intent = getIntent();

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        session = new SessionManager(getBaseContext());
        hash = new HashMap<String, String>();
        hash = session.getUserDetails();

        str1 = intent.getStringExtra("TAB2_EVENTNAME");
        str2 = intent.getStringExtra("TAB2_DATE");
        str3 = intent.getStringExtra("TAB2_AMOUNT");
        str4 = intent.getStringExtra("TAB2_OWNER");
        str5 = intent.getStringExtra("TAB2_STATUS");
        str6 = intent.getStringExtra("TAB2_CONTRACT_ADD");

        name = (TextView)findViewById(R.id.rname);
        pay_final = (Button)findViewById(R.id.paybuttonfinal);
        eventname = (TextView)findViewById(R.id.ename);
        date = (TextView)findViewById((R.id.date));
        amt = (TextView)findViewById(R.id.amt);
        status = (TextView)findViewById(R.id.status);
        settle = (Button)findViewById(R.id.settle);
        cash = (ImageButton)findViewById(R.id.imageButton2);
        Xfers = (ImageButton)findViewById(R.id.imageButton);
        Inputamount = (EditText)findViewById(R.id.inputamount);
        payment_method = (TextView)findViewById(R.id.textView19);


        name.setText(str4); // rec name
        eventname.setText(str1); //event name
        date.setText(str2);
        amt.setText(str3);
        if(str5.toString().trim().equals("0")) {
            status.setText("Not settled".toUpperCase());
            amt.setTextColor(Color.RED);
            status.setTextColor(Color.parseColor("#b52121"));
        }
        else if(str5.toString().trim().equals("1")){
            status.setText("has been settled");
            status.setTextColor(Color.parseColor("#FF119100"));
        }
        pay_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount= Inputamount.getText().toString().trim();
                int amount_check = Integer.parseInt(amount);

                if(amount_check <= Integer.parseInt(str3))
                    CreateXferPayout(Inputamount.getText().toString().trim());
                else
                    Toast.makeText(ContractDetails.this, "Amount cannot be greater than Contract Amount", Toast.LENGTH_SHORT).show();
            }
        });
        Xfers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment_method.setText("You have chosen Xfers");


            }
        });
        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payment_method.setText("You have chosen Cash");


            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                Intent intent = new Intent(this, ScrollingActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    public void Send_Notification(String str){
        String apiKey = "AIzaSyCHslDzvLhkgY_k-J5C_us2T7YHhMgJabw";
        Content content = createContent(str);
        Log.d(TAG, "inside method send notification");
        POST2GCM.post(apiKey, content);
    }
    public Content createContent(String str){
        //get notification id of other user.
        Content c = new Content();
        c.addRegId(str);
        c.createData(NOTIFICATION_TYPE, str4.toUpperCase()
                + " has settled the payment of "
                + Inputamount.getText().toString().trim()
                + " for " + str1.toUpperCase()
                + " by "
                + payment_method.getText().toString().substring(16, payment_method.getText().toString().length())
                + ". Please tap on this balloon to open! " );
        return c;
    }

    private void GetNotificationID() {
        final String[] noti_id = new String[1];
        RequestQueue queue = Volley.newRequestQueue(ContractDetails.this);
        String url = "http://23.97.60.51/bayar_mysql/get_noti_id.php?username="+ str4;
        JsonArrayRequest movieReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString().trim());
                hidePDialog();
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Send_Notification(obj.getString("noti_id"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                //hidePDialog();
            }
        });

        queue.add(movieReq);


    }

    private void CreateCashPayout(String amount) {
        //send notification from payee to reciever, asking if he has received the money.
        Send_Notification("Tap to confirm that you have recieved cash settlement for the Expense " + str1);
        Log.d(TAG, "inside method");
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Paying...");
        pDialog.show();
        queue2 = Volley.newRequestQueue(getBaseContext());
        Log.d(TAG, hash.get(SessionManager.KEY_NAME));
        Log.d(TAG, hash.get(SessionManager.KEY_PASS));

        String url2 = "";

        Log.d(TAG, url2);
        StringRequest createContract = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, response.toString().trim());
                pDialog.setMessage("Done");
                hidePDialog();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePDialog();
                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        //queue2.add(createContract);
    }

    private void CreateXferPayout(String amount) {

        pDialog = new ProgressDialog(this);
        // Showing progsress dialog before making http request
        pDialog.setMessage("Paying Via Xfers");
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url2 = "http://23.97.60.51/sam1.php?" +
                "control=pay" +
                "&genname=" + hash.get(SessionManager.KEY_NAME) +
                "&password=" + hash.get(SessionManager.KEY_PASS) +
                "&amount=" +  Inputamount.getText().toString().trim()+
                "&id="+  str6;

        Log.d("Xfers Pay Link", url2);
        StringRequest req = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, response.toString().trim());
                GetNotificationID();
                pDialog.setMessage("Done");
                hidePDialog();
                finish();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hidePDialog();

                VolleyLog.d(TAG, "Error: " + error.getMessage());
            }
        });
        req.setRetryPolicy(new DefaultRetryPolicy(80000,	0, 1.0f));

        queue.add(req);
       // Send_Notification("Hello, ");
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
