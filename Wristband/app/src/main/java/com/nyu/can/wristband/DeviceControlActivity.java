/*
 * Copyright (C) 2013 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.nyu.can.wristband;

import android.app.Activity;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattService;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

/**
 * For a given BLE device, this Activity provides the user interface to connect, display data,
 * and display GATT services and characteristics supported by the device.  The Activity
 * communicates with {@code BluetoothLeService}, which in turn interacts with the
 * Bluetooth LE API.
 */
public class DeviceControlActivity extends Activity {
    private final static String TAG = DeviceControlActivity.class.getSimpleName();
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static final byte[] DISABLE_NOTIFICATION_VALUE = {0x00, 0x00};
    public static final byte[] ENABLE_NOTIFICATION_VALUE = {0x01, 0x00};

    public static final String EXTRAS_DEVICE_NAME = "DEVICE_NAME";
    public static final String EXTRAS_DEVICE_ADDRESS = "DEVICE_ADDRESS";

    private TextView mConnectionState;
    private TextView mDataField, mInfoField;
    private String mDeviceName;
    private String mDeviceAddress;
    private ExpandableListView mGattServicesList;
    private BluetoothLeService mBluetoothLeService;
    private ArrayList<ArrayList<BluetoothGattCharacteristic>> mGattCharacteristics =
            new ArrayList<ArrayList<BluetoothGattCharacteristic>>();
    private boolean mConnected = false, mService = false, mData = false;
    private BluetoothGattCharacteristic mNotifyCharacteristic;


    byte[] step_Data = null;
    byte[] fitbit_Data = null;
    byte[] battery_data = null;
    byte[] info_data =null;

    Button display;





