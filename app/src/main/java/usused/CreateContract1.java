package usused;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.bayarchain.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Adapters.CustomListAdapter;
import Model.ID;
import Push.Content;
import Push.POST2GCM;
import SessionManagement.SessionManager;

/**
 * Created by Aditya Aggarwal on 19-08-2016.
 */
public class CreateContract1 extends Activity {
    private ArrayAdapter<String> adapter;
    private TextView eventName, splitbill, user, user2, user3;
    private EditText eventNameInput,amountSplit;
    private Button createEvent,split, createContract, testButton;
    private String[] products = {"Aditya","Gunj", "Manish"};
    public ArrayList<ID> IDList;
    public static final String TAG = "TAG" ;
    public CustomListAdapter cust_adap;
    HashMap<String, String> hash;
    SessionManager session;
    String received_contract_address, date;
    int year,day,month;
    ProgressDialog pDialog;
    String final_date;

    //SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

    String not_id, str1, str2,str_name;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.fragment_main);
        eventNameInput = (EditText)findViewById(R.id.editText1);
        amountSplit = (EditText)findViewById(R.id.editText2);
        splitbill = (TextView)findViewById(R.id.textView11);
        split = (Button)findViewById(R.id.button2);
        createContract = (Button)findViewById(R.id.createContract);
        adapter = new ArrayAdapter<String>(this, R.layout.list_item,R.id.product_name, products);
        testButton = (Button)findViewById(R.id.testbutton);
        user = (TextView)findViewById(R.id.textView9);
        user2 = (TextView)findViewById(R.id.textView11);
        user3 = (TextView)findViewById(R.id.textView);
        session = new SessionManager(this);
        hash = session.getUserDetails();
        IDList = new ArrayList<ID>();
        testButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Dialog book = new Dialog(CreateContract1.this);
                book.requestWindowFeature(Window.FEATURE_NO_TITLE);

                book.setContentView(R.layout.dialog_list_view);
                EditText search = (EditText)book.findViewById(R.id.editText5);

                pDialog = new ProgressDialog(CreateContract1.this);
                // Showing progsress dialog before making http request
                pDialog.setMessage("Loading...");
                pDialog.show();
                CallThread();
                cust_adap = new CustomListAdapter(CreateContract1.this,  IDList);
                final ListView listView = (ListView)book.findViewById(R.id.listView);
                listView.setAdapter(cust_adap);
                search.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                        int textlength = charSequence.length();
                        ArrayList<ID> tempArrayList = new ArrayList<ID>();
                        for(ID c: IDList){
                            if (textlength <= c.getName().length()) {
                                if (c.getName().toLowerCase().contains(charSequence.toString().toLowerCase())) {
                                    tempArrayList.add(c);
                                }
                            }
                        }
                        cust_adap = new CustomListAdapter(CreateContract1.this, tempArrayList);
                    listView.setAdapter(cust_adap);
                    //CreateContract1.this.adapter.getFilter().filter(charSequence);
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {

                    }
                });
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                        str_name = IDList.get(i).getName();
                        user.setText("Went out for a " + eventNameInput.getText().toString() +" with "+IDList.get(i).getName() + "\n and we spent");
                        str1 = IDList.get(i).getUsername();
                        not_id = IDList.get(i).getNoti_id();
                        str2 = IDList.get(i).getAddress();
                        book.dismiss();
                        user3.setText("    ..So " + IDList.get(i).getName() + " pays his fair share which is ");
                    }
                });
                book.show();
            }
        });
        createContract.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                final Dialog book = new Dialog(CreateContract1.this);
                book.setContentView(R.layout.date);
                DatePicker calender = (DatePicker)book.findViewById(R.id.datePicker);
                String date = calender.getDayOfMonth() + "-" + calender.getMonth()+ "-" + calender.getYear();
                final_date = date;
                calender.init(2016, 01, 01, new DatePicker.OnDateChangedListener() {
                    @Override
                    public void onDateChanged(DatePicker datePicker, int i, int i1, int i2) {
                        year = i;
                        month = i1;
                        day = i2;
                        String ddate = String.valueOf(i) + "-" + String.valueOf(i1) + "-" + String.valueOf(i2);
                        final_date = ddate;
                        Log.d(TAG, ddate);
                    }
                });
                Log.d("Updated Date", date);

                book.show();
            }

        });

        split.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                if((!amountSplit.getText().toString().equals(""))) {
                    int var = (Integer.parseInt(amountSplit.getText().toString().trim()) / 2);
                    splitbill.setText("$ "+ Integer.toString(var));
                }
                else
                    Toast.makeText(CreateContract1.this, "Please enter the amount to be split", Toast.LENGTH_LONG);
            }

        });

    }
    private void CreateContractMethod() {
        Log.d(TAG, "inside method");
        String amount = this.splitbill.getText().toString().trim();
        if(amount!= null)
            Log.d("Amount" , amount + amount.toString().substring(1, amount.toString().length()).trim() );
        else
            Toast.makeText(CreateContract1.this, "Enter the amount to be spilt", Toast.LENGTH_SHORT).show();

        String event_Name = this.eventNameInput.getText().toString().trim();
        String receiver_name = str_name;//this.user.getText().toString().trim();
        String receiver_username = str1; //this.user2.getText().toString().trim();
        String receiver_address = str2; //this.user3.getText().toString().trim();
        String receiver_notification_id = this.not_id;
        String ev_name = eventNameInput.getText().toString().trim();
        pDialog = new ProgressDialog(CreateContract1.this);
        // Showing progsress dialog before making http request
        pDialog.setMessage("Loading...");
        pDialog.show();
        RequestQueue queue2 = Volley.newRequestQueue(CreateContract1.this);

        String url2 = "http://bayarchain.southeastasia.cloudapp.azure.com/test8sprint.php?" +
                "control=create"+
                "&genname=" 	+ hash.get(SessionManager.KEY_NAME) +
                "&password=" 	+ hash.get(SessionManager.KEY_PASS) +
                "&recname=" 	+ receiver_username.trim().toString() +
                "&amount=" 		+ amount.toString().substring(1, amount.toString().length()).trim() +
                "&timestamp=" 	+ final_date +
                "&eventName="   + ev_name;

        Log.d(TAG, url2);
        if (!amount.equals(null) && !event_Name.equals(null) && !receiver_name.equals(null) && !receiver_address.equals(null) && !receiver_notification_id.equals(null)) {
            StringRequest createContract = new StringRequest(Request.Method.GET, url2, new Response.Listener<String>() {

                public void onResponse(String response) {
                    Log.d(TAG, response.toString().trim());
                    hidePDialog();
                    received_contract_address = response.toString().trim();
                    Send_Notification(received_contract_address);
                    Toast.makeText(CreateContract1.this, "Contract Created, your friend has been notified", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, received_contract_address);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    VolleyLog.d(TAG, "Error: " + error.getMessage());
                }
            });
            createContract.setRetryPolicy(new DefaultRetryPolicy(20000,	0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue2.add(createContract);

        }
    }

    public void CallThread(){
        RequestQueue queue = Volley.newRequestQueue(CreateContract1.this);
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
    public void Send_Notification(String str){
        String apiKey = "AIzaSyCHslDzvLhkgY_k-J5C_us2T7YHhMgJabw";
        Content content = createContent(str);
        Log.d(TAG, "inside method send notification");
        POST2GCM.post(apiKey, content);
    }
    public  Content createContent(String str){

        Content c = new Content();
        c.addRegId(not_id);
        c.createData("message", "Contract ID: "+ str);
        Log.d(TAG, "App.java" + c.data);
        return c;
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
}

