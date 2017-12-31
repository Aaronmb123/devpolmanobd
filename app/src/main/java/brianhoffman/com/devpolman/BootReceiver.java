package brianhoffman.com.devpolman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


public class BootReceiver extends BroadcastReceiver {

    private static final String TAG = "DriveSafeBootReceiver";
    private static final String ON_BOOT_SCREEN = "StartDriveSafeBootUpScreen";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "onReceive...");

        Log.i(TAG, "service running: " + String.valueOf(QueryPreferences.isServiceRunning(context)));

        if (QueryPreferences.isServiceRunning(context)) {
            ObdQueryService.startDriveSafeService(context);
            Intent driveSafeIntent = new Intent(context, DriveSafeActivity.class);
            driveSafeIntent.putExtra(ON_BOOT_SCREEN, true);
            context.startActivity(driveSafeIntent);
            Log.i(TAG, "starting drive safe");
        }

    }
}
