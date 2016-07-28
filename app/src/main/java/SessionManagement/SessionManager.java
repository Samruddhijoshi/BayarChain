package SessionManagement;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.bayarchain.TabActivity;
import com.bayarchain.loginPage;

@SuppressLint("CommitPrefEdits")
public class SessionManager {
	// Shared Preferences
	SharedPreferences pref;
	// Editor for Shared preferences
	Editor editor;
	// Context
	Context _context;
	// Shared pref mode

	
	int PRIVATE_MODE = 0;
	// Sharedpref file name
	private static final String PREF_NAME = "Userfile";
	// All Shared Preferences Keys
	private static final String IS_LOGIN = "IsLoggedIn";
	// User name (make variable public to access from outside)
	public static final String KEY_NAME = "name";
	// password (make variable public to access from outside)
	public static final String KEY_PASS = "pass";
	// storing private key address in address
	public static final String KEY_ADDRESS = "address";
	// in future store phone number of the user
	public static final String KEY_NOTIFICATION_ID = "noti_id";

	// Constructor
	public SessionManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}
	/**
	 * Create login session
	 * */
	public void createLoginSession(String name, String pass){ //add address and noti id
		// Storing login value as TRUE
		editor.putBoolean(IS_LOGIN, true);
		// Storing name
		editor.putString(KEY_NAME, name);
		// Storing password
		editor.putString(KEY_PASS, pass);
		// Storing password
		//editor.putString(KEY_ADDRESS, address);
		//storing notification id
		//editor.putString(KEY_NOTIFICATION_ID,noti_id);
		//storing phone
		//editor.putString(KEY_PHONE, phone);
		// commit changes
		editor.commit();
	}	
	
	/**
	 * Check login method wil check user login status
	 * If false it will redirect user to login page
	 * Else won't do anything
	 * */
	public void checkLogin(){
		// Check login status
		if(!this.isLoggedIn()){
			// user is not logged in redirect him to Login Activity
			Intent i = new Intent(_context, loginPage.class);
			// Closing all the Activities
			i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			// Add new Flag to start new Activity
			i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			// Staring Login Activity
			_context.startActivity(i);
		}
		
	}
	/**
	 * Get stored session data
	 * */
	public HashMap<String, String> getUserDetails(){
		HashMap<String, String> user = new HashMap<String, String>();
		// user name
		user.put(KEY_NAME, pref.getString(KEY_NAME, null));
		// user password
		user.put(KEY_PASS, pref.getString(KEY_PASS, null));
		//return address
		user.put(KEY_ADDRESS, pref.getString(KEY_ADDRESS, null));
		//return notification id
		user.put(KEY_ADDRESS, pref.getString(KEY_NOTIFICATION_ID, null));
		//return phone
		//user.put(KEY_PHONE, pref.getString(KEY_PHONE, null));

		return user;
	}


	public void logoutUser(){
		// Clearing all data from Shared Preferences
		editor.clear();
		editor.commit();
		
		// After logout redirect user to Login Activity
		Intent i = new Intent(_context, loginPage.class);
		// Closing all the Activities
		i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		
		// Add new Flag to start new Activity
		i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		
		// Staring Login Activity
		_context.startActivity(i);
	}

	public boolean isLoggedIn(){
		return pref.getBoolean(IS_LOGIN, false);
	}
}
