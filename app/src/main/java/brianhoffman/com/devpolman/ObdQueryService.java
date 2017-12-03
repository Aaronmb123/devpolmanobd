package brianhoffman.com.devpolman;


import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

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
    }

}
