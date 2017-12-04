package brianhoffman.com.devpolman;


import android.app.admin.DeviceAdminReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class Controller extends DeviceAdminReceiver {

    private static final String TAG = "DeviceAdminReceiver";

    @Override
    public void onEnabled(Context context, Intent intent) {
        Log.i(TAG, "Enabled");
    }

    @Override
    public void onDisabled(Context context, Intent intent) {
        Log.i(TAG, "Disabled");
    }
}
