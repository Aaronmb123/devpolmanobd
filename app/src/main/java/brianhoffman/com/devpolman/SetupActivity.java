package brianhoffman.com.devpolman;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class SetupActivity extends AppCompatActivity {

    private static final String TAG = "SetupActivity";
    private boolean mIsPasscodeSet;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        // see if passcode has been created yet
        mIsPasscodeSet = QueryPreferences.isPasscodeSet(this);
        Log.i(TAG, String.valueOf(mIsPasscodeSet));

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_passcode_fragment_container);
        if (fragment == null) {
            if (mIsPasscodeSet)
                fragment = EnterPasscodeFragment();
            else
                fragment = SetupFragment();
            fm.beginTransaction().add(R.id.activity_passcode_fragment_container, fragment).commit();
        }
    }

    protected Fragment EnterPasscodeFragment() {

        return new EnterPasscodeFragment();
    }

    protected Fragment SetupFragment() {

        return new SetupFragment();
    }

}
