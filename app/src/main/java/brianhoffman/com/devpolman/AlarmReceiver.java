package brianhoffman.com.devpolman;


import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Set;


public class AlarmReceiver extends BroadcastReceiver {

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

    private boolean mTest = true;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        //Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();
        //MainActivity activity = (MainActivity) arg0;

        mBluetoothConnected = false;
        mSpeedOverZero = false;

        // check if device locked already
        //if(mBluetoothConnected && mSpeedOverZero) return;

        // get obd
        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
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
            Toast.makeText(arg0, "no comm", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mSocket.connect();
        }catch (IOException e) {
            Toast.makeText(arg0, "no connect", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mInputStream = mSocket.getInputStream();
        } catch (IOException e) {
            Toast.makeText(arg0, "no input stream", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mOutputStream = mSocket.getOutputStream();
        } catch (IOException e) {
            Toast.makeText(arg0, "no output stream", Toast.LENGTH_SHORT).show();
            return;
        }

        mBluetoothConnected = true;

        // reset obd
        try {
            mOutputStream.write(("AT Z\r").getBytes());
        } catch (IOException ieo) {
            Toast.makeText(arg0, "no write reset to output", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mOutputStream.flush();
        } catch (IOException ieo) {
            Toast.makeText(arg0, "No flush", Toast.LENGTH_SHORT).show();
        }

        // set protocol
        try {
            mOutputStream.write(("AT SP 0\r").getBytes());
        } catch (IOException ieo) {
            Toast.makeText(arg0, "no write set to output", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mOutputStream.flush();
        } catch (IOException ieo) {
            Toast.makeText(arg0, "No flush", Toast.LENGTH_SHORT).show();
        }

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
            Toast.makeText(arg0, "no write rpm cmd to output", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            mOutputStream.flush();
        } catch (IOException ieo) {
            Toast.makeText(arg0, "No flush", Toast.LENGTH_SHORT).show();
        }

        // read buffer
        try {
            if (mInputStream.available() == 0) {
                Toast.makeText(arg0, "No data to read", Toast.LENGTH_SHORT).show();
                return;
            }
        } catch (IOException ioe ) {

            return;
        }

        byte b;
        char c;

        while (true) {
            try {
                b = (byte) mInputStream.read();
            } catch (IOException e) {

                return;
            }

            c = (char) b;
            if (c == '<') break;
            mStrBuffer.append(c);
        }

        // decode
        String speedKphStr = mStrBuffer.substring(mStrBuffer.length() - 2, mStrBuffer.length());
        speedKphStr = "0x" + speedKphStr;
        int speedKph = Integer.decode(speedKphStr);
        if (speedKph > 0)
            mSpeedOverZero = true;

        if(mBluetoothConnected && mSpeedOverZero) {
            DevicePolicyManager devman = MainActivity.getDevicePolicyManager();
            devman.lockNow();
        }

//        if (mTest) {
//            mTest = false;
//            DevicePolicyManager devman = MainActivity.getDevicePolicyManager();
//            devman.lockNow();
//        }

    }

}