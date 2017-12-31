package brianhoffman.com.devpolman;


import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class EnableDriveSafeFragment extends Fragment {


    private static final String TAG = "EnableDriveSafeFragment";

    private Button mDevicePolicyManagerBTN;
    private Button mDriveSafeBTN;
    private Button mCloseBTN;

    private Activity mActivity;

    private static DevicePolicyManager mDevicePolicyManager;
    private ComponentName mComponentName;

    private static final int RESULT_ENABLE = 1;


    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        View view = inflater.inflate(R.layout.fragment_enable_drive_safe, container, false);

        mActivity = getActivity();

        mDevicePolicyManager = ((DriveSafeActivity) getActivity()).mDevicePolicyManager;
        mComponentName = ((DriveSafeActivity) getActivity()).mComponentName;

        mDriveSafeBTN = (Button) view.findViewById(R.id.start_obd_service_btn);
        if (QueryPreferences.isServiceRunning(mActivity)) {
            mDriveSafeBTN.setText("Stop DriveSafe Service");
        } else {
            mDriveSafeBTN.setText("Start DriveSafe Service");
        }
        mDriveSafeBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onToggleDriveSafeService();
            }
        });

        mDevicePolicyManagerBTN = (Button) view.findViewById(R.id.device_policy_manager_btn);
        mCloseBTN = (Button) view.findViewById(R.id.close_btn);






        mDevicePolicyManagerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean active = mDevicePolicyManager.isAdminActive(mComponentName);
                if (active) {
                    mDevicePolicyManager.removeActiveAdmin(mComponentName);
                    onToggleDevicePolicyManager(!active, mActivity);
                } else {
                    Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
                    intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, mComponentName);
                    intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION, "Must enable for DriveSafe to work properly");
                    mActivity.startActivityForResult(intent, RESULT_ENABLE);
                }
            }
        });

        mCloseBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(android.os.Build.VERSION.SDK_INT >= 21) {
                    mActivity.finishAndRemoveTask();
                } else {
                    mActivity.finish();
                }

                return;
            }
        });

        return view;

    }

    private void onToggleDevicePolicyManager(boolean isActive, Context context) {
        if (isActive) {
            mDevicePolicyManagerBTN.setText("Disable Locking Capability");
            mDriveSafeBTN.setClickable(true);
            mDriveSafeBTN.setTextColor(Color.parseColor("#000000"));
            QueryPreferences.setDevicePolicyManagerOn(context, true);
        } else {
            mDevicePolicyManagerBTN.setText("Enable Locking Capability");
            mDriveSafeBTN.setClickable(false);
            mDriveSafeBTN.setTextColor(Color.parseColor("#708090"));
            QueryPreferences.setDevicePolicyManagerOn(context, false);
        }
    }

    private void onToggleDriveSafeService() {

        boolean isRunning = QueryPreferences.isServiceRunning(mActivity);

        if (isRunning) {
            ObdQueryService.stopDriveSafeService(mActivity);
            mDriveSafeBTN.setText("Start DriveSafe Service");
            mDevicePolicyManagerBTN.setClickable(true);
            mDevicePolicyManagerBTN.setTextColor(Color.parseColor("#000000"));
            Log.i(TAG, "ObdQueryService Stopped");
        } else {
            ObdQueryService.startDriveSafeService(mActivity);
            mDriveSafeBTN.setText("Stop DriveSafe Service");
            mDevicePolicyManagerBTN.setClickable(false);
            mDevicePolicyManagerBTN.setTextColor(Color.parseColor("#708090"));
            Log.i(TAG, "ObdQueryService Started");
        }
    }

    private void setButtons() {

        if (QueryPreferences.isDevicePolicyManagerOn(mActivity)) {
            mDevicePolicyManagerBTN.setText("Disable Locking Capability");
            mDriveSafeBTN.setClickable(true);
            mDriveSafeBTN.setTextColor(Color.parseColor("#000000"));
        } else {
            mDevicePolicyManagerBTN.setText("Enable Locking Capability");
            mDriveSafeBTN.setClickable(false);
            mDriveSafeBTN.setTextColor(Color.parseColor("#708090"));
        }

        if (QueryPreferences.isServiceRunning(mActivity)) {
            mDevicePolicyManagerBTN.setClickable(false);
            mDevicePolicyManagerBTN.setTextColor(Color.parseColor("#708090"));
        } else {
            mDevicePolicyManagerBTN.setClickable(true);
            mDevicePolicyManagerBTN.setTextColor(Color.parseColor("#000000"));
        }

    }

    @Override
    public void onResume() {
        super.onResume();

        setButtons();

    }

}
