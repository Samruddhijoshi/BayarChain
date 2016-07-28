package Fragments;


import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.graphics.Color;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bayarchain.ContractDetails;
import com.bayarchain.R;
import com.bayarchain.TabActivity;
import com.bayarchain.loginPage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import Adapters.CustomListAdapter2;
import Model.Contract;
import SessionManagement.SessionManager;

/**
 * Created by Adi_711 on 06-06-2016.
 */

public class Tab2_alter extends Fragment {

    ListView listview;
    Button credit, debit, logout;
    CustomListAdapter2 cust_adap;
    ArrayList<Contract> ContractList;
    ProgressDialog pDialog;
    final String TAG = "SECTION2_DUMMY";
    SessionManager session;
    HashMap<String, String> map;
    private ArrayAdapter<String> adapter;
    TextView name2;
    private CloseActivity mCallback;
    private String[] products = {"Tap Credit/Debit to Refresh"};
    ObjectAnimator textColorAnim;
    ValueAnimator background;


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.section2_dummy, container, false);
        map = new HashMap<String, String>();
        session = new SessionManager(getContext());
        map = session.getUserDetails();
        credit = (Button)view.findViewById(R.id.creditBtn);
        debit = (Button)view.findViewById(R.id.debitBtn);
        ContractList = new ArrayList<Contract>();
        listview = (ListView)view.findViewById(R.id.listView2);
        name2= (TextView)view.findViewById(R.id.name2);
        logout = (Button) view.findViewById(R.id.logout2);
        adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, products);
        listview.setAdapter(adapter);
        name2.setText((map.get(SessionManager.KEY_NAME)));

        cust_adap = new CustomListAdapter2(getActivity() , ContractList, "credit");
        //CallThread("credit");
        credit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                debit.setBackgroundColor(0xffffff);
                credit.setBackgroundColor(Color.parseColor("#91ffffff"));

                cust_adap = new CustomListAdapter2(getActivity() , ContractList, "credit");
                ContractList.clear();
                CallThread("credit");
                listview.setAdapter(cust_adap);
            }
        });
        debit.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                credit.setBackgroundColor(0xffffff);
                debit.setBackgroundColor(Color.parseColor("#91ffffff"));

                cust_adap = new CustomListAdapter2(getActivity() , ContractList, "debit");
                ContractList.clear();
                CallThread2("debit");
                listview.setAdapter(cust_adap);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent intent = new Intent(getActivity(), ContractDetails.class);
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
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                session.logoutUser();
                getActivity().finish();
                Intent intent = new Intent(getContext(), TabActivity.class);
                intent.putExtra("LOGOUTUSER", "logout");
            }
        });

        return view;
    }
    public void CallThread(final String check){

        pDialog = new ProgressDialog(getContext());
        // Showing progsress dialog before making http request
        pDialog.setMessage("Loading Credit List, Please wait...");
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());

        String url ="";
        url = "http://bayarchain.southeastasia.cloudapp.azure.com/test8sprint.php?control=myContract&genname="
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
        pDialog = new ProgressDialog(getContext());
        pDialog.setMessage("Loading Debit List, Please wait...");
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(getContext());
        String url ="";
        url = "http://bayarchain.southeastasia.cloudapp.azure.com/test8sprint.php?control=allContract" +
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
                        ContractList.add(contract);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
				/*Collections.sort(ContractList, new Comparator<Contract>(){

					@Override
					public int compare(Contract contract, Contract t1) {
						int i = Integer.parseInt(contract.getContract_status());
						int j = Integer.parseInt(t1.getContract_status());
						return String.valueOf(i).compareToIgnoreCase(String.valueOf(j));
					}
				});*/
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
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }
    public interface CloseActivity{
        public void Close();
    }
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        // This makes sure that the container activity has implemented
        // the callback interface. If not, it throws an exception
        try {
            mCallback = (CloseActivity) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

}
