package sprint3_ad;

import android.app.PendingIntent;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.bayarchain.PushNotificationService;
import com.bayarchain.R;

import SessionManagement.SessionManager;

public class PaymentReceived extends AppCompatActivity {
    Intent intent;
    TextView payment_rec;
    Button acknowledge;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.payment_received);
        payment_rec = (TextView)findViewById(R.id.payment_rec_textview);
        acknowledge = (Button)findViewById(R.id.acknowldge);

        SessionManager session = new SessionManager(this);
        String key = session.returnStoredMessage();
        Log.d("HELLLLLLLLOOOOOO", key);
        payment_rec.setText(key);
        acknowledge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PaymentReceived.this.finish();
            }
        });
    }
    protected void onNewIntent(Intent intent) {
        String startPageNumber;

        if (intent != null) {
            startPageNumber = intent.getExtras().getString("PAYMENT");
        } else {
            startPageNumber = "0";
        }
    }
}
