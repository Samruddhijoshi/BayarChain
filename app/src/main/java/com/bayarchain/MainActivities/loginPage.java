package com.bayarchain.MainActivities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bayarchain.R;
import com.google.android.gms.common.api.GoogleApiClient;

import com.bayarchain.Push.GCMClientManager;
import com.bayarchain.SessionManagement.SessionManager;

public class LoginPage extends AppCompatActivity {

	EditText username, password;
	Button loginBtn, signUpBtn;
	SessionManager session;
	String notification_registration_id;
	private GoogleApiClient client;

	public boolean isConnected(){
		ConnectivityManager connMgr = (ConnectivityManager) this.getSystemService(Activity.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
		if (networkInfo != null && networkInfo.isConnected())
			return true;
		else
			return false;
	}

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		username = (EditText) findViewById(R.id.username1);
		password = (EditText) findViewById(R.id.password);
		loginBtn = (Button) findViewById(R.id.login);
		signUpBtn = (Button) findViewById(R.id.signUp);
		session = new SessionManager(getBaseContext());

		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

		final GCMClientManager pushClientManager = new GCMClientManager(this, "721883998676");

		loginBtn.setOnClickListener(new OnClickListener(){
			public void onClick(View v) {
				String user = username.getText().toString().trim();
				String pass = password.getText().toString().trim();
				pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
					@Override
						public void onSuccess(String registrationId, boolean isNewRegistration) {

						Log.d("Registration id", registrationId);
						//send this registrationId to your server
						notification_registration_id = registrationId;
						finish();
					}
					@Override
					public void onFailure(String ex) {
						super.onFailure(ex);
					}
				});
				CallThread(user,pass);
			}
		});

		signUpBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent(LoginPage.this, SignUp.class);
				startActivity(intent);
			}
		});
	}

	protected void CallThread(final String username, final String password) {
		RequestQueue queue = Volley.newRequestQueue(this);
		String url ="http://bayarchain.southeastasia.cloudapp.azure.com/bayar_mysql/check_user_login.php?username=%27"+username+
																		"%27&password=%27" +password+"%27" +
																		 "&noti_id=%27"  +notification_registration_id +"%27" ;
		Log.d("Login page update notifiaction", url);
	// Request a string response from the provided URL.
		StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
				new Response.Listener<String>() {
					@Override
					public void onResponse(String response) {

						Log.d("Response", response.toString());
						if(response.toString().trim().equals("record found") && isConnected()){
							session.createLoginSession(username, password);
							Intent intent = new Intent(LoginPage.this, ScrollingActivity.class);
							startActivity(intent);
							LoginPage.this.finish();
						}
						else if(!isConnected()){
							Toast.makeText(getBaseContext(), "Please Check if you have an active stable internet connection", Toast.LENGTH_SHORT).show();
						}
						else {
							Toast.makeText(getBaseContext(), "Please sign up first. Your wallet is linked to your phone.", Toast.LENGTH_LONG).show();

						}
					}
				}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				//mTextView.setText("That didn't work!");
			}
		});
		// Add the request to the RequestQueue.
		queue.add(stringRequest);
	}
	@Override
	public void onBackPressed() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("Are you sure you want to exit?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						LoginPage.this.finish();
					}
				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
		AlertDialog alert = builder.create();
		alert.show();
	}
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		//finish();
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		//finish();
	}
}
