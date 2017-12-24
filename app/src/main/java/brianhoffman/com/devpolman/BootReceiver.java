package brianhoffman.com.devpolman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "DriveSafeBootReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "...");

        if (QueryPreferences.isServiceRunning(context)) {
            ObdQueryService.startDriveSafeService(context);
            Log.i(TAG, "starting drive safe");
        }

    }
}
