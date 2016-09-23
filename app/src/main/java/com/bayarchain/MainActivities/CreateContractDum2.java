package com.bayarchain.MainActivities;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
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
import com.bayarchain.Adapters.CustomListAdapter;
import com.bayarchain.Adapters.CustomListAdapterWithDelete;
import com.bayarchain.Model.ID;
import com.bayarchain.Push.Content;
import com.bayarchain.Push.POST2GCM;
import com.bayarchain.R;
import com.bayarchain.SessionManagement.SessionManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by Aditya Aggarwal on 21-08-2016.
 */
public class CreateContractDum2 extends ActionBarActivity {
    public static final String TAG = "TAG";
    public static final String NOTIFICATION_TYPE = "CONTRACT";
    private ArrayList<ID> IDList;
    private CustomListAdapter cust_adap;
    private HashMap<String, String> hash;
    private SessionManager session;
    private String received_contract_address, date;
    public int year, day, month;
    private ProgressDialog pDialog;
    private String final_date;
    public String not_id, str1, str2, str_name;
    private EditText expense_name;
    private EditText amount;
    private Button date_selector, add_friend, create_contract_button, scan_button;
    private ListView friend_listview;
    private String[] products = {"Aditya", "Gunj", "Manish"};
    private CustomListAdapterWithDelete friend_adap;
    private ArrayList<ID> friend_list;
    private int progressDialogChecker = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_contract);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        expense_name = (EditText) findViewById(R.id.editText3);
        amount = (EditText) findViewById(R.id.editText4);
        date_selector = (Button) findViewById(R.id.button3);
        add_friend = (Button) findViewById(R.id.addfriends);
        friend_listview = (ListView) findViewById(R.id.listView3);
        create_contract_button = (Button) findViewById(R.id.create_contract_button);
        session = new SessionManager(this);
        hash = session.getUserDetails();
        IDList = new ArrayList<ID>();
        friend_list = new ArrayList<ID>();
        friend_adap = new CustomListAdapterWithDelete(CreateContractDum2.this, friend_list);
        scan_button = (Button) findViewById(R.id.scan_button);

        final Intent intent_start_scan = new Intent(this, com.bayarchain.OCR.OcrCaptureActivity.class);

        add_friend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog book = new Dialog(CreateContractDum2.this);
                book.requestWindowFeature(Window.FEATURE_NO_TITLE);

                book.setContentView(R.layout.dialog_list_view);
                EditText search = (EditText) book.findViewById(R.id.editText5);

                pDialog = new ProgressDialog(CreateContractDum2.this);
                // Showing progsress dialog before making http request
                pDialog.setMessage("Loading...");
                pDialog.show();
                CallThread();
                cust_adap = new CustomListAdapter(CreateContractDum2.this, IDList);
                final ListView listView = (ListView) book.findViewById(R.id.listView);
                listView.setAdapter(cust_adap);
                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int textlength = charSequence.length();
                        ArrayList<ID> tempArrayList = new ArrayList<ID>();
                        for (ID c : IDList) {
                            if (textlength <= c.getName().length()) {
                                if (c.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                    tempArrayList.add(c);
                                }
                            }
                        }
                        cust_adap = new CustomListAdapter(CreateContractDum2.this, tempArrayList);
                        listView.setAdapter(cust_adap);
                        IDList = tempArrayList;
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        str_name = IDList.get(i).getName();
                        str1 = IDList.get(i).getUsername();
                        not_id = IDList.get(i).getNoti_id();
                        str2 = IDList.get(i).getAddress();

                        friend_list.add(IDList.get(i));
                        friend_listview.setAdapter(friend_adap);
                        book.dismiss();
                    }
                });
                book.show();
            }
        });
        date_selector.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Dialog book = new Dialog(CreateContractDum2.this);
                book.setContentView(R.layout.date);
                DatePicker calender = (DatePicker) book.findViewById(R.id.datePicker);
                String date = calender.getDayOfMonth() + "-" + calender.getMonth() + "-" + calender.getYear();
                date_selector.setText(date);
                final_date = date;
                Calendar c = Calendar.getInstance();

                calender.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                        year = i; //year
                        month = (i1 + 1);//month+
                        day = i2;//day
                        String ddate = String.valueOf(i) + "-" + String.valueOf(i1 + 1) + "-" + String.valueOf(i2);
                        final_date = ddate;
                        Log.d("final_date", final_date);
                        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

                        Date date = null;
                        try {
                            date = fmt.parse(final_date);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        SimpleDateFormat fmtOut = new SimpleDateFormat("MMMM dd");
                        String frm = fmtOut.format(date);
                        Log.d(TAG, ddate);
                        date_selector.setText(frm);
                        book.dismiss();
                    }
                });
                final_date = date_selector.getText().toString().trim();

                Log.d("Updated Date", date);
                book.show();
                final_date = date_selector.getText().toString().trim();
            }

        });

        create_contract_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //CreateContractMethod();
                MultipleFriendsControllr();
            }
        });
        scan_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(intent_start_scan, 1);
            }
        });

    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                String result = data.getStringExtra("result");
                amount.setText(result);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Write your code if there's no result
            }
        }
    }

    public void CallThread() {
        RequestQueue queue = Volley.newRequestQueue(CreateContractDum2.this);
        IDList.clear();
        String url = "http://bayarchain.southeastasia.cloudapp.azure.com/bayar_mysql/list_of_users.php";
        JsonArrayRequest movieReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString().trim());
                hidePDialog();
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        ID id = new ID();

                        id.setUsername(obj.getString("username"));
                        id.setAddress(obj.getString("id"));
                        id.setName(obj.getString("name"));
                        id.setNoti_id(obj.getString("noti_id"));
                        if (!obj.getString("username").equals(hash.get(SessionManager.KEY_NAME)))
                            IDList.add(id);

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                cust_adap.notifyDataSetChanged();
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

    public void MultipleFriendsControllr() {

        RequestQueue queue = Volley.newRequestQueue(CreateContractDum2.this);
        IDList.clear();
        StringRequest[] movieReq = new StringRequest[5];

        pDialog = new ProgressDialog(CreateContractDum2.this);
        pDialog.setMessage("Loading...");
        pDialog.show();

        for (int i = 0; i < friend_list.size(); i++) {
            String event_Name = this.expense_name.getText().toString().trim();
            String receiver_name = friend_list.get(i).getUsername().toString().trim();
            String receiver_username = friend_list.get(i).getUsername().toString().trim();
            String receiver_address = friend_list.get(i).getAddress().toString().trim();
            String receiver_notification_id = friend_list.get(i).getNoti_id().toString().trim();
            movieReq[i] = MultipleFreinds(receiver_name, receiver_notification_id, event_Name, receiver_notification_id);
            queue.add(movieReq[i]);
        }
        String ev_name = this.expense_name.getText().toString().trim();
    }

    public StringRequest MultipleFreinds(final String uname, final String noti_id, String ename, final String noti) {

        String url2 = getResources().getString(R.string.apiurl) +
                "control=create" +
                "&genname=" + hash.get(SessionManager.KEY_NAME) +
                "&password=" + hash.get(SessionManager.KEY_PASS) +
                "&recname=" + uname +
                "&amount=" + String.valueOf(Double.parseDouble(amount.getText().toString()) / (friend_list.size() + 1)) +
                "&timestamp=" + final_date +
                "&eventName=" + this.expense_name.getText().toString().trim();

        StringRequest global = null;
        Log.d(TAG, url2);
        if (!amount.equals(null) && !ename.equals(null) && !uname.equals(null) && !noti_id.equals(null)) {
            StringRequest createContract = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {
                public void onResponse(String response) {

                    progressDialogChecker = progressDialogChecker + 1;
                    received_contract_address = response.toString().trim();
                    checkAndCloseProgressDialog(progressDialogChecker);

                    Log.d(TAG, response.toString().trim() + response.toString().trim().length());
                    if (response.toString().trim().length() == 40) {

                        Log.d("progressDialogChecker", String.valueOf(progressDialogChecker));

                        Send_Notification(received_contract_address, noti);
                        //Toast.makeText(CreateContractDum2.this, "Contract Created, your friend "+ uname + " has been notified", Toast.LENGTH_SHORT).show();
                        Toast.makeText(CreateContractDum2.this, "Waiting for your friend " + uname + " to Acknowledge the Expense", Toast.LENGTH_SHORT).show();

                        Log.d("Response of create contract", received_contract_address);

                    } else {
                        Toast.makeText(CreateContractDum2.this, "There was an error adding this contract, Please try again later", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                    progressDialogChecker = progressDialogChecker + 1;
                    checkAndCloseProgressDialog(progressDialogChecker);
                }
            });
            createContract.setRetryPolicy(new DefaultRetryPolicy(20000, friend_list.size(), DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            global = createContract;
        }
        return global;
    }

    private void checkAndCloseProgressDialog(int progressDialogChecker) {

        if (progressDialogChecker == friend_list.size()) {
            hidePDialog();
            progressDialogChecker = 0;
            CreateContractDum2.this.finish();
            session.storeReturnKey("donefromcreatecontract");
        }
    }

    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    public void Send_Notification(String str, String noti) {
        String apiKey = "AIzaSyCHslDzvLhkgY_k-J5C_us2T7YHhMgJabw";
        Content content = createContent(str, noti);
        Log.d(TAG, "CreateContractDummy Inside method send notification");
        POST2GCM.post(apiKey, content);
    }

    public Content createContent(String str, String noti) {

        Content c = new Content();
        c.addRegId(noti);
        c.createDataForContract(NOTIFICATION_TYPE, "Hello!! Expense: " + expense_name.getText().toString().trim() + " added for the amount " + amount.getText().toString().trim() + " by "+ hash.get(SessionManager.KEY_NAME) + " .Please Tap this balloon to confirm this contract", str);
        Log.d(TAG, "App.java" + c.data);
        return c;
    }

}