package brianhoffman.com.devpolman;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterPasscodeFragment extends Fragment {

    private EditText mEnterPasscodeET;
    private Button mPasscodeBTN;

    //use SharePreferences to store hashed password


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    protected void onCreateView(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_enter_passcode);

        mEnterPasscodeET = (EditText) view.findViewById(R.id.passcode_et);
        mPasscodeBTN = (Button) view.findViewById(R.id.passcode_btn);
        mPasscodeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mEnterPasscodeET.getText().toString().equals("123456")) {

                    Intent intent = new Intent(getActivity(), PhoneLockerActivity.class);
                    startActivity(intent);
                    getActivity().finish();

                } else {
                    mEnterPasscodeET.setText("");
                    Toast.makeText(getApplicationContext(),"Passcode incorrect", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}
