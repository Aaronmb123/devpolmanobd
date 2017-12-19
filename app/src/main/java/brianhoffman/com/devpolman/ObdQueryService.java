package brianhoffman.com.devpolman;


import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class ObdQueryService extends IntentService {

    private static final String TAG = "ObdQueryService";
    private static final long POLL_INTERVAL_MILLISECONDS = 100;

    private static int mNumberOfIntents;
    private Context mContext;

    public static Intent newIntent(Context context) {

        return new Intent(context, ObdQueryService.class);

    }

//    @Override
//    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
//        super.onStartCommand(intent, flags, startId);
//        return START_STICKY;
//
//    }

    public static void startService(Context context) {

        Intent intent = ObdQueryService.newIntent(context);
        context.startService(intent);

        QueryPreferences.setServiceRunningState(context, true);

        Log.i(TAG, "startService");

    }

    public static void stopService(Context context) {

        Intent intent = ObdQueryService.newIntent(context);
        context.stopService(intent);

        QueryPreferences.setServiceRunningState(context, false);

        Log.i(TAG, "stopService");

    }

    public ObdQueryService() {
        super(TAG);

    }

    @Override
    protected void onHandleIntent(Intent intent) {

        Log.i(TAG, "OnHandleIntent");
        Log.i(TAG, "sending Query...");
//        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
//
//        if (mBluetoothAdapter == null) {
//            Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
//        } else {
//            if (!mBluetoothAdapter.isEnabled()) {
//                mBluetoothAdapter.enable();
//                Log.i(TAG, "Bluetooth enabled");
//
//            }
//        }
//
//        ObdQueryTask task = ObdQueryTask.getInstance(getApplicationContext());
//
//        if (task != null) {
//            try {
//                task.execute();
//                Log.i(TAG, "Sent OBD query");
//            } catch (Exception e) {
//                Log.i(TAG, "OBD Query Error");
//                return;
//            }
//        } else {
//            Log.i(TAG, "no OBD Query task created");
//        }

        Log.i(TAG, "Sleeping...");

        try {
            // query for sleep time based on interval set in QueryPreferences
            Thread.sleep(500);
        } catch (Exception e) {
            Log.i(TAG, "Thread sleep error");
        }

        if (QueryPreferences.isServiceRunning(getApplicationContext())) {
            Log.i(TAG, "Sending intent...");
            startService(intent);
        }
    }
}

