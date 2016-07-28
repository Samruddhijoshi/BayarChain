package usused;


import android.app.Activity;
import android.app.ProgressDialog;
import android.support.v4.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import Model.Contract;
import Adapters.CustomListAdapter2;
import SessionManagement.SessionManager;


public class Tab2 extends Fragment{
	private static final String TAG = "TAGGGGG";
	private EditText username;
	private EditText password;
	private Button button1, logout;
	private TextView dummy, name, username2;
	private ListView list_contracts, list_contracts2;
	private String[] products = {"Tap Tab to refresh"};
	private ArrayAdapter<String> adapter;
	public CustomListAdapter2 cust_adap, cust_adap2;
	Context context;
	SessionManager session ;
	ArrayList<Contract> ContractList, ContractList2;
	HashMap<String,String> map;
	ProgressDialog pDialog;
	CloseActivity mCallback;
			int i = 0;
	TabHost host;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.section2, container, false);
		ContractList = new ArrayList<Contract>();
		ContractList2 = new ArrayList<Contract>();
		context = view.getContext();
		list_contracts = (ListView)view.findViewById(R.id.listView1);
		list_contracts2 = (ListView)view.findViewById(R.id.abcd);

		session = new SessionManager(getContext());
		logout = (Button)view.findViewById(R.id.logout);
		adapter = new ArrayAdapter<String>(context, R.layout.list_item, R.id.product_name, products);
		name = (TextView)view.findViewById(R.id.name);
		username2 = (TextView)view.findViewById(R.id.username1);
		map = session.getUserDetails();
		name.setText((map.get(SessionManager.KEY_NAME)));

		host = (TabHost)view.findViewById(R.id.tabHost);
		host.setup();
		//Tab 1
		TabHost.TabSpec spec = host.newTabSpec("Tab One");
		spec.setContent(R.id.listView1);
		cust_adap = new CustomListAdapter2(getActivity() , ContractList, "credit");
		//CallThread("credit");
		list_contracts.setAdapter(cust_adap);
		spec.setIndicator("Credit");

		host.addTab(spec);
		//Tab2
		spec = host.newTabSpec("Tab Two");
		spec.setContent(R.id.abcd);
		cust_adap2 = new CustomListAdapter2(getActivity() , ContractList2, "debit");
		spec.setIndicator("Debit");
		host.addTab(spec);



		host.setOnTabChangedListener(new TabHost.OnTabChangeListener() {
			@Override
			public void onTabChanged(String s) {
				Log.i("******Clickin Tab number ... ", "" + host.getCurrentTab());
				if(host.getCurrentTab() ==0){
					CallThread("credit");
					list_contracts.setAdapter(cust_adap);
				}
				else if(host.getCurrentTab() == 1){
					CallThread2("debit");
					list_contracts2.setAdapter(cust_adap2);
				}
				else
					Toast.makeText(getContext(), "Please Tap on either tab to refresh list", Toast.LENGTH_SHORT).show();
			}
		});

		logout.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				session.logoutUser();
				getActivity().finish();
				mCallback.Close();
			}
		});

		list_contracts.setAdapter(adapter);
		list_contracts2.setAdapter(adapter);
		list_contracts.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

			}

		});

		list_contracts2.setOnItemClickListener(new OnItemClickListener(){

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

		return view;

	}
	public void onCreate() {
		int i = host.getCurrentTab();
		if(i ==0){
			//CallThread("credit");
			//list_contracts.setAdapter(cust_adap);
		}
		super.onResume();
		Log.d("DEBUG", "onResume of LoginFragment");
		//CallThread("credit");

	}
	public void CallThread(final String check){

		pDialog = new ProgressDialog(getContext());
		// Showing progsress dialog before making http request
		pDialog.setMessage("Loading please wait");
		pDialog.show();
		RequestQueue queue = Volley.newRequestQueue(getContext());

		String url ="";
		url = "http://bayarchain.southeastasia.cloudapp.azure.com/test8sprint.php?control=myContract&genname="+ map.get(SessionManager.KEY_NAME) +"&password=12345";
		Log.d("Credit link", url);
		ContractList.clear();
		ContractList2.clear();
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
		movieReq.setRetryPolicy(new DefaultRetryPolicy(800000, 0, 1.0f));
		queue.add(movieReq);
	}
	public void CallThread2(final String check){
		pDialog = new ProgressDialog(getContext());
		pDialog.setMessage("Loading...");
		pDialog.show();
		RequestQueue queue = Volley.newRequestQueue(getContext());
		String url ="";
		url = "http://bayarchain.southeastasia.cloudapp.azure.com/test8sprint.php?control=allContract&genname="+map.get(SessionManager.KEY_NAME) +"&password=12345";
		Log.d("Credit link", url);
		ContractList.clear();
		ContractList2.clear();
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
						ContractList2.add(contract);
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
					cust_adap2.notifyDataSetChanged();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				VolleyLog.d(TAG, "Error: " + error.getMessage());
				hidePDialog();
			}
		});
		movieReq.setRetryPolicy(new DefaultRetryPolicy(800000, 0, 1.0f));
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