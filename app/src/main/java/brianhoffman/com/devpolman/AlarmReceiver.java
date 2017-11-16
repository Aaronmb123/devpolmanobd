package brianhoffman.com.devpolman;


import android.app.admin.DevicePolicyManager;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

public class AlarmReceiver extends BroadcastReceiver {

    private final static String OBD_NAME = "OBDII";

    private BluetoothSocket mSocket;
    private BluetoothDevice mDevice;
    private BluetoothAdapter mBluetoothAdapter;
    private Set<BluetoothDevice> pairedDevices;

    private InputStream mInputStream;
    private OutputStream mOutputStream;
    private byte[] mBuffer;
    private StringBuilder mStrBuffer = new StringBuilder();

    private boolean mBluetoothConnected;
    private boolean mSpeedOverZero;

    @Override
    public void onReceive(Context arg0, Intent arg1) {
        // For our recurring task, we'll just display a message
        Toast.makeText(arg0, "I'm running", Toast.LENGTH_SHORT).show();
//        MainActivity activity = (MainActivity) arg0;

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        pairedDevices = mBluetoothAdapter.getBondedDevices();
        String devices = "";

        for (BluetoothDevice device : pairedDevices) {
            devices += device.getName() + " " + device.getAddress() + " ";
            mPairedDevicesTV.setText(devices);
            if (device.getName().equals(OBD_NAME)) {
                mDevice = device;
                mDeviceTV.setText(mDevice.getName());
                break;
            }
        }
        // get obd
        // connect to bluetooth
        // set mBluetoothConnected flag
        // reset obd
        // set protocol
        // send speed command
        // read buffer
        // decode
        // return mSpeedOverZero

        if(mBluetoothConnected && mSpeedOverZero) {
            DevicePolicyManager devman = MainActivity.getDevicePolicyManager();
            devman.lockNow();
        }

    }

}