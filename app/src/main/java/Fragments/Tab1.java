package Fragments;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
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
import java.util.Date;
import java.util.HashMap;

import Adapters.CustomListAdapter;
import Model.ID;
import Push.Content;
import Push.POST2GCM;
import SessionManagement.SessionManager;

public class Tab1 extends Fragment{
	private ArrayAdapter<String> adapter;
	private Context context;
	private TextView  eventName, splitbill, user, user2, user3;
	private EditText eventNameInput,amountSplit;
	private Button createEvent,split, createContract, testButton;
	private String[] products = {"Aditya","Gunj", "Manish"};
	public ArrayList<ID> IDList;
	public static final String TAG = "TAG" ;
	public CustomListAdapter cust_adap;
	View view;
	HashMap<String, String> hash;
	SessionManager session;
	String received_contract_address, date;
	int year,day,month;
	ProgressDialog pDialog;
	String final_date;

	//SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

	String not_id, str1, str2,str_name;
	public boolean isConnected(){
		ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		view =  inflater.inflate(R.layout.fragment_main, container, false);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
		StrictMode.setThreadPolicy(policy);
		//timeStamp = (TextView)view.findViewById(R.id.textView2);
		eventNameInput = (EditText)view.findViewById(R.id.editText1);
		amountSplit = (EditText)view.findViewById(R.id.editText2);
		splitbill = (TextView)view.findViewById(R.id.textView11);
		context = view.getContext();
		split = (Button)view.findViewById(R.id.button2);
		createContract = (Button)view.findViewById(R.id.createContract);
		adapter = new ArrayAdapter<String>(context, R.layout.list_item,R.id.product_name, products);
		testButton = (Button)view.findViewById(R.id.testbutton);
		user = (TextView)view.findViewById(R.id.textView9);
		user2 = (TextView)view.findViewById(R.id.textView11);
		user3 = (TextView)view.findViewById(R.id.textView);
		session = new SessionManager(getContext());
		hash = session.getUserDetails();
		IDList = new ArrayList<ID>();
		final int[] ID_item_selected_index = {0};

		if(isConnected()) {// Reachability
		}
		else
			Toast.makeText( context, "Please Check if you have an active stable internet connection", Toast.LENGTH_SHORT).show();

		testButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				final Dialog book = new Dialog(getActivity());
				book.setContentView(R.layout.dialog_list_view);
				pDialog = new ProgressDialog(getContext());
				// Showing progsress dialog before making http request
				pDialog.setMessage("Loading...");
				pDialog.show();
				CallThread();
				cust_adap = new CustomListAdapter(getActivity(),  IDList);
				ListView listView = (ListView)book.findViewById(R.id.listView);
				listView.setAdapter(cust_adap);
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
		createContract.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				final Dialog book = new Dialog(getActivity());
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
				Button finalize_contract = (Button)book.findViewById(R.id.finalize);
				finalize_contract.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View view) {
						CreateContractMethod();
						book.dismiss();
					}
				});
				book.show();
			}

		});

		split.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if((!amountSplit.getText().toString().equals(""))) {
					int var = (Integer.parseInt(amountSplit.getText().toString().trim()) / 2);
					splitbill.setText("$ "+ Integer.toString(var));
				}
				else
					Toast.makeText(getContext(), "Please enter the amount to be split", Toast.LENGTH_LONG);
			}

		});

		return view;
	}
	private void CreateContractMethod() {
		Log.d(TAG, "inside method");
		String amount = this.splitbill.getText().toString().trim();
		if(amount!= null)
		Log.d("Amount" , amount + amount.toString().substring(1, amount.toString().length()).trim() );
		else
		Toast.makeText(getContext(), "Enter the amount to be spilt", Toast.LENGTH_SHORT).show();

		String event_Name = this.eventNameInput.getText().toString().trim();
		String receiver_name = str_name;//this.user.getText().toString().trim();
		String receiver_username = str1; //this.user2.getText().toString().trim();
		String receiver_address = str2; //this.user3.getText().toString().trim();
		String receiver_notification_id = this.not_id;
		String ev_name = eventNameInput.getText().toString().trim();
		pDialog = new ProgressDialog(getContext());
		// Showing progsress dialog before making http request
		pDialog.setMessage("Loading...");
		pDialog.show();
		RequestQueue queue2 = Volley.newRequestQueue(getContext());

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
					Toast.makeText(getContext(), "Contract Created, your friend has been notified", Toast.LENGTH_SHORT).show();
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
		RequestQueue queue = Volley.newRequestQueue(getContext());
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
