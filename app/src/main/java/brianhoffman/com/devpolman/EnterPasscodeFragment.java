package brianhoffman.com.devpolman;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class EnterPasscodeFragment extends Fragment {

    private static final String TAG = "EnterPasscodeFragment";
    private static final String HASHED_PASSCODE = "hashedPasscode";

    private EditText mEnterPasscodeET;
    private Button mPasscodeBTN;
    private Activity mActivity;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_enter_passcode, container, false);

        mEnterPasscodeET = (EditText) view.findViewById(R.id.passcode_et);
        mPasscodeBTN = (Button) view.findViewById(R.id.passcode_btn);
        mPasscodeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String passcode = PreferenceManager.getDefaultSharedPreferences(getContext()).getString(HASHED_PASSCODE, null);

                //if (mEnterPasscodeET.getText().toString().equals(passcode)) {

                    // swap fragments to EnableDriveSafeFragment
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    Fragment fragment = new EnableDriveSafeFragment();
                    fm.beginTransaction().add(R.id.activity_enable_drive_safe_fragment_container, fragment, "ENABLE_DRIVE_SAFE").commit();
                    fm.beginTransaction().replace(R.id.activity_enable_drive_safe_fragment_container, fragment).commit();
                    Log.i(TAG, "Swapping in EnableDriveSafeFragment");

//                } else {
//                    mEnterPasscodeET.setText("");
//                    Toast.makeText(getActivity(),"Passcode Incorrect", Toast.LENGTH_SHORT).show();
//                }
            }
        });

        ((AppCompatActivity) getActivity()).getSupportActionBar().show();


        return view;
    }

}
