package brianhoffman.com.devpolman;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class StackTraceActivity extends AppCompatActivity {

    private TextView mStackTraceTV;
    private String mStacktrace;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mStackTraceTV = (TextView) findViewById(R.id.stacktrace_text_view);

        //mStacktrace = getIntent().getStringExtra();
        //mStackTraceTV.setText(mStacktrace);

        mStackTraceTV.setText("stacktrace blah blah blah");

    }
}
