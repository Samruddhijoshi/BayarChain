package com.bayarchain;

import android.app.Activity;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import Push.GCMClientManager;

public class signUp extends Activity {

	EditText Firstname, lastName, password, confirmPassword, username, email, contact;
	Button loginBtn, signUpBtn;
	String uname, pass, reg_id, fullname;

	protected void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.signup);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();

		StrictMode.setThreadPolicy(policy);
		Firstname = (EditText) findViewById(R.id.firstName);
		email = (EditText) findViewById(R.id.emailaddress);
		password = (EditText) findViewById(R.id.password);
		confirmPassword = (EditText) findViewById(R.id.confirmPassword);
		username = (EditText) findViewById(R.id.username1);
		contact = (EditText)findViewById(R.id.contact);
		final GCMClientManager pushClientManager = new GCMClientManager(this, "721883998676");

		pushClientManager.registerIfNeeded(new GCMClientManager.RegistrationCompletedHandler() {
			@Override
			public void onSuccess(String registrationId, boolean isNewRegistration) {

				Log.d("Registration id", registrationId);
				reg_id = registrationId;
				//send this registrationId to your server
			}

			@Override
			public void onFailure(String ex) {
				super.onFailure(ex);
			}
		});
		signUpBtn = (Button) findViewById(R.id.signUpBtn);


		signUpBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				String password1 = password.getText().toString().trim();
				String confirmPassword1 = confirmPassword.getText().toString().trim();

				if (confirmPassword1.equals(password1)) {
					Log.d("Sign_Up", "Password Match");
					CallThread(Firstname.getText().toString(), email.getText().toString(), contact.getText().toString());
					finish();
				} else {
					Log.d("Sign_Up", "Password do not Match");
				}

			}
		});
	}
	protected void CallThread(String a,String s, String t) {
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				HttpClient httpClient =
						new DefaultHttpClient();
				HttpPost httpPost = new HttpPost("http://23.97.60.51/bayar_mysql/enter_user_details.php");
				//Post Data
				Log.d("DATA INSIDE THREAD", reg_id + Firstname.getText().toString() + email.getText().toString());

				List<NameValuePair> nameValuePair = new ArrayList<NameValuePair>(6);

				nameValuePair.add(new BasicNameValuePair("name", Firstname.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("username",username.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("password", password.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("noti_id", reg_id.toString()));
				nameValuePair.add(new BasicNameValuePair("email",email.getText().toString()));
				nameValuePair.add(new BasicNameValuePair("phone", contact.getText().toString()));
				try {
					httpPost.setEntity(new UrlEncodedFormEntity(nameValuePair));
					try {
						HttpResponse response = httpClient.execute(httpPost);
						Log.d("Response", response.getEntity().getContent().toString());
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
			} });

		thread.start();
	}
}


