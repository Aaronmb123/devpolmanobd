package brianhoffman.com.devpolman;

import android.app.DownloadManager;
import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class ObdQueryTask extends AsyncTask {

    private final static String OBD_NAME = "OBDII";
    private final static String TAG = "ObdQueryTask";
    private static final int LONG_QUERY_INTERVAL = 5000; // milliseconds
    private static final int SHORT_QUERY_INTERVAL = 500;

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private StringBuilder mStrBuffer = new StringBuilder();
    //private StringBuilder mSetProcBuffer = new StringBuilder();
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

//        if (mBluetoothAdapter.isEnabled()) {
//            mBluetoothAdapter.disable();
//        }

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
            Log.e(TAG, "Could not create RF Comm Socket");
            return null;
        }

        try {
            mSocket.connect();
            //mConnected = true;
            QueryPreferences.setQueryInterval(mContext, SHORT_QUERY_INTERVAL);
            Log.i(TAG, "Short query interval set");
        } catch (IOException e) {
            QueryPreferences.setQueryInterval(mContext, LONG_QUERY_INTERVAL);
            Log.i(TAG, "Long query interval set");
            Log.i(TAG, "Could not connect to OBD Bluetooth device");
            Log.i(TAG, e.toString());
            try {
                mSocket.close();
            } catch (IOException ioe) {
                Log.e(TAG, "Could not close socket");
            }

            return null;
        }

        try {
            mInputStream = mSocket.getInputStream();
        } catch (IOException e) {
            Log.i(TAG, "Could not create input stream");
            return null;
        }

        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.i(TAG, "Could not create output stream");
            return null;
        }

        byte b;
        char c;

        // reset obd
        try {
            mOutputStream.write(("AT Z\r").getBytes());
        } catch (IOException e) {
            Log.i(TAG, "Could not reset OBD");
            return null;
        }

        try {
            mOutputStream.flush();
        } catch (IOException e) {
            Log.i(TAG, "Error flushing output buffer");
        }

        try { Thread.sleep(50); } catch (InterruptedException e) { e.printStackTrace(); }


        while (true) {
            try {
                b = (byte) mInputStream.read();
            } catch (IOException e) {
                Log.i(TAG, "Input Stream Read Error");
                return null;
            }

            c = (char) b;

            if (c == '>') {
                break;
            }
        }
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
        // rpm command returns two bytes in a five-char, string format, i.e., "A4 60"
        try {
            mOutputStream.write(("01 0C\r").getBytes());
        } catch (IOException e) {
            Log.i(TAG, "Could not write RPM command to output stream");
            return null;
        }

//        try {
//            mOutputStream.flush();
//        } catch (IOException ieo) {
//            Log.i(TAG, "Error flushing output stream.");
//        }

        // read buffer
        try {
            Thread.sleep(50);
        } catch (Exception e) {
            Log.i(TAG, "Thread sleep error");
            return null;
        }

        while (true) {
            try {
                b = (byte) mInputStream.read();
            } catch (IOException e) {
                Log.i(TAG, "Error reading input stream");
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
            Log.i(TAG, "Error closing Bluetooth socket");
        }

        String rpmOutput = mRpmCmdBuffer.toString();
        rpmOutput = rpmOutput.trim();

        // decode
        // OBD returns a string of text
        // we only want the last 5 chars
        String rpmStr = rpmOutput.substring(rpmOutput.length() - 5, rpmOutput.length());
        rpmStr = rpmStr.replace(" ", "");
        rpmStr = "0x" + rpmStr;
        Log.i(TAG, "Rpm: " + String.valueOf(rpmStr));
        int rpms = 0;

        try {
            rpms = Integer.decode(rpmStr);
        } catch (RuntimeException e) {
            Log.i(TAG, "Decode error");

            return null;
        }

        if (rpms > 0)
            mRpmsOverZero = true;

        if (mRpmsOverZero && QueryPreferences.isServiceRunning(mContext)
                && QueryPreferences.isDevicePolicyManagerOn(mContext)) {

            DevicePolicyManager devman = DriveSafeActivity.getDevicePolicyManager();

            try {
                devman.lockNow();
                Log.i(TAG, "Locking");

            } catch (Exception e) {
                Log.i(TAG, "lockNow() failed");
                Log.i(TAG, String.valueOf(e));
            }
        }

        Log.i(TAG, "do in background ending");

        return null;
    }

}


