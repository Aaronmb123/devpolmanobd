package brianhoffman.com.devpolman;


import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;

public class ObdQueryService extends IntentService {

    private static final String TAG = "ObdQueryService";

    private static ComponentName mComponentName;

    public static Intent newIntent(Context context) {
        mComponentName = new ComponentName(context, Controller.class);

        return new Intent(context, ObdQueryService.class);

    }

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        return START_STICKY;

    }

    public static void startDriveSafeService(Context context) {

        Intent intent = ObdQueryService.newIntent(context);
        context.startService(intent);

        QueryPreferences.setServiceRunningState(context, true);

        Log.i(TAG, "startService");

    }

    public static void stopDriveSafeService(Context context) {

        Intent intent = ObdQueryService.newIntent(context);
        context.stopService(intent);

        QueryPreferences.setServiceRunningState(context, false);

        Log.i(TAG, "stopService");

    }

    // work in progress (12-22-17)
    // fake a shorter timeout for socket.connect
    public static void runSocketTimeOut(final BluetoothSocket socket) {
        Log.i(TAG, "runSocketTimeOut");
        Runnable socketTimeout = new Runnable() {
            @Override
            public void run() {
                Log.i(TAG, "socketTimeOut");
                try {
                    Thread.sleep(5000);
                } catch (Exception e) {}
                if (!socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException ioe) {}
                    //QueryPreferences.setQueryInterval(mContext, LONG_QUERY_INTERVAL);
                    //Log.i(TAG, "Long query interval set.");
                }
            }
        };
        new Thread(socketTimeout).start();

    }

    public ObdQueryService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "OnHandleIntent");

        if (QueryPreferences.isDevicePolicyManagerOn(this)) {

            ObdQueryTask task = ObdQueryTask.getInstance(getApplicationContext());

            if (task != null) {
                try {
                    task.execute();
                    Log.i(TAG, "Sent OBD query");
                } catch (Exception e) {
                    Log.i(TAG, "OBD Query Error");
                    return;
                }
            } else {
                Log.i(TAG, "no OBD Query task created");
            }
        }

        try {
            int interval = QueryPreferences.getQueryInterval(this);
            Log.i(TAG, "Sleep interval: " + String.valueOf(interval));
            Thread.sleep(interval);
        } catch (Exception e) {
            Log.i(TAG, "Thread sleep error");
        }

        if (QueryPreferences.isServiceRunning(this)) {
            Log.i(TAG, "Sending new intent...");
            startService(intent);
        }
    }
}

