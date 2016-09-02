package Push;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

import com.bayarchain.PushNotificationService;

/**
 * Created by Adi_711 on 31-05-2016.
 */
public class GcmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Explicitly specify that GcmIntentService will handle the intent.

        ComponentName comp = new ComponentName(context.getPackageName(),
                PushNotificationService.class.getName());
        // Start the service, keeping the device awake while it is launching.
        startWakefulService(context, (intent.setComponent(comp)));
        setResultCode(Activity.RESULT_OK);
        String action = intent.getAction();
        if("ACTION_1".equals(action)) {
            Log.d("shuffTest", "Pressed YES");
        }

    }
}