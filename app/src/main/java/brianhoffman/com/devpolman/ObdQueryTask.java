package brianhoffman.com.devpolman;

import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class ObdQueryTask extends AsyncTask {

    private final static String OBD_NAME = "OBDII";
    private final static String TAG = "ObdQueryTask";
    private static final int LONG_QUERY_INTERVAL = 2000; // milliseconds
    private static final int SHORT_QUERY_INTERVAL = 500;

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private StringBuilder mStrBuffer = new StringBuilder();
    private StringBuilder mSetProcBuffer = new StringBuilder();
    private StringBuilder mRpmCmdBuffer = new StringBuilder();

    private Context mContext = null;
    private boolean mRpmsOverZero;
    private boolean mConnected;
    public static ObdQueryTask sObdQueryTask = null;


    private ObdQueryTask(Context context) {
        mContext = context;
    }

    public static ObdQueryTask getInstance(Context context) {

        if (sObdQueryTask != null) {
            switch (sObdQueryTask.getStatus()) {

                case FINISHED:
                    sObdQueryTask = new ObdQueryTask(context);
                    break;

                default:
                    return null;
            }
        } else {
            sObdQueryTask = new ObdQueryTask(context);
        }
        return sObdQueryTask;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Log.i(TAG, "do in background");

        mRpmsOverZero = false;

        // get obd
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e) {
            return null;
        }

        if (!mBluetoothAdapter.isEnabled())
            mBluetoothAdapter.enable();

        mPairedDevices = mBluetoothAdapter.getBondedDevices();

        for (BluetoothDevice device : mPairedDevices) {
            if (device.getName().equals(OBD_NAME)) {
                mDevice = device;
                break;
            }
        }

        // connect to bluetooth
        try {
            mSocket = mDevice.createRfcommSocketToServiceRecord(mDevice.getUuids()[0].getUuid());
        } catch (IOException e) {
            Log.i(TAG, "Could not create RF Comm Socket.");
            return null;
        }

        try {
            mSocket.connect();
            //mConnected = true;
            QueryPreferences.setQueryInterval(mContext, SHORT_QUERY_INTERVAL);
            Log.i(TAG, "short interval");
        } catch (IOException e) {
            Log.i(TAG, "Could not connect to OBD bluetooth device.");
            Log.i(TAG, e.toString());
            QueryPreferences.setQueryInterval(mContext, LONG_QUERY_INTERVAL);
            Log.i(TAG, "long interval");
            return null;
        }

        try {
            mInputStream = mSocket.getInputStream();
        } catch (IOException e) {
            Log.i(TAG, "Could not create input stream.");
            return null;
        }

        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.i(TAG, "Could not create output stream.");
            return null;
        }

//        // reset obd
//        try {
//            mOutputStream.write(("AT Z\r").getBytes());
//        } catch (IOException e) {
//            Log.i(TAG, "no write reset to output");
//            return null;
//        }
//
//        try {
//            mOutputStream.flush();
//        } catch (IOException e) {
//            Log.i(TAG, "No flush");
//        }
//
//        try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); }
//
        byte b;
        char c;
//
//        while (true) {
//            try {
//                b = (byte) mInputStream.read();
//            } catch (IOException e) {
//                Log.i(TAG, "input read error");
//                return null;
//            }
//
//            c = (char) b;
//
//            if (c == '>') {
//                break;
//            }
//            mStrBuffer.append(c);
//
//        }
//
//        // set protocol
//        try {
//            mOutputStream.write(("AT SP 0\r").getBytes());
//        } catch (IOException ieo) {
//            Log.i(TAG, "no write set to output");
//            return null;
//        }
//
//
//        try {
//            mOutputStream.flush();
//        } catch (IOException ieo) {
//            Log.i(TAG, "No flush");
//        }
//
//
//        try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); }
//
//
//        while (true) {
//            try {
//                b = (byte) mInputStream.read();
//            } catch (IOException e) {
//                Log.i(TAG, "input read error");
//                return null;
//            }
//
//            c = (char) b;
//            if (c == '>') {
//                break;
//            }
//            mSetProcBuffer.append(c);
//
//        }

        // send speed command
//        try {
//            mOutputStream.write(("01 0D\r").getBytes());
//        } catch (IOException e) {
//            Log.i(TAG, "no write speed cmd to output");
//            return null;
//        }

        // send rpm command
        try {
            mOutputStream.write(("01 0C\r").getBytes());
        } catch (IOException e) {
            Log.i(TAG, "Could not write RPM command to output stream.");
            return null;
        }

        try {
            mOutputStream.flush();
        } catch (IOException ieo) {
            Log.i(TAG, "No flush");
        }

        // read buffer
        try {
            Thread.sleep(200);
        } catch (Exception e) {
            Log.i(TAG, "thread sleep error");
            return null;
        }

        while (true) {
            try {
                b = (byte) mInputStream.read();
            } catch (IOException e) {
                Log.i(TAG, "input read error");
                return null;
            }

            c = (char) b;
            if (c == '>') {
                break;
            }
            mRpmCmdBuffer.append(c);

        }

        try {
            mSocket.close();
        } catch (IOException ioe) {
            Log.i(TAG, "no close");
        }

        String rpmOutput = mRpmCmdBuffer.toString();
        rpmOutput = rpmOutput.trim();

        // decode
        String rpmStr = rpmOutput.substring(rpmOutput.length() - 2, rpmOutput.length());
        rpmStr = "0x" + rpmStr;
        int speedKph = Integer.decode(rpmStr);

        if (speedKph > 0)
            mRpmsOverZero = true;

        //
        if (mRpmsOverZero && QueryPreferences.isServiceRunning(mContext)) {
            DevicePolicyManager devman = PhoneLockerActivity.getDevicePolicyManager();
            Log.i(TAG, "Locking");
            devman.lockNow();
        }
        Log.i(TAG, "do in background ending");
        return null;
    }

}


