package com.bayarchain.MainActivities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bayarchain.Push.Content;
import com.bayarchain.Push.POST2GCM;
import com.bayarchain.R;
import com.bayarchain.SessionManagement.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class ContractDetails extends AppCompatActivity {

    private static final String         TAG = "ContractDetails";
    private HashMap<String, String>     hash;
    private SessionManager              session;
    private RequestQueue                queue2;
    private String                      IntentEventName, IntentAmount, IntentDate, IntentOwner, IntentStatus, IntentContractAddress;
    private EditText                    Inputamount;
    public ImageButton                  ImageBtn;
    private ProgressDialog              pDialog;
    private Button                      pay_final;
    private final String                NOTIFICATION_TYPE = "PAYMENT";
    private TextView                    name, eventname, date, amt, status, payment_method;
    private Boolean flag = false;
    private RadioGroup Payment_Method_Radio_Group;

    int checked=0;

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

        IntentEventName = intent.getStringExtra("TAB2_EVENTNAME");
        IntentDate = intent.getStringExtra("TAB2_DATE");
        IntentAmount = intent.getStringExtra("TAB2_AMOUNT");
        IntentOwner = intent.getStringExtra("TAB2_OWNER");
        IntentStatus = intent.getStringExtra("TAB2_STATUS");
        IntentContractAddress = intent.getStringExtra("TAB2_CONTRACT_ADD");

        name = (TextView)findViewById(R.id.rname);
        pay_final = (Button)findViewById(R.id.paybuttonfinal);
        eventname = (TextView)findViewById(R.id.ename);
        date = (TextView)findViewById((R.id.date));
        amt = (TextView)findViewById(R.id.amt);
        status = (TextView)findViewById(R.id.status);
        ImageBtn = (ImageButton)findViewById(R.id.imageButton);
        Inputamount = (EditText)findViewById(R.id.inputamount);
        payment_method = (TextView)findViewById(R.id.textView19);
        Payment_Method_Radio_Group = (RadioGroup)findViewById(R.id.myRadioGroup);

        name.setText(IntentOwner);
        eventname.setText(IntentEventName);
        date.setText(IntentDate);
        amt.setText(IntentAmount);


        if(IntentStatus.toString().trim().equals("0")) {
            status.setText("Not settled".toUpperCase());
            amt.setTextColor(Color.RED);
            status.setTextColor(Color.parseColor("#b52121"));
        }
        else if(IntentStatus.toString().trim().equals("1")){
            status.setText("has been settled");
            status.setTextColor(Color.parseColor("#FF119100"));
        }
        Payment_Method_Radio_Group.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if(i == R.id.xfers){
                    ImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.xfers_png));
                    checked = 1;
                }
                else if( i == R.id.cash){
                    ImageBtn.setImageDrawable(getResources().getDrawable(R.drawable.cash2));
                    checked = 2;
                }
            }
        });
        pay_final.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String amount= Inputamount.getText().toString().trim();
                double amount_check = Double.parseDouble(amount);
                final Intent intent = new Intent(ContractDetails.this, Xfers.class);

                if(checked == 1 && amount_check <= Double.parseDouble(IntentAmount) && !session.returnXferApiKey().toString().trim().equals("notset"))
                    CreateXferPayout(Inputamount.getText().toString().trim());
                else if(checked == 2 && amount_check <= Double.parseDouble(IntentAmount))
                    CreateCashPayout(Inputamount.getText().toString().trim());
                else if(checked == 0)
                    Toast.makeText(ContractDetails.this, "Please select a payment method first!!", Toast.LENGTH_SHORT).show();
                else if(session.returnXferApiKey().toString().trim().equals("notset")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(ContractDetails.this);
                    builder.setMessage("Please enter your XFers API Token before making an Xfers Payment.")
                            .setCancelable(false)
                            .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    startActivity(intent);
                                }
                            });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else if(amount_check <= Double.parseDouble(IntentAmount))
                    Toast.makeText(ContractDetails.this, "Amount cannot be greater than Contract Amount", Toast.LENGTH_SHORT).show();

            }
        });
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here

        int id = item.getItemId();
        session = new SessionManager(this);
        Intent intent = new Intent(ContractDetails.this, Xfers.class);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {//this is the logout button
            this.session.logoutUser();
            this.finish();
            return true;
        }
        else if(id == R.id.action_manage){//this is manage profile activity
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_contract_details, menu);
        return true;
    }
    public void Send_Notification(String str){
        String apiKey = "AIzaSyCHslDzvLhkgY_k-J5C_us2T7YHhMgJabw";
        if(!str.toString().trim().equals("Payment has been successful")) {
            Content content = createContent(str);
            Log.d(TAG, "inside method send notification");
            POST2GCM.post(apiKey, content);
        }
    }
    public Content createContent(String str){
        //get notification id of other user.
        Content c = new Content();
        c.addRegId(str);
        String hello ="";

        if(checked ==2)
            hello = "Cash";
        else if(checked ==1)
            hello ="Xfers";

        c.createData(NOTIFICATION_TYPE, IntentOwner.toUpperCase()
                + " has settled the payment of "
                + Inputamount.getText().toString().trim()
                + " for " + IntentEventName.toUpperCase()
                + " by "
                + hello
                + ". Please tap on this balloon to open! " );
        return c;
    }

    private void GetNotificationID() {
        RequestQueue queue = Volley.newRequestQueue(ContractDetails.this);
        String url = "http://23.97.60.51/bayar_mysql/get_noti_id.php?username="+ IntentOwner;
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

        Log.d(TAG, "inside method");
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Paid with Cash..Please wait");
        pDialog.show();
        queue2 = Volley.newRequestQueue(getBaseContext());

        RequestQueue queue = Volley.newRequestQueue(this);
        String url2 = getResources().getString(R.string.apiurl) +
                "control=pay"       +
                "&paymode=cash"     +
                "&genname="         + hash.get(SessionManager.KEY_NAME)              +
                "&password="        + hash.get(SessionManager.KEY_PASS)              +
                "&amount="          + Inputamount.getText().toString().trim()        +
                "&recname="         + IntentOwner                                    +
                "&id="              + IntentContractAddress;

        Log.d("Xfers Pay Link", url2);
        StringRequest req = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, response.toString().trim());
                GetNotificationID();
                hidePDialog();

                AlertDialog.Builder builder = new AlertDialog.Builder(ContractDetails.this);
                builder.setMessage(response.toString().trim())
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                session.storeReturnKey("done");
                                ContractDetails.this.finish();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
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
    }

    private void CreateXferPayout(String amount) {

        pDialog = new ProgressDialog(this);
        // Showing progsress dialog before making http request
        pDialog.setMessage("Paying Via Xfers");
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url2 = getResources().getString(R.string.apiurl) +
                "control=pay"       +
                "&paymode=xfers"    +
                "&genname="         + hash.get(SessionManager.KEY_NAME)              +
                "&password="        + hash.get(SessionManager.KEY_PASS)              +
                "&amount="          + Inputamount.getText().toString().trim()        +
                "&genAPIKey="       + session.returnXferApiKey().toString().trim()   +
                "&recname="         + IntentOwner                                           +
                "&id="              + IntentContractAddress;

        Log.d("Xfers Pay Link", url2);
        StringRequest req = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {

            public void onResponse(String response) {
                Log.d(TAG, response.toString().trim());
                GetNotificationID();
                hidePDialog();

                AlertDialog.Builder builder = new AlertDialog.Builder(ContractDetails.this);
                builder.setMessage(response.toString().trim())
                        .setCancelable(false)
                        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                session.storeReturnKey("donefrompay");
                                ContractDetails.this.finish();
                            }
                        });

                AlertDialog alert = builder.create();
                alert.show();
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
