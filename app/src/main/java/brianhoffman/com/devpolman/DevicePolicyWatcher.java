package brianhoffman.com.devpolman;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class DevicePolicyWatcher extends DeviceAdminReceiver {

    private static final String TAG = "DeviceAdminReceiver";

    // add logic to send texts only if disabled outside of app

    @Override
    public void onEnabled(Context context, Intent intent) {
        QueryPreferences.setDevicePolicyManagerOn(context, true);

        // send message
        Log.i(TAG, "DevPolMan On. Sending text...");
        Log.i(TAG, "DevPolMan Enabled");

    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        QueryPreferences.setDevicePolicyManagerOn(context, false);

        // send message
        Log.i(TAG, "DevPolMan Off. Sending text...");
        Log.i(TAG, "DevPolMan Disabled");
    }
}
