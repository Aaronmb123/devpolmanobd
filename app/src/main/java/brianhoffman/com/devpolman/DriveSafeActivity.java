package brianhoffman.com.devpolman;

import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class DriveSafeActivity extends AppCompatActivity {

    private static final String TAG = "DriveSafeActivity";
    private static final String bootUpArg = "QuitDriveSafeActivity";

    private boolean mIsPasscodeSet;

    private static final int RESULT_ENABLE = 1;
    //private static DevicePolicyManager mDevicePolicyManager;
    //private ComponentName mComponentName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);


        // see if passcode has been created yet
        mIsPasscodeSet = QueryPreferences.isPasscodeSet(this);
        Log.i(TAG, String.valueOf(mIsPasscodeSet));

        // see if phone has been restarted
        boolean phoneRestarted = getIntent().getBooleanExtra(bootUpArg, false);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_passcode_fragment_container);
        if (fragment == null) {
            if (mIsPasscodeSet)
                fragment = EnterPasscodeFragment();
            else if (phoneRestarted)
                fragment = BootUpFragment();
            else
                fragment = SetupFragment();
            fm.beginTransaction().add(R.id.activity_passcode_fragment_container, fragment).commit();
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

    protected Fragment EnterPasscodeFragment() {

        return new EnterPasscodeFragment();
    }

    protected Fragment SetupFragment() {

        return new SetupFragment();
    }

    protected Fragment BootUpFragment() {

        return new BootUpFragment();
    }


}
