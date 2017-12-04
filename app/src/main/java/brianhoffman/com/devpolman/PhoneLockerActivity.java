package brianhoffman.com.devpolman;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PhoneLockerActivity extends AppCompatActivity {

    private static final String TAG = "PhoneLockerActivity";

    //TODO rename mbuttonenable;
    private Button mDevicePolicyManagerBTN;
    private Button mDriveSafeBTN;
    private Button mCloseBTN;

    private static final int RESULT_ENABLE = 1;
    private static DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_locker);

        mDriveSafeBTN = (Button) findViewById(R.id.start_obd_service_btn);
        if (ObdQueryService.isServiceAlarmOn(getApplicationContext())) {
            mDriveSafeBTN.setText("Stop DriveSafe Service");
        } else {
            mDriveSafeBTN.setText("Start DriveSafe Service");
        }
        mDriveSafeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!ObdQueryService.isServiceAlarmOn(getApplicationContext())) {
                    ObdQueryService.setServiceAlarm(getApplicationContext(), true);
                    mDriveSafeBTN.setText("Stop DriveSafe Service");
                    Log.i(TAG, "ObdQueryService Started");
                } else {
                    ObdQueryService.setServiceAlarm(getApplicationContext(), false);
                    mDriveSafeBTN.setText("Start DriveSafe Service");
                    Log.i(TAG, "ObdQueryService Stopped");
                }
            }
        });

        mDevicePolicyManagerBTN = (Button) findViewById(R.id.button_enable);
        mCloseBTN = (Button) findViewById(R.id.close_btn);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(PhoneLockerActivity.this, Controller.class);

        boolean active = !(QueryPreferences.isDevicePolicyManagerOn(getApplicationContext()));
        onToggleDevicePolicyManager(active, getApplicationContext());

        mDevicePolicyManagerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
                if (active) {
                    mDevicePolicyManager.removeActiveAdmin(mComponentName);
                    onToggleDevicePolicyManager(!active, getApplicationContext());
                } else {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Please enable app");
                    startActivityForResult(intent, RESULT_ENABLE);
                }
            }
        });

        mCloseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void onToggleDevicePolicyManager(boolean isActive, Context context) {
        if (isActive) {
            mDevicePolicyManagerBTN.setText("Disable Locking Capability");
            mDriveSafeBTN.setClickable(true);
            mDriveSafeBTN.setTextColor(Color.parseColor("#000000"));
            QueryPreferences.setDevicePolicyManagerOn(context, false);
        } else {
            mDevicePolicyManagerBTN.setText("Enable Locking Capability");
            mDriveSafeBTN.setClickable(false);
            mDriveSafeBTN.setTextColor(Color.parseColor("#708090"));
            QueryPreferences.setDevicePolicyManagerOn(context, true);
        }
    }

    public static DevicePolicyManager getDevicePolicyManager() {
        return mDevicePolicyManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    onToggleDevicePolicyManager(true, getApplicationContext());
                } else {
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }



}
