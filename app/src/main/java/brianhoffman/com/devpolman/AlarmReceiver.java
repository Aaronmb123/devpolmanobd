package brianhoffman.com.devpolman;


import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {


        // TODO if locked return
        // getStatus() to get the status of your AsyncTask.
        // If status is AsyncTask.Status.RUNNING then your task is running.

        try {
            new obdQueryTask(arg0).execute();
        } catch (Exception e) {
            return;
        }

        Toast.makeText(arg0, "PhoneLocker running", Toast.LENGTH_SHORT).show();

    }

    private class obdQueryTask extends AsyncTask<Void, Void, Void> {

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

        private boolean mBluetoothConnected;
        private boolean mSpeedOverZero;

        public obdQueryTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            Log.i("+++++++++++++++++++", "async running");

            mBluetoothConnected = false;
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

            Log.i("+++++++++++++++++++", "do in background");

            try {
                mSocket.connect();
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "no connect");
                Log.i("+++++++++++++++++++", e.toString());
                return null;
            }

            Log.i("+++++++++++++++++++", "connected");

            try {
                mInputStream = mSocket.getInputStream();
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "no input stream");
                return null;
            }

            Log.i("+++++++++++++++++++", "got input stream");

            try {
                mOutputStream = mSocket.getOutputStream();
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "no output stream");
                return null;
            }

            Log.i("+++++++++++++++++++", "got output stream");

            mBluetoothConnected = true;

            // reset obd
            try {
                mOutputStream.write(("AT Z\r").getBytes());
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "no write reset to output");
                return null;
            }

            Log.i("+++++++++++++++++++", "wrote to output");

            try {
                mOutputStream.flush();
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "No flush");
            }

            Log.i("+++++++++++++++++++", "flush reset obd");

            try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); }

            Log.i("+++++++++++++++++++", "thread slept for obd reset");

            byte b;
            char c;

            while (true) {
                try {
                    b = (byte) mInputStream.read();
                    Log.i("+++++++++++++++++++", "read reset byte");
                } catch (IOException e) {
//                    Toast.makeText(mContext, "input read error", Toast.LENGTH_SHORT).show();
                    Log.i("+++++++++++++++++++", "input read error");
                    return null;
                }

                c = (char) b;
                Log.i("+++++++++++++++++++", String.valueOf(c));

                if (c == '>') {
                    Log.i("+++++++++++++++++++", "reset breaking");
                    break;
                }
                mStrBuffer.append(c);
                Log.i("+++++++++++++++++++", mResetCmdBuffer.toString());

            }

            // set protocol
            try {
                mOutputStream.write(("AT SP 0\r").getBytes());
            } catch (IOException ieo) {
//                Toast.makeText(mContext, "no write set to output", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "no write set to output");
                return null;
            }

            Log.i("+++++++++++++++++++", "sent set protocol");

            try {
                mOutputStream.flush();
            } catch (IOException ieo) {
//                Toast.makeText(mContext, "No flush", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "No flush");
            }

            Log.i("+++++++++++++++++++", "flush set protocol");

            try { Thread.sleep(600); } catch (InterruptedException e) { e.printStackTrace(); }

            Log.i("+++++++++++++++++++", "thread slept for obd set proc");

            while (true) {
                try {
                    b = (byte) mInputStream.read();
                    Log.i("+++++++++++++++++++", "read set proc byte");
                } catch (IOException e) {
//                    Toast.makeText(mContext, "input read error", Toast.LENGTH_SHORT).show();
                    Log.i("+++++++++++++++++++", "input read error");
                    return null;
                }

                c = (char) b;
                if (c == '>') {
                    Log.i("+++++++++++++++++++", "set proc breaking");
                    break;
                }
                mSetProcBuffer.append(c);
                Log.i("+++++++++++++++++++", mSetProcBuffer.toString());

            }

            // send speed command
            try {
                mOutputStream.write(("01 0D\r").getBytes());
            } catch (IOException e) {
                Log.i("+++++++++++++++++++", "no write speed cmd to output");
                return null;
            }
            Log.i("+++++++++++++++++++", "send speed");

            try {
                mOutputStream.flush();
            } catch (IOException ieo) {
                Log.i("+++++++++++++++++++", "No flush");
            }

            Log.i("+++++++++++++++++++", "flush speed/rpm");

            // read buffer
            try {
                Thread.sleep(600);
            } catch (Exception e) {
                Log.i("+++++++++++++++++++", "thread sleep error");
                return null;
            }

            Log.i("+++++++++++++++++++", "rpm speed/thread slept");

            while (true) {
                try {
                    b = (byte) mInputStream.read();
                    Log.i("+++++++++++++++++++", "read speed/rpm byte");
                } catch (IOException e) {
                    Log.i("+++++++++++++++++++", "input read error");
                    return null;
                }

                c = (char) b;
                if (c == '>') {
                    Log.i("+++++++++++++++++++", "speed/rpm breaking");
                    break;
                }
                mRpmCmdBuffer.append(c);
                Log.i("+++++++++++++++++++", mRpmCmdBuffer.toString());

            }

            String speedOutput = mRpmCmdBuffer.toString();
            Log.i("++++++++++after loop", speedOutput);
            speedOutput = speedOutput.trim();
            Log.i("++++++++++after trim", speedOutput);

            // decode
            String speedKphStr = speedOutput.substring(speedOutput.length() - 2, speedOutput.length());
            Log.i("+++speedKphAfterSub", speedKphStr);
            speedKphStr = "0x" + speedKphStr;
            Log.i("+++speedKphAfterCat", speedKphStr);
            int speedKph = Integer.decode(speedKphStr);
            Log.i("speedKphAfterDecode", String.valueOf(speedKph));

            if (speedKph > 0)
                mSpeedOverZero = true;

            Log.i("+++++++++++++++++++", Boolean.toString(mSpeedOverZero));

            if (mSpeedOverZero) {

                // closing socket before locking allows program to reconnect after unlock
                // TODO save connection state somehow
                // SharedPreferences
                try {
                    mSocket.close();
                } catch (IOException ioe) {
                    Log.i("+++++++++++++++++++", "no close");
                }

                DevicePolicyManager devman = PhoneLockerActivity.getDevicePolicyManager();
                Log.i("+++++++++++++++++++", "locking");
                devman.lockNow();
            }

            return null;
        }
    }
}