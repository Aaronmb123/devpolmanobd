package brianhoffman.com.devpolman;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AlarmReceiver extends BroadcastReceiver {

    private static final String TAG = "AlarmReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {

        // TODO if locked return
        // getStatus() to get the status of your AsyncTask.
        // If status is AsyncTask.Status.RUNNING then your task is running.

        try {
            new ObdQueryTask(context).execute();
        } catch (Exception e) {
            Log.i(TAG, "OBD Query Error");
            return;
        }

        Toast.makeText(context, "PhoneLocker running", Toast.LENGTH_SHORT).show();

    }
}