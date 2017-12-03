package brianhoffman.com.devpolman;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class PhoneLockerActivity extends AppCompatActivity {

    Button mButtonEnable;
    private Button mCloseBTN;
    private PendingIntent pendingIntent;
    private AlarmManager manager;

    static final int RESULT_ENABLE = 1;
    private static DevicePolicyManager mDevicePolicyManager;
    ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phone_locker);

        // Retrieve a PendingIntent that will perform a broadcast
        Intent alarmIntent = new Intent(this, AlarmReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, 0, alarmIntent, 0);

        mButtonEnable = (Button) findViewById(R.id.button_enable);
        mCloseBTN = (Button) findViewById(R.id.close_btn);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(PhoneLockerActivity.this, Controller.class);

        boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
        if (active) {
            mButtonEnable.setText("Disable");
        } else {
            mButtonEnable.setText("Enable");
        }

        mButtonEnable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
                if (active) {
                    mDevicePolicyManager.removeActiveAdmin(mComponentName);
                    mButtonEnable.setText("Enable");
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

    public static DevicePolicyManager getDevicePolicyManager() {
        return mDevicePolicyManager;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {
                    mButtonEnable.setText("Disable");
                } else {
                    Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public void startAlarm(View view) {
        manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        int interval = 1000;

        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), interval, pendingIntent);
        Toast.makeText(this, "Alarm Set", Toast.LENGTH_SHORT).show();
    }

    public void cancelAlarm(View view) {
        if (manager != null) {
            manager.cancel(pendingIntent);
            Toast.makeText(this, "Alarm Canceled", Toast.LENGTH_SHORT).show();
        }
    }

}
