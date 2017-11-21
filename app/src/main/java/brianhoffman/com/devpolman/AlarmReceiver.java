package brianhoffman.com.devpolman;


import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;


public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {


        // TODO if locked return
        //getStatus() to get the status of your AsyncTask.
        // If status is AsyncTask.Status.RUNNING then your task is running.


        try {
            new obdQueryTask(arg0).execute();
        } catch (Exception e) {
            return;
        }

        //Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();

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

        private boolean mBluetoothConnected;
        private boolean mSpeedOverZero;

        public obdQueryTask(Context context) {
            mContext = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //Toast.makeText(mContext, "async running", Toast.LENGTH_SHORT).show();
            Log.i("+++++++++++++++++++", "async running");

            mBluetoothConnected = false;
            mSpeedOverZero = false;

            // get obd
            try {
                mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            } catch (Exception e) {
                //Toast.makeText(mContext, "no bluetooth", Toast.LENGTH_SHORT).show();
                return;
            }
            //mBluetoothAdapter.enable();
            mPairedDevices = mBluetoothAdapter.getBondedDevices();

            for (BluetoothDevice device : mPairedDevices) {
                if (device.getName().equals(OBD_NAME)) {
                    mDevice = device;
                    break;
                }
            }

            // connect to bluetooth
            try {
                // client device must have UUID of server device (server device UUID is random)
                mSocket = mDevice.createRfcommSocketToServiceRecord(mDevice.getUuids()[0].getUuid());
            } catch (IOException e) {
                //Toast.makeText(mContext, "no comm", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "no comm");
                return;
            }

        }

        @Override
        protected Void doInBackground(Void... voids) {
            Log.i("+++++++++++++++++++", "do in background");

            try {
                mSocket.connect();
            } catch (IOException e) {
//            Writer writer = new StringWriter();
//            e.printStackTrace(new PrintWriter(writer));
//            String s = writer.toString();
//            for (int i = 0; i < 5; i++)
//                Toast.makeText(arg0, s, Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, "no connect", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "no connect");
                return null;
            }

            Log.i("+++++++++++++++++++", "connected");

            try {
                mInputStream = mSocket.getInputStream();
            } catch (IOException e) {
//            Writer writer = new StringWriter();
//            e.printStackTrace(new PrintWriter(writer));
//            String s = writer.toString();
//            for (int i = 0; i < 5; i++)
//                Toast.makeText(arg0, s, Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, "no input stream", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "no input stream");
                return null;
            }

            Log.i("+++++++++++++++++++", "got input stream");

            try {
                mOutputStream = mSocket.getOutputStream();
            } catch (IOException e) {
//            Writer writer = new StringWriter();
//            e.printStackTrace(new PrintWriter(writer));
//            String s = writer.toString();
//            for (int i = 0; i < 5; i++)
//                Toast.makeText(arg0, s, Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, "no output stream", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "no output stream");
                return null;
            }

            Log.i("+++++++++++++++++++", "got output stream");

            mBluetoothConnected = true;

            // reset obd
            try {
                mOutputStream.write(("AT Z\r").getBytes());
            } catch (IOException e) {
//            Writer writer = new StringWriter();
//            e.printStackTrace(new PrintWriter(writer));
//            String s = writer.toString();
//            for (int i = 0; i < 5; i++)
//                Toast.makeText(arg0, s, Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, "no write reset to output", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "no write reset to output");
                return null;
            }

            Log.i("+++++++++++++++++++", "wrote to output");

            try {
                mOutputStream.flush();
            } catch (IOException e) {
//            Writer writer = new StringWriter();
//            e.printStackTrace(new PrintWriter(writer));
//            String s = writer.toString();
//            for (int i = 0; i < 5; i++)
//                Toast.makeText(arg0, s, Toast.LENGTH_LONG).show();
//                Toast.makeText(mContext, "No flush", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "No flush");
            }

            Log.i("+++++++++++++++++++", "flush reset obd");

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

//        // send speed command
//        try {
//            mOutputStream.write(("01 0D\r").getBytes());
//        } catch (IOException e) {
//            Toast.makeText(arg0, "no write speed cmd to output", Toast.LENGTH_SHORT).show();
//            return;
//        }

            // send rpm command
            try {
                mOutputStream.write(("01 0C\r").getBytes());
            } catch (IOException e) {
//                Toast.makeText(mContext, "no write rpm cmd to output", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "no write rpm cmd to output");
                return null;
            }

            Log.i("+++++++++++++++++++", "send rpm");

            try {
                mOutputStream.flush();
            } catch (IOException ieo) {
//                Toast.makeText(mContext, "No flush", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "No flush");
            }

            Log.i("+++++++++++++++++++", "flush rpm");

            // read buffer

            try {
                Thread.sleep(1000);
            } catch (Exception e) {
//                Toast.makeText(mContext, "thread sleep error", Toast.LENGTH_SHORT).show();
                Log.i("+++++++++++++++++++", "thread sleep error");
                return null;
            }

            Log.i("+++++++++++++++++++", "thread slept");

            byte b;
            char c;

            while (true) {
                try {
                    b = (byte) mInputStream.read();
                } catch (IOException e) {
//                    Toast.makeText(mContext, "input read error", Toast.LENGTH_SHORT).show();
                    Log.i("+++++++++++++++++++", "input read error");
                    return null;
                }

                c = (char) b;
                if (c == '<') break;
                mStrBuffer.append(c);
            }

            Log.i("+++++++++++++++++++", mStrBuffer.toString());

            // decode
            String speedKphStr = mStrBuffer.substring(mStrBuffer.length() - 2, mStrBuffer.length());
            speedKphStr = "0x" + speedKphStr;
            int speedKph = Integer.decode(speedKphStr);
            if (speedKph > 0)
                mSpeedOverZero = true;

            Log.i("+++++++++++++++++++", Boolean.toString(mSpeedOverZero));

            if (mBluetoothConnected && mSpeedOverZero) {
                DevicePolicyManager devman = MainActivity.getDevicePolicyManager();
                devman.lockNow();
            }

            return null;
        }
    }

//    private void SendMessageToMainActivity(String stacktrace) {
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction("ServiceToActivityAction");
//        broadcastIntent.putExtra("ServiceToActivityKey", stacktrace);
//        sendBroadcast(broadcastIntent);
//    }

}