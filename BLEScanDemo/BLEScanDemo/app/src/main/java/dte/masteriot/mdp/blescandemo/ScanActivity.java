package dte.masteriot.mdp.blescandemo;

import android.Manifest;
import android.bluetooth.BluetoothProfile;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.bluetooth.le.BluetoothLeScanner;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.content.Context;
import android.os.Bundle;
import android.util.SparseArray;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ScanActivity extends AppCompatActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_COARSE_LOCATION = 1;

    String TAG = "ScanActivity";

    BluetoothAdapter mBluetoothAdapter;
    BluetoothLeScanner scanner;
    ScanSettings scanSettings;
    MIOT_ScanCallBack mScanCallback;

    private List<String> scannedDevicesList;
    private ArrayAdapter<String> adapter;

    ListView devicesList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);

        //Define listview in layout
        devicesList = (ListView) findViewById(R.id.devicesList);

        //Setup list on device click listener
        // TODO

        //Inicialize de devices list
        scannedDevicesList = new ArrayList<>();

        //Inicialize the list adapter for the listview with params: Context / Layout file / TextView ID in layout file / Devices list
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, android.R.id.text1, scannedDevicesList);

        //Set the adapter to the listview
        devicesList.setAdapter(adapter);

        //Check for permissions on BT adapter
        checkLocationPermissionBT();


    }

    @Override
    protected void onStop() {
        super.onStop();
        startLeScan(false);
    }

    int START_BLUETOOTH_CODE = 32;

    private boolean initBT() {
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        mBluetoothAdapter = bluetoothManager.getAdapter();

        Log.d(TAG, "mBluetoothAdapter" + mBluetoothAdapter.toString());

        if ( mBluetoothAdapter == null)
        {
//            Toast.makeText(ScanActivity.this, "Bluetooth adapter not found", Toast.LENGTH_SHORT).show();
            Log.d( TAG, "Bluetooth adapter not found...");
            showAlertMessage();
            return false;
        }
        else {


            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, START_BLUETOOTH_CODE);
//            Toast.makeText(ScanActivity.this, "Bluetooth adapter found", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Bluetooth adapter found...");

            //Create the scan settings
            ScanSettings.Builder scanSettingsBuilder = new ScanSettings.Builder();
            //Set scan latency mode. Lower latency, faster device detection/more battery and resources consumption
            scanSettingsBuilder.setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY);
            //Wrap settings together and save on a settings var
            scanSettings = scanSettingsBuilder.build();

            //Get the BLE scanner from the BT adapter
            scanner = mBluetoothAdapter.getBluetoothLeScanner();
            mScanCallback = new MIOT_ScanCallBack();
            return true;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent resultIntent) {
        // Check which request it is that we're responding to
        if (requestCode == START_BLUETOOTH_CODE) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {

            }
        }
    }

    private void checkLocationPermissionBT() {
        //If Android version is M (6.0 API 23) or newer, check if it has Location permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (this.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                //If Location permissions are not granted for the app, ask user for it! Request response will be received in the onRequestPermissionsResult.
                requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_COARSE_LOCATION);
            }
            else {
                //init Bluetooth adapter
                if ( initBT() ) {
                    //Start scan of bluetooth devices
                    startLeScan(true);
                }
            }
        }
    }

    public void showAlertMessage()
    {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Bluetooth Alert");
        alertDialog.setMessage("BLUETOOTH adapter not found");
        alertDialog.setPositiveButton( "OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialoginterface, int i) {
                ScanActivity.this.finish();
                System.exit(0);
            }
        });

        alertDialog.show();
    }

    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        //Check if permission request response is from Location
        switch (requestCode) {
            case PERMISSION_REQUEST_COARSE_LOCATION: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //User granted permissions. Setup the scan settings
                    Log.i( TAG, "/+++++++++++++++++++++");
                    Log.d( TAG, "coarse location permission granted");
                    Log.i( TAG, "/+++++++++++++++++++++");

                    //init Bluetooth adapter
                    if ( initBT() ) {
                        //Start scan of bluetooth devices
                        startLeScan(true);
                    }
                } else {
                    //User denied Location permissions. Here you could warn the user that without
                    //Location permissions the app is not able to scan for BLE devices and eventually
                    //In this case we just close the app
                    finish();
                }
                return;
            }
        }
    }

    private void startLeScan(boolean ends) {
        if (ends) {
            //********************
            //START THE BLE SCAN
            //********************
            //Scanning parameters FILTER / SETTINGS / RESULT CALLBACK. Filter are used to define a particular
            //device to scan for. The Callback is defined above as a method.
            //scanner.startScan(null, scanSettings, mScanCallback);
            scanner.startScan(null, scanSettings, mScanCallback);
        } else {
            //Stop scan
            if ( mScanCallback != null ) {
                scanner.stopScan(mScanCallback);
            }
        }
    }

    class MIOT_ScanCallBack extends ScanCallback {
        @Override
        public void onScanResult(int callbackType, ScanResult result) {
            super.onScanResult(callbackType, result);

            //Here will be received all the detected BLE devices around. "result" contains the device
            //address and name as a BLEPeripheral, the advertising content as a ScanRecord, the Rx RSSI
            //and the timestamp when received. Type result.get... to see all the available methods you can call.

            //Convert advertising bytes to string for a easier parsing. GetBytes may return a NullPointerException. Treat it right(try/catch).
            //Beacons information comes here
            byte[] advertisingPayload = result.getScanRecord().getBytes();

            //Print the advertising String in the LOG with other device info (ADDRESS - RSSI - ADVERTISING - NAME)
            Log.i( TAG, "/+++++++++++++++++++++");
            Log.i(TAG, result.getDevice().getAddress() + " -RSSI: " + result.getRssi() + " -Name: " + result.getDevice().getName());
            Log.i( TAG, "/+++++++++++++++++++++");

            //Check if scanned device is already in the list by mac address
            boolean contains = false;
            for (int i = 0; i < scannedDevicesList.size(); i++) {
                if (scannedDevicesList.get(i).contains(result.getDevice().getAddress())) {
                    //Device already added
                    contains = true;
                    //Replace the device with updated values in that position
                    scannedDevicesList.set(i, result.getRssi() + "  " + result.getDevice().getName() + "\n       (" + result.getDevice().getAddress() + ")");
                    break;
                }
            }

            if (!contains) {
                //Scanned device not found in the list. NEW => add to list
                scannedDevicesList.add(result.getRssi() + "  " + result.getDevice().getName() + "\n       (" + result.getDevice().getAddress() + ")");
            }

            //After modify the list, notify the adapter that changes have been made so it updates the UI.
            //UI changes must be done in the main UI thread
            runOnUiThread(new UI_Task());
        }
    }

    class UI_Task implements Runnable {
        @Override
        public void run() {
            adapter.notifyDataSetChanged();
        }
    }
}