    // Code to manage Service lifecycle.
    private final ServiceConnection mServiceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder service) {
            mBluetoothLeService = ((BluetoothLeService.LocalBinder) service).getService();
            if (!mBluetoothLeService.initialize()) {
                Log.e(TAG, "Unable to initialize Bluetooth");
                finish();
            }
            // Automatically connects to the device upon successful start-up initialization.
            mBluetoothLeService.connect(mDeviceAddress);
            Toast toast = Toast.makeText(getApplicationContext(),"Connecting",Toast.LENGTH_SHORT);
            toast.show();

        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            mBluetoothLeService = null;
        }
    };

    // Handles various events fired by the Service.
    // ACTION_GATT_CONNECTED: connected to a GATT server.
    // ACTION_GATT_DISCONNECTED: disconnected from a GATT server.
    // ACTION_GATT_SERVICES_DISCOVERED: discovered GATT services.
    // ACTION_DATA_AVAILABLE: received data from the device.  This can be a result of read
    //                        or notification operations.
    private final BroadcastReceiver mGattUpdateReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            final String action = intent.getAction();
            if (BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)) {
                mConnected = true;
                updateConnectionState(R.string.connected);
                invalidateOptionsMenu();
                if(mDeviceName.contains("MI")) mBluetoothLeService.bonddevice();
                mDataField.setText(mDeviceName);
            } else if (BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)) {
                mConnected = false;
                mService = false;
                mData = false;
                updateConnectionState(R.string.disconnected);
                invalidateOptionsMenu();
                clearUI();
                if(mDeviceName.contains("MI"))mBluetoothLeService.removebond();
            } else if (BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)) {
                // Show all the supported services and characteristics on the user interface.
                if(mDeviceName.contains("Charge"))getfitbitdata();
                if(mDeviceName.contains("MI")) pairMiband();
                mService = true;
                //deviceinfo();
                //displayData("Device ID:" + bytesToHex(info_data).substring(0,3) + '\n');

            } else if (BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)) {
                mData = true;

            }
        }
    };

    // If a given GATT characteristic is selected, check for supported features.  This sample
    // demonstrates 'Read' and 'Notify' features.  See
    // http://d.android.com/reference/android/bluetooth/BluetoothGatt.html for the complete
    // list of supported characteristic features.
    private final ExpandableListView.OnChildClickListener servicesListClickListner =
            new ExpandableListView.OnChildClickListener() {
                @Override
                public boolean onChildClick(ExpandableListView parent, View v, int groupPosition,
                                            int childPosition, long id) {
                    if (mGattCharacteristics != null) {
                        final BluetoothGattCharacteristic characteristic =
                                mGattCharacteristics.get(groupPosition).get(childPosition);
                        final int charaProp = characteristic.getProperties();
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_READ) > 0) {
                            // If there is an active notification on a characteristic, clear
                            // it first so it doesn't update the data field on the user interface.
                            if (mNotifyCharacteristic != null) {
                                mBluetoothLeService.setCharacteristicNotification(
                                        mNotifyCharacteristic, false);
                                mNotifyCharacteristic = null;
                            }
                            mBluetoothLeService.readCharacteristic(characteristic);
                        }
                        if ((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY) > 0) {
                            mNotifyCharacteristic = characteristic;
                            mBluetoothLeService.setCharacteristicNotification(
                                    characteristic, true);
                        }
                        return true;
                    }
                    return false;
                }
    };

    private void clearUI() {
        //mGattServicesList.setAdapter((SimpleExpandableListAdapter) null);
        //mDataField.setText(R.string.no_data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gatt_services_characteristics);
        display = (Button)findViewById(R.id.button2);
        display.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(mDeviceName.contains("MI")){
                    while(step_Data == null){
                        ReadMibandstepvalue();
                    }
                        Intent intent = new Intent(DeviceControlActivity.this, MibandDisplayActivity.class);
                        intent.putExtra("step", step_Data);
                        startActivity(intent);
                    }else if(mDeviceName.contains("Charge")){
                        System.out.println(bytesToHex(fitbit_Data));
                        if(fitbit_Data != null && battery_data != null){
                        Intent intent = new Intent(DeviceControlActivity.this,FitbitDisplayActivity.class);
                        intent.putExtra("data", fitbit_Data);
                        intent.putExtra("battery",battery_data);
                        startActivity(intent);
                        }
                    }
                }
            });



        final Intent intent = getIntent();
        mDeviceName = intent.getStringExtra(EXTRAS_DEVICE_NAME);
        mDeviceAddress = intent.getStringExtra(EXTRAS_DEVICE_ADDRESS);


        // Sets up UI references.
        //((TextView) findViewById(R.id.device_address)).setText(mDeviceAddress);
        //mGattServicesList = (ExpandableListView) findViewById(R.id.gatt_services_list);
        //mGattServicesList.setOnChildClickListener(servicesListClickListner);
        mConnectionState = (TextView) findViewById(R.id.connection_state);
        mDataField = (TextView) findViewById(R.id.textView4);
        mInfoField = (TextView)findViewById(R.id.textView6);


        getActionBar().setTitle("Device");
        getActionBar().setDisplayHomeAsUpEnabled(true);
        Intent gattServiceIntent = new Intent(this, BluetoothLeService.class);
        bindService(gattServiceIntent, mServiceConnection, BIND_AUTO_CREATE);




    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mGattUpdateReceiver, makeGattUpdateIntentFilter());
        if (mBluetoothLeService != null) {
            final boolean result = mBluetoothLeService.connect(mDeviceAddress);
            Log.d(TAG, "Connect request result=" + result);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mGattUpdateReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(mServiceConnection);
        mBluetoothLeService = null;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.gatt_services, menu);
        if (mConnected) {
            menu.findItem(R.id.menu_connect).setVisible(false);
            menu.findItem(R.id.menu_disconnect).setVisible(true);
        } else {
            menu.findItem(R.id.menu_connect).setVisible(true);
            menu.findItem(R.id.menu_disconnect).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.menu_connect:
                mBluetoothLeService.connect(mDeviceAddress);
                //Intent intent = new Intent(DeviceControlActivity.this,MibandDisplayActivity.class);
               // startActivity(intent);
                return true;
            case R.id.menu_disconnect:
                mBluetoothLeService.disconnect();
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateConnectionState(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mConnectionState.setText(resourceId);
            }
        });
    }

    private void displayData(String data) {
        if (data != null) {
            mInfoField.setText(data);
        }
    }

    private void ReadMibandstepvalue(){
        List<BluetoothGattService> list = mBluetoothLeService.getSupportedGattServices();
        BluetoothGattService service =list.get(2);
        BluetoothGattCharacteristic step = service.getCharacteristic(UUID.fromString("0000ff06-0000-1000-8000-00805f9b34fb"));
        mBluetoothLeService.readCharacteristic(step);
        byte[] stepValue = step.getValue();
        if(stepValue != null) step_Data = stepValue;
    }

    private  void ReadMibanddeviceinfo(){
        List<BluetoothGattService> list = mBluetoothLeService.getSupportedGattServices();
        BluetoothGattService service = list.get(2);
        BluetoothGattCharacteristic info = service.getCharacteristic(UUID.fromString("0000ff01-0000-1000-8000-00805f9b34fb"));
        mBluetoothLeService.readCharacteristic(info);
        byte[] infovalue = info.getValue();
        if(infovalue != null) info_data = infovalue;
    }

    private void pairMiband() {
        List<BluetoothGattService> list = mBluetoothLeService.getSupportedGattServices();
        BluetoothGattService service = list.get(5);
        BluetoothGattCharacteristic alert = service.getCharacteristic(UUID.fromString("00002a06-0000-1000-8000-00805f9b34fb"));
        byte[] value = new byte[1];
        value[0] = (byte) 0x01;
        alert.setValue(value);
        mBluetoothLeService.writeCharacteristic(alert);
        Toast toast = Toast.makeText(getApplicationContext(),"click button to continue!", Toast.LENGTH_SHORT);
        toast.show();
    }

    private void ReadFitbitdatavalue(){
        List<BluetoothGattService>list = mBluetoothLeService.getSupportedGattServices();
        BluetoothGattService service = list.get(3);
        BluetoothGattCharacteristic data = service.getCharacteristic(UUID.fromString("558dfa01-4fa8-4105-9f02-4eaa93e62980"));
        mBluetoothLeService.readCharacteristic(data);
        BluetoothGattDescriptor descriptor = data.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        mBluetoothLeService.setCharacteristicNotification(data, true);
        descriptor.setValue(ENABLE_NOTIFICATION_VALUE);
        mBluetoothLeService.writeDescriptor(descriptor);
        byte[] fitbitvalue = data.getValue();
        if(fitbitvalue != null) fitbit_Data = fitbitvalue;
    }

    private void ReadFitbitbattery(){
        List<BluetoothGattService>list = mBluetoothLeService.getSupportedGattServices();
        BluetoothGattService service = list.get(5);
        BluetoothGattCharacteristic battery = service.getCharacteristic(UUID.fromString("00002a19-0000-1000-8000-00805f9b34fb"));
        mBluetoothLeService.readCharacteristic(battery);
        BluetoothGattDescriptor descriptor = battery.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
        mBluetoothLeService.setCharacteristicNotification(battery, true);
        descriptor.setValue(ENABLE_NOTIFICATION_VALUE);
        mBluetoothLeService.writeDescriptor(descriptor);
        byte[] batteryvalue = battery.getValue();
        if(batteryvalue != null) battery_data = batteryvalue;
    }

    private void getdata(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(fitbit_Data == null || battery_data == null) {
                    ReadFitbitdatavalue();
                    ReadFitbitbattery();
                }
            }
        });
        thread.start();
    }

    private void getinfo(){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                while(info_data == null){
                    ReadMibanddeviceinfo();
                }
            }
        });thread.start();
    }

    private void deviceinfo(){
        while (info_data == null){
            getinfo();
        }
    }




    // Demonstrates how to iterate through the supported GATT Services/Characteristics.
    // In this sample, we populate the data structure that is bound to the ExpandableListView
    // on the UI.
    private void getfitbitdata(){
        //if (gattServices == null) return;
        /*String uuid = null;
        String unknownServiceString = getResources().getString(R.string.unknown_service);
        String unknownCharaString = getResources().getString(R.string.unknown_characteristic);
        ArrayList<HashMap<String, String>> gattServiceData = new ArrayList<HashMap<String, String>>();
        ArrayList<ArrayList<HashMap<String, String>>> gattCharacteristicData
                = new ArrayList<ArrayList<HashMap<String, String>>>();
        mGattCharacteristics = new ArrayList<ArrayList<BluetoothGattCharacteristic>>();*/

        // Loops through available GATT Services.
        while(fitbit_Data == null && battery_data == null){
            /*HashMap<String, String> currentServiceData = new HashMap<String, String>();
            uuid = gattService.getUuid().toString();
            currentServiceData.put(
                    LIST_NAME, MibandGattAttributes.lookup(uuid, unknownServiceString));
            currentServiceData.put(LIST_UUID, uuid);
            gattServiceData.add(currentServiceData);

            ArrayList<HashMap<String, String>> gattCharacteristicGroupData =
                    new ArrayList<HashMap<String, String>>();
            ArrayList<HashMap<String, byte[]>>chargroupdata = new ArrayList<HashMap<String, byte[]>>();
            List<BluetoothGattCharacteristic> gattCharacteristics =
                    gattService.getCharacteristics();
            ArrayList<BluetoothGattCharacteristic> charas =
                    new ArrayList<BluetoothGattCharacteristic>();*/
            // Loops through available Characteristics.
               /* charas.add(gattCharacteristic);
                HashMap<String, String> currentCharaData = new HashMap<String, String>();
                uuid = gattCharacteristic.getUuid().toString();
                currentCharaData.put(
                        LIST_NAME, MibandGattAttributes.lookup(uuid, unknownCharaString));
                currentCharaData.put(LIST_UUID, uuid);
                //uuidlist.add(uuid);
                gattCharacteristicGroupData.add(currentCharaData);
            }
            mGattCharacteristics.add(charas);
            gattCharacteristicData.add(gattCharacteristicGroupData);*/
            getdata();
        }
        Toast toast = Toast.makeText(getApplicationContext(),"click button to continue!", Toast.LENGTH_SHORT);
        toast.show();

        /*SimpleExpandableListAdapter gattServiceAdapter = new SimpleExpandableListAdapter(
                this,
                gattServiceData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 },
                gattCharacteristicData,
                android.R.layout.simple_expandable_list_item_2,
                new String[] {LIST_NAME, LIST_UUID},
                new int[] { android.R.id.text1, android.R.id.text2 }
        );
        mGattServicesList.setAdapter(gattServiceAdapter);*/
    }

    private void setDeviceName(){

    }

    private static IntentFilter makeGattUpdateIntentFilter() {
        final IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        return intentFilter;
    }

    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }








}
