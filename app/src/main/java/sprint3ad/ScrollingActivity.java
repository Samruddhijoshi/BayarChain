package sprint3ad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bayarchain.ContractDetails;
import com.bayarchain.CreateContractDum2;
import com.bayarchain.R;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Adapters.CustomListAdapter2;
import Model.Contract;
import SessionManagement.SessionManager;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    private Button credit,debit;
    private ListView listView;
    TextView fullname_appbar, username_appbar;
    CustomListAdapter2 cust_adap;
    ArrayList<Contract> ContractList;
    ProgressDialog pDialog;
    final String TAG = "SECTION2_DUMMY";
    HashMap<String, String> map;
    private ArrayAdapter<String> adapter;
    private String[] products = {"Tap Credit/Debit to Refresh"};
    ShowcaseView sv;
    private SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        session = new SessionManager(this);

        RelativeLayout.LayoutParams lps = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lps.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lps.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        int margin = ((Number) (getResources().getDisplayMetrics().density * 12)).intValue();
        lps.setMargins(margin, margin, margin, margin);

        View showcasedView = findViewById(R.id.fab);
        ViewTarget target = new ViewTarget(showcasedView);
        if(session.getHelpScreen() == 0) {
            sv = new ShowcaseView.Builder(this)
                    .withMaterialShowcase()
                    .setStyle(R.style.CustomShowcaseTheme3)
                    .setTarget(target)
                    .setContentTitle("Add Expense")
                    .setContentText("Tap the '+' Button to add a new Expense!!")
                    .build();
            sv.setButtonPosition(lps);
            session.storehelpScreen(1);
        }

        credit = (Button)findViewById(R.id.credit);
        debit = (Button)findViewById(R.id.debit);

        listView = (ListView)findViewById(R.id.listView);
        ContractList = new ArrayList<Contract>();
        fullname_appbar = (TextView)findViewById(R.id.fullName_activityscrolling);
        username_appbar = (TextView)findViewById(R.id.username_activityscrolling);


        session.checkLogin();

        map = new HashMap<String, String>();
        session = new SessionManager(this);
        map = session.getUserDetails();
        if(!session.isLoggedIn()){
            this.finish();
        }
        else
        {
            fullname_appbar.setText(session.getUserDetails().get("name").toUpperCase());
            username_appbar.setText(session.getUserDetails().get("name"));
        }
        adapter = new ArrayAdapter<String>(ScrollingActivity.this, android.R.layout.simple_list_item_1, products);
        listView.setAdapter(adapter);
        cust_adap = new CustomListAdapter2(this , ContractList, "credit");

        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cust_adap = new CustomListAdapter2(ScrollingActivity.this , ContractList, "credit");
                ContractList.clear();
                CallThread("credit");
                listView.setAdapter(cust_adap);
                cust_adap.notifyDataSetChanged();
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    }
                });
            }
        });
        debit.setOnClickListener(this);
        final Intent intent = new Intent(this, CreateContractDum2.class);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Tap to create Contract", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            startActivity(intent);
            }
        });
    }

    public void CallThread(final String check){ //credit call Thread

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Credit List, Please wait...");
        pDialog.setCancelable(false);
        pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel Loading", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url ="";
        url = "http://bayarchain.southeastasia.cloudapp.azure.com/pri.php?control=myContract&genname="
                + map.get(SessionManager.KEY_NAME)
                +"&password=" + map.get(SessionManager.KEY_PASS);

        Log.d("Credit link", url);
        ContractList.clear();
        JsonArrayRequest movieReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString().trim());
                hidePDialog();
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Contract contract = new Contract();
                        contract.setContract_address(obj.getString("contractID"));
                        contract.setContract_amount(obj.getString("amount"));
                        contract.setCreator_username(obj.getString("owner"));
                        contract.setContract_status(obj.getString("status"));
                        contract.setContract_timestamp(obj.getString("timestamp"));
                        contract.setContract_event(obj.getString("event"));
                        contract.setContract_principal(obj.getString("total"));
                        ContractList.add(contract);

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
                hidePDialog();
            }
        });
        movieReq.setRetryPolicy(new DefaultRetryPolicy(80000,	0, 1.0f));

        queue.add(movieReq);
    }
    public void CallThread2(final String check){
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading Debit List, Please wait..");
        pDialog.setCancelable(false);
        pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel Loading", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        pDialog.show();

        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="";
        url = "http://bayarchain.southeastasia.cloudapp.azure.com/pri.php?control=allContract" +
                "&genname="    + map.get(SessionManager.KEY_NAME) +
                "&password=" + map.get(SessionManager.KEY_PASS);
        Log.d("Credit link", url);
        ContractList.clear();
        JsonArrayRequest movieReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString().trim());
                hidePDialog();
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Contract contract = new Contract();
                        contract.setContract_address(obj.getString("contractID"));
                        contract.setContract_amount(obj.getString("amount"));
                        contract.setCreator_username(obj.getString("owner"));
                        contract.setContract_status(obj.getString("status"));
                        contract.setContract_timestamp(obj.getString("timestamp"));
                        contract.setContract_event(obj.getString("event"));
                        contract.setContract_principal(obj.getString("total"));

                        ContractList.add(contract);
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
                hidePDialog();
            }
        });
        movieReq.setRetryPolicy(new DefaultRetryPolicy(80000,	0, 1.0f));
        queue.add(movieReq);
    }
    public void CallThread3(final String check){
        RequestQueue queue = Volley.newRequestQueue(this);
        String url ="";
        url = "http://bayarchain.southeastasia.cloudapp.azure.com/pri.php?control=allContract" +
                "&genname="    + map.get(SessionManager.KEY_NAME) +
                "&password=" + map.get(SessionManager.KEY_PASS);
        Log.d("Credit link", url);
        ContractList.clear();
        JsonArrayRequest movieReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {

            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString().trim());
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        Contract contract = new Contract();
                        contract.setContract_address(obj.getString("contractID"));
                        contract.setContract_amount(obj.getString("amount"));
                        contract.setCreator_username(obj.getString("owner"));
                        contract.setContract_status(obj.getString("status"));
                        contract.setContract_timestamp(obj.getString("timestamp"));
                        contract.setContract_event(obj.getString("event"));
                        contract.setContract_principal(obj.getString("total"));

                        ContractList.add(contract);
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
            }
        });
        movieReq.setRetryPolicy(new DefaultRetryPolicy(80000,	0, 1.0f));
        queue.add(movieReq);
    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scrolling, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        session = new SessionManager(this);
        Intent intent = new Intent(ScrollingActivity.this, Xfers.class);
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {//this is the logout button
            this.session.logoutUser();
            return true;
        }
        else if(id == R.id.action_manage){//this is manage profile activity
            //.makeText(ScrollingActivity.this, "Currently not available", Toast.LENGTH_SHORT).show();
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        cust_adap = new CustomListAdapter2(ScrollingActivity.this , ContractList, "debit");
        ContractList.clear();
        CallThread2("debit");
        listView.setAdapter(cust_adap);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ScrollingActivity.this, ContractDetails.class);
                Contract con = new Contract();
                con = ContractList.get(position);
                intent.putExtra("TAB2_EVENTNAME", con.getContract_event());
                intent.putExtra("TAB2_AMOUNT", con.getContract_amount());
                intent.putExtra("TAB2_DATE", con.getContract_timestamp());
                intent.putExtra("TAB2_OWNER", con.getCreator_username());
                intent.putExtra("TAB2_STATUS", con.getContract_status());
                intent.putExtra("TAB2_CONTRACT_ADD", con.getContract_address());
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onResume() {
        super.onResume();
        String retrunkey = session.getRetrunKey();

        if(retrunkey.toString().trim().equals("done")){
        CallThread2("debit");

        listView.setAdapter(cust_adap);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ScrollingActivity.this, ContractDetails.class);
                Contract con = new Contract();
                con = ContractList.get(position);
                intent.putExtra("TAB2_EVENTNAME", con.getContract_event());
                intent.putExtra("TAB2_AMOUNT"   , con.getContract_amount());
                intent.putExtra("TAB2_DATE"     , con.getContract_timestamp());
                intent.putExtra("TAB2_OWNER"    , con.getCreator_username());
                intent.putExtra("TAB2_STATUS"   , con.getContract_status());
                intent.putExtra("TAB2_CONTRACT_ADD", con.getContract_address());
                startActivity(intent);
            }
        });
    }
    }
}
