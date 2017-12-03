package brianhoffman.com.devpolman;


import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class ObdQueryService extends IntentService {

    private static final String TAG = "ObdQueryService";

    public static Intent newIntent(Context context) {
        return new Intent(context, ObdQueryService.class);
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

    }

}
