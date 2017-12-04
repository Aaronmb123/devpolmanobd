package brianhoffman.com.devpolman;


import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.util.Log;
import android.widget.Toast;

public class ObdQueryService extends IntentService {

    private static final String TAG = "ObdQueryService";
    private static final long POLL_INTERVAL_MILLISECONDS = 1000;

    public static Intent newIntent(Context context) {
        return new Intent(context, ObdQueryService.class);
    }

    public static void setServiceAlarm(Context context, Boolean isOn) {

        Intent intent = ObdQueryService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);

        if (isOn) {
            alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    POLL_INTERVAL_MILLISECONDS, pendingIntent);
        } else {
            alarmManager.cancel(pendingIntent);
            pendingIntent.cancel();
        }

        QueryPreferences.setAlarmOn(context, isOn);

    }

    public static boolean isServiceAlarmOn(Context context) {
        Intent intent = ObdQueryService.newIntent(context);
        PendingIntent pendingIntent = PendingIntent.getService(context, 0, intent, PendingIntent.FLAG_NO_CREATE);
        return pendingIntent != null;
    }

    public ObdQueryService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.i(TAG, "Received an intent: " + intent);

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if (mBluetoothAdapter == null) {
            Toast.makeText(getApplicationContext(), "Device doesn't support Bluetooth", Toast.LENGTH_SHORT).show();
        } else {
            if (!mBluetoothAdapter.isEnabled()) {
                mBluetoothAdapter.enable();
                Log.i(TAG, "Bluetooth enabled");

            }
        }

        try {
            new ObdQueryTask(getApplicationContext()).execute();
        } catch (Exception e) {
            Log.i(TAG, "OBD Query Error");
            return;
        }

        Log.i(TAG, "Sent OBD query");

    }

}
