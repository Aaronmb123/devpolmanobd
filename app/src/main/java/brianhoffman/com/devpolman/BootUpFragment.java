package brianhoffman.com.devpolman;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class BootUpFragment extends Fragment {

    private TextView mBootSuccessfullTV;
    private Button mOkBTN;
    private Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ((AppCompatActivity) getActivity()).getSupportActionBar().hide();

    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_boot_up, container, false);

        mBootSuccessfullTV = (TextView) view.findViewById(R.id.boot_up_tv);
        mOkBTN = (Button) view.findViewById(R.id.boot_up_ok_btn);

        mOkBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(android.os.Build.VERSION.SDK_INT >= 21) {
                    mActivity.finishAndRemoveTask();
                } else {
                    mActivity.finish();
                }
            }
        });

        return view;

    }
}
