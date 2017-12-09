package brianhoffman.com.devpolman;

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

    private Context mContext;
    private final static String OBD_NAME = "OBDII";

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> mPairedDevices;

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private StringBuilder mStrBuffer = new StringBuilder();
    private StringBuilder mResetCmdBuffer = new StringBuilder();
    private StringBuilder mSetProcBuffer = new StringBuilder();
    private StringBuilder mRpmCmdBuffer = new StringBuilder();

    private boolean mSpeedOverZero;

    public ObdQueryTask(Context context) {
        mContext = context;
    }

    @Override
    protected Object doInBackground(Object[] objects) {

        Log.i("+++++++++++++++++++", "do in background");

        mSpeedOverZero = false;

        // get obd
        try {
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        } catch (Exception e) {
            return null;
        }

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
            Log.i("+++++++++++++++++++", "no comm");
            return null;
        }

        try {
            mSocket.connect();
        } catch (IOException e) {
            Log.i("+++++++++++++++++++", "no connect");
            Log.i("+++++++++++++++++++", e.toString());
            return null;
        }

        try {
            mInputStream = mSocket.getInputStream();
        } catch (IOException e) {
            Log.i("+++++++++++++++++++", "no input stream");
            return null;
        }

        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Log.i("+++++++++++++++++++", "no output stream");
            return null;
        }

        // reset obd
        try {
            mOutputStream.write(("AT Z\r").getBytes());
        } catch (IOException e) {
            Log.i("+++++++++++++++++++", "no write reset to output");
            return null;
        }

        try {
            mOutputStream.flush();
        } catch (IOException e) {
            Log.i("+++++++++++++++++++", "No flush");
        }

        try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); }

        byte b;
        char c;

        while (true) {
            try {
                b = (byte) mInputStream.read();
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "input read error");
                return null;
            }

            c = (char) b;

            if (c == '>') {
                break;
            }
            mStrBuffer.append(c);

        }

        // set protocol
        try {
            mOutputStream.write(("AT SP 0\r").getBytes());
        } catch (IOException ieo) {
            Log.i("+++++++++++++++++++", "no write set to output");
            return null;
        }


        try {
            mOutputStream.flush();
        } catch (IOException ieo) {
            Log.i("+++++++++++++++++++", "No flush");
        }


        try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); }


        while (true) {
            try {
                b = (byte) mInputStream.read();
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "input read error");
                return null;
            }

            c = (char) b;
            if (c == '>') {
                break;
            }
            mSetProcBuffer.append(c);

        }

        // send speed command
        try {
            mOutputStream.write(("01 0D\r").getBytes());
        } catch (IOException e) {
            Log.i("+++++++++++++++++++", "no write speed cmd to output");
            return null;
        }

        try {
            mOutputStream.flush();
        } catch (IOException ieo) {
            Log.i("+++++++++++++++++++", "No flush");
        }


        // read buffer
        try {
            Thread.sleep(600);
        } catch (Exception e) {
            Log.i("+++++++++++++++++++", "thread sleep error");
            return null;
        }


        while (true) {
            try {
                b = (byte) mInputStream.read();
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "input read error");
                return null;
            }

            c = (char) b;
            if (c == '>') {
                break;
            }
            mRpmCmdBuffer.append(c);

        }

        // closing socket before locking allows program to reconnect after unlock
        // TODO save connection state somehow
        // SharedPreferences
        try {
            mSocket.close();
        } catch (IOException ioe) {
            Log.i("+++++++++++++++++++", "no close");
        }

        String speedOutput = mRpmCmdBuffer.toString();
        speedOutput = speedOutput.trim();

        // decode
        String speedKphStr = speedOutput.substring(speedOutput.length() - 2, speedOutput.length());
        speedKphStr = "0x" + speedKphStr;
        int speedKph = Integer.decode(speedKphStr);

        if (speedKph > 0)
            mSpeedOverZero = true;


        if (mSpeedOverZero) {

            DevicePolicyManager devman = PhoneLockerActivity.getDevicePolicyManager();
            devman.lockNow();
        }

        return null;
    }

}

