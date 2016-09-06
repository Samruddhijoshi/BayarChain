package sprint3ad;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.bayarchain.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import SessionManagement.SessionManager;

public class Xfers extends AppCompatActivity {

    EditText fullname, contact, email, apikey;
    ProgressDialog pDialog;
    public static final String TAG = "XFERS_PAGE";
    SessionManager session;
    HashMap<String, String> map;
    Button Xfer_copy, Save, Cancel;
    ImageButton info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xfers);
        session = new SessionManager(this);
        map = new HashMap<String, String>();
        map = session.getUserDetails();

        fullname = (EditText)findViewById(R.id.xfer_full_name);
        contact  = (EditText)findViewById(R.id.editText7);
        email = (EditText)findViewById(R.id.email_xfer);
        apikey = (EditText)findViewById(R.id.xfer_api);
        Xfer_copy = (Button)findViewById(R.id.xfer_copy_btn);
        Save = (Button)findViewById(R.id.xfers_save_btn);
        Cancel = (Button)findViewById(R.id.xfers_cancel_btn);
        info = (ImageButton)findViewById(R.id.xfers_api_key_infobtn);


        fullname.setClickable(false);
        contact.setClickable(false);
        email.setClickable(false);

        Xfer_copy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sandbox.xfers.io/api_tokens"));
                startActivity(browserIntent);
            }
        });
        Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //store on server and in session manager
                session.storeXfersApiKey(apikey.getText().toString().trim());

            }
        });
        Cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(Xfers.this, "Tap on the Copy Button to Copy your Xfers wallet key from the website to Transfer money to friends!", Toast.LENGTH_LONG).show();
            }
        });

        CallThread();

    }
    public void CallThread(){ //getting user details everytime

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Loading your Xfers Account Details");
        pDialog.setCancelable(false);
        pDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel Loading", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        pDialog.show();
        RequestQueue queue = Volley.newRequestQueue(this);

        String url = "http://23.97.60.51/bayar_mysql/get_email_phone.php?username=%27"
                + map.get(SessionManager.KEY_NAME)
                +"%27";

        Log.d("getting details for xfers", url);
        JsonArrayRequest movieReq = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString().trim());
                hidePDialog();
                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);
                        fullname.setText(obj.getString("fullname"));
                        contact.setText(obj.getString("contact"));
                        email.setText(obj.getString("email"));

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d(TAG, "Error: " + error.getMessage());
                hidePDialog();
            }
        });
        queue.add(movieReq);

    }
    private void hidePDialog() {
        if (pDialog != null) {
            pDialog.dismiss();
            pDialog = null;
        }
    }

}
