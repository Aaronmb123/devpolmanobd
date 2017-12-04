package brianhoffman.com.devpolman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasscodeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = fm.findFragmentById(R.id.activity_passcode_fragment_container);
        if (fragment == null) {
            fragment = EnterPasscodeFragment();
            fm.beginTransaction().add(R.id.activity_passcode_fragment_container, fragment).commit();
        }
    }

    protected Fragment EnterPasscodeFragment() {

        return new EnterPasscodeFragment();
    }

}
