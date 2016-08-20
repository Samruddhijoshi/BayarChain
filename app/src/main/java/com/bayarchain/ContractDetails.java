package com.bayarchain;

import android.app.ProgressDialog;
import android.content.Intent;
import org.json.JSONException;
import org.json.JSONObject;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.android.volley.*;
import com.android.volley.toolbox.JsonObjectRequest;


import java.util.HashMap;

import Push.Content;
import Push.POST2GCM;
import SessionManagement.SessionManager;

public class ContractDetails extends AppCompatActivity {

    private static final String TAG = "ContractDetails";
    HashMap<String, String> hash;
    SessionManager session;
    RequestQueue queue2;
    String str1,str2,str3,str4,str5,str6;
    private EditText Inputamount;
    ImageButton cash,Xfers;
    ProgressDialog pDialog;

    public void sendToPay(View view){
        //Intent intent = new Intent(this, payActivity.class);
        //startActivity(intent);
    }
    TextView  name, eventname, date, amt, status;
    Button settle,settle_cash;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_display);
        Intent intent = getIntent();

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
        eventname = (TextView)findViewById(R.id.ename);
        date = (TextView)findViewById((R.id.date));
        amt = (TextView)findViewById(R.id.amt);
        status = (TextView)findViewById(R.id.status);
        settle = (Button)findViewById(R.id.settle);
        cash = (ImageButton)findViewById(R.id.imageButton);
        Xfers = (ImageButton)findViewById(R.id.imageButton2);
        Inputamount = (EditText)findViewById(R.id.inputamount);


        name.setText(str4); // rec name
        eventname.setText(str1); //event name
        date.setText(str2);
        amt.setText(str3);
        if(str5.toString().trim().equals("0")) {
            status.setText("has not been settled");
            status.setTextColor(Color.parseColor("#b52121"));
        }
        else if(str5.toString().trim().equals("1")){
            status.setText("has been settled");
            status.setTextColor(Color.parseColor("#FF119100"));
        }

        cash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateCashPayout(Inputamount.getText().toString().trim());
            }
        });
        Xfers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateXferPayout(Inputamount.getText().toString().trim());
            }
        });

    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                finish();
                Intent intent = new Intent(this, TabActivity.class);
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
    public  Content createContent(String str){
        //get notification id of other user.
        Content c = new Content();
        //c.addRegId(not_id);
        c.createData("message", "Plesae confirm that you have received payment for the");
        c.createData("title", "receiver");
        return c;
    }
    private void CreateCashPayout(String amount) {
        //send notification from payee to reciever, asking if he has received the money.
        Send_Notification("Hello");
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
        String url ="http://23.97.60.51/bankapi/register.php?control=payout";
        Log.d("Xfers Pay Link", url);
        JsonObjectRequest req = new JsonObjectRequest(url, new JSONObject(),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d(TAG, response.toString().trim());
                        hidePDialog();

                        try {
                            VolleyLog.v("Response:%n %s", response.toString(4));
                            Log.d(TAG, response.getString("id"));
                            Log.d(TAG, response.getString("recipient"));
                            Log.d(TAG, response.getString("invoice_id"));
                            Log.d(TAG, response.getString("amount"));
                            Log.d(TAG, response.getString("descriptions"));
                            Log.d(TAG, response.getString("status"));
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });

        req.setRetryPolicy(new DefaultRetryPolicy(80000,	0, 1.0f));
        queue.add(req);
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
