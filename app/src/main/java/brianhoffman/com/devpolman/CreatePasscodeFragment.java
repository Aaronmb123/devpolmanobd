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

public class CreatePasscodeFragment extends Fragment {

    private static final String HASHED_PASSCODE = "passcode";

    private EditText mEnterPasscodeET;
    private EditText mReEnterPasscodeET;
    private Button mDoneBTN;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_create_passcode, container, false);

        mEnterPasscodeET = (EditText) view.findViewById(R.id.enter_passcode_et);
        mReEnterPasscodeET = (EditText) view.findViewById(R.id.re_enter_passcode_et);
        mDoneBTN = (Button) view.findViewById(R.id.done_btn);

        mDoneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterPasscode = mEnterPasscodeET.getText().toString();
                String reEnterPassode = mReEnterPasscodeET.getText().toString();
                if (enterPasscode.equals(reEnterPassode)) {
                    // hash and save to shared preferences
                    QueryPreferences.setPasscode(getContext(), enterPasscode);
                    Intent intent = new Intent(getActivity(), PhoneLockerActivity.class);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    mEnterPasscodeET.setText("");
                    mReEnterPasscodeET.setText("");
                    Toast.makeText(getActivity(), "Passcodes don't match", Toast.LENGTH_SHORT).show();
                }
            }
        });


        return view;

    }

}
