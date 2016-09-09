package SessionManagement;

import java.util.HashMap;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

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
	// in future store phone number of the user
	public static final String KEY_CONTACT = "address";
	//store the email address of the user
	public static final String KEY_EMAIL = "email";
	//store the XFERS API KEY OF THE USER
	public static final String KEY_API_XFER = "xferkey";
	//store the notification id of user's android device
	public static final String KEY_NOTIFICATION_ID = "noti_id";
	// on recieve intent codes for notifications of payment confirmation
	public static final String KEY_PAYMENT_RECIEIVED = "payment_rec";
	//help screen count
	public static final String KEY_HELP_SCREEN = "help_screen";
	//return from create contract screen key
	public static final String KEY_RETURN_FROM_SCREEN_CREATE_CONTRACT = "return_key";
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
		// commit changes
		editor.commit();
	}

	public void storehelpScreen(int i){
		editor.putInt(KEY_HELP_SCREEN, i);
		Log.d("HELPSCREEN_SHARED_PREF", String.valueOf(i));

		editor.commit();
	}

	public String getRetrunKey(){return pref.getString(KEY_RETURN_FROM_SCREEN_CREATE_CONTRACT, null); }
	public void storeReturnKey(String email){
		editor.putString(KEY_RETURN_FROM_SCREEN_CREATE_CONTRACT, email);
		editor.commit();
	}
	public int getHelpScreen(){
		return pref.getInt(KEY_HELP_SCREEN, 0);
	}
	public String getEmail(){
		return pref.getString(KEY_EMAIL, null);
	}
	public void storeEmail(String email){
		editor.putString(KEY_EMAIL, email);
		editor.commit();
	}
	public void storeMessage(String message){
		editor.putString(KEY_PAYMENT_RECIEIVED, message);
		Log.d("PAYMENT_NOTI_SHARED_PREF", message);
		editor.commit();
	}
	public void storeXfersApiKey(String message){
		editor.putString(KEY_API_XFER, message);
		Log.d("KEY_API_XFER", message);
		editor.commit();
	}
	public String returnXferApiKey(){
		return pref.getString(KEY_API_XFER, null);
	}
	public String returnStoredMessage(){
		return pref.getString(KEY_PAYMENT_RECIEIVED, null);
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
		//return notification id
		user.put(KEY_NOTIFICATION_ID, pref.getString(KEY_NOTIFICATION_ID, null));
		//return email address
		user.put(KEY_EMAIL, pref.getString(KEY_EMAIL, null));
		//return phone
		user.put(KEY_CONTACT, pref.getString(KEY_CONTACT, null));
		//return xfer api key of the user
		user.put(KEY_API_XFER, pref.getString(KEY_API_XFER, null));

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
