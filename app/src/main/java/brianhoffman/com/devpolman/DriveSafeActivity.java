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

        mIsPasscodeSet = true;

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

    // when the enable drive safe fragment is active and the user presses home
    // or the activity is interrupted, i.e., phone call, etc, the fragment can be
    // brought back without going through the enter passcode fragment, which
    // we don't want
//    @Override
//    protected void onPause()
//    {
//        Log.i(TAG,"Home button pressed");
//
//        // if fragment is enablefragment, swap to passcode fragment
//        FragmentManager fm = getSupportFragmentManager();
//        Fragment fragment = fm.findFragmentByTag(R.id.activity_enable_drive_safe_fragment_container);
//        if (fragment == null) {
//            fragment = EnterPasscodeFragment();
//
//            fm.beginTransaction().add(R.id.activity_enable_drive_safe_fragment_container, fragment).commit();
//        }
//
//        super.onPause();
//    }

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

}
