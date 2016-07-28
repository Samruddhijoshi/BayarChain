package com.bayarchain;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;

import SessionManagement.SessionManager;

public class ContractDetails extends AppCompatActivity {

    private static final String TAG = "ContractDetails";
    HashMap<String, String> hash;
    SessionManager session;
    RequestQueue queue2;
    String str1,str2,str3,str4,str5,str6;
    ProgressDialog pDialog;

    public void sendToPay(View view){
        //Intent intent = new Intent(this, payActivity.class);
        //startActivity(intent);
    }
    TextView  name, eventname, date, amt, status;
    Button settle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contract_details);
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

        settle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateContractMethod();
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
    private void CreateContractMethod() {
        Log.d(TAG, "inside method");
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Paying...");
        pDialog.show();
        queue2 = Volley.newRequestQueue(getBaseContext());
        Log.d(TAG, hash.get(SessionManager.KEY_NAME));
        Log.d(TAG, hash.get(SessionManager.KEY_PASS));

        String url2 = "http://bayarchain.southeastasia.cloudapp.azure.com/test8sprint.php?" +
                "control=pay" +
                "&genname=" + hash.get(SessionManager.KEY_NAME) +
                "&password=" + hash.get(SessionManager.KEY_PASS) +
                "&amount=" +  str3+
                "&id="+  str6;

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
            queue2.add(createContract);
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}
