package com.bayarchain.MainActivities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bayarchain.Adapters.CustomContractAdapter;
import com.bayarchain.Model.Contract;
import com.bayarchain.R;
import com.bayarchain.Recyclerview.DividerItemDecoration;
import com.bayarchain.Recyclerview.RecyclerTouchListener;
import com.bayarchain.SessionManagement.SessionManager;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ScrollingActivity extends AppCompatActivity implements View.OnClickListener {

    public final String TAG = "SECTION2_DUMMY";

    private Button credit,debit;
    private ListView listView;
    private TextView fullname_appbar, username_appbar;
    private ArrayList<Contract> ContractList;
    private ProgressDialog pDialog;
    private HashMap<String, String> map;
    private ArrayAdapter<String> adapter;
    private String[] products = {"Tap Credit/Debit to Refresh"};
    private ShowcaseView sv;
    private SessionManager session;
    private CustomContractAdapter cust_contract_adapter;
    private RecyclerView recycle;

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

        recycle = (RecyclerView)findViewById(R.id.recycler_view);
        recycle.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recycle.setItemAnimator(new DefaultItemAnimator());

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

        cust_contract_adapter = new CustomContractAdapter(ContractList, "debit");
        recycle.setHasFixedSize(true);
        recycle.setLayoutManager(new LinearLayoutManager(this));

        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cust_contract_adapter = new CustomContractAdapter(ContractList , "credit");
                ContractList.clear();
                CallThread("credit");
                recycle.setAdapter(cust_contract_adapter);
                cust_contract_adapter.notifyDataSetChanged();
                recycle.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycle, new RecyclerTouchListener.ClickListener() {
                    @Override
                    public void onClick(View view, int position) {

                    }

                    @Override
                    public void onLongClick(View view, int position) {

                    }
                }));
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
        url = getResources().getString(R.string.apiurl)
                +"control=credit&genname="
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
                cust_contract_adapter.notifyDataSetChanged();

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
        url = getResources().getString(R.string.apiurl)+
                "control=debit" +
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
                        if(!obj.getString("status").equals("2"))
                            ContractList.add(contract);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                cust_contract_adapter.notifyDataSetChanged();
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
            this.finish();
            return true;
        }
        else if(id == R.id.action_manage){//this is manage profile activity
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        cust_contract_adapter = new CustomContractAdapter(ContractList,"debit");
        ContractList.clear();
        CallThread2("debit");
        recycle.setAdapter(cust_contract_adapter);
        recycle.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycle, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Contract con = new Contract();
                con = ContractList.get(position);
                    Intent intent = new Intent(ScrollingActivity.this, ContractDetails.class);

                    intent.putExtra("TAB2_EVENTNAME", con.getContract_event());
                    intent.putExtra("TAB2_AMOUNT", con.getContract_amount());
                    intent.putExtra("TAB2_DATE", con.getContract_timestamp());
                    intent.putExtra("TAB2_OWNER", con.getCreator_username());
                    intent.putExtra("TAB2_STATUS", con.getContract_status());
                    intent.putExtra("TAB2_CONTRACT_ADD", con.getContract_address());
                    startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }


    @Override
    protected void onResume() {
        super.onResume();
        String retrunkey = session.getRetrunKey();

        if(retrunkey.toString().trim().equals("done")){
        CallThread2("debit");
        recycle.setAdapter(cust_contract_adapter);
            recycle.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recycle, new RecyclerTouchListener.ClickListener() {
                @Override
                public void onClick(View view, int position) {
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

                @Override
                public void onLongClick(View view, int position) {

                }
            }));
    }
    }
}
