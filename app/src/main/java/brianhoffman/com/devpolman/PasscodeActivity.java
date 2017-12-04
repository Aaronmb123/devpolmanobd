package brianhoffman.com.devpolman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class PasscodeActivity extends AppCompatActivity {

    private EditText mEnterPasscodeET;
    private Button mPasscodeBTN;

    //use SharePreferences to store hashed password

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_passcode);

        mEnterPasscodeET = (EditText) findViewById(R.id.passcode_et);
        mPasscodeBTN = (Button) findViewById(R.id.passcode_btn);
        mPasscodeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnterPasscodeET.getText().toString().equals("123456")) {

                    Intent intent = new Intent(getApplicationContext(), PhoneLockerActivity.class);
                    startActivity(intent);
                    finish();

                } else {
                    mEnterPasscodeET.setText("");
                    Toast.makeText(getApplicationContext(),"Passcode incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
