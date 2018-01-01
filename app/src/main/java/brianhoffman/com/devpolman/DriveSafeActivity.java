package brianhoffman.com.devpolman;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.Toast;

public class DriveSafeActivity extends AppCompatActivity {

    private static final String TAG = "DriveSafeActivity";
    private static final String ON_BOOT_SCREEN = "StartDriveSafeBootUpScreen";
    private static final String PREF_INTERRUPTED = "appInterrupted";

    private static final int RESULT_ENABLE = 1;
    public static DevicePolicyManager mDevicePolicyManager;
    public ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_safe);

        mDevicePolicyManager = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        mComponentName = new ComponentName(this, DevicePolicyWatcher.class);

        // see if phone has been restarted, start black screen
        boolean phoneRestarted = getIntent().getBooleanExtra(ON_BOOT_SCREEN, false);
        Log.i(TAG, String.valueOf(phoneRestarted));

        // see if passcode has been created yet, start enter passcode screen
        boolean mIsPasscodeSet = QueryPreferences.isPasscodeSet(this);
        Log.i(TAG, String.valueOf(mIsPasscodeSet));

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_enable_drive_safe_fragment_container);
        if (fragment == null) {
            if (phoneRestarted)
                fragment = BootUpFragment();
            else if (mIsPasscodeSet)
                fragment = EnterPasscodeFragment();
            else
                fragment = SetupFragment();
            fm.beginTransaction().add(R.id.activity_enable_drive_safe_fragment_container, fragment).commit();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(android.os.Build.VERSION.SDK_INT >= 21) {
                finishAndRemoveTask();
            } else {
                finish();
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.i(TAG,"onResume");


        if (QueryPreferences.getActivityInterrupted(this)) {
            Log.i(TAG,"interrupted: " + String.valueOf(QueryPreferences.getActivityInterrupted(this)) );

            QueryPreferences.setActivityInterrupted(this, false);

            FragmentManager fm = getSupportFragmentManager();
            Fragment fragment = fm.findFragmentByTag("ENTER_PASSCODE");
            Log.i(TAG,String.valueOf(fragment));

            if (fragment == null) {
                fragment = new EnterPasscodeFragment();
                fm.beginTransaction().add(R.id.activity_enable_drive_safe_fragment_container, fragment, "ENTER_PASSCODE").commit();

            }
            Log.i(TAG,"swapping in EnterPasscodeFragment");
            fm.beginTransaction().replace(R.id.activity_enable_drive_safe_fragment_container, fragment).commit();

        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case RESULT_ENABLE:
                if (resultCode == Activity.RESULT_OK) {

                    //onToggleDevicePolicyManager(true, mContext);
                } else {
                    Toast.makeText(this, "Locker not activated!", Toast.LENGTH_SHORT).show();
                }
                return;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected Fragment EnterPasscodeFragment() {

        return new EnterPasscodeFragment();
    }

    protected Fragment SetupFragment() {

        return new SetupFragment();
    }

    protected Fragment BootUpFragment() {

        return new BootUpFragment();
    }

    public static DevicePolicyManager getDevicePolicyManager() {
        return mDevicePolicyManager;
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        Log.i(TAG,"home button pressed");

        QueryPreferences.setActivityInterrupted(this,true);

    }

}
