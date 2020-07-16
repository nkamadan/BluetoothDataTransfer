package com.example.erkentani;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.ParcelUuid;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    Button openBlt;
    Button pairBtn;
    ListView paired;
    BluetoothAdapter myBtAdapter;
    Intent enablingIntent;
    int requestCodeEnable;
    private static final String NAME= "SelfHealth";
    private static final UUID MY_UUID= UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        openBlt= findViewById(R.id.btnBluetooth);
        pairBtn=findViewById(R.id.btnpaired);
        paired= findViewById(R.id.lst);
        myBtAdapter= BluetoothAdapter.getDefaultAdapter();
        enablingIntent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        requestCodeEnable=1; //any number greater than 0 is OK
        bluetoothOnMethod();
        ExeButton();
    }

    private void ExeButton() {
        pairBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Set<BluetoothDevice> devices= myBtAdapter.getBondedDevices();
                String[] strings= new String[devices.size()];
                int index=0;
                if(devices.size()>0){
                    for(BluetoothDevice device: devices){
                        strings[index]=device.getName();
                        index++;
                    }
                    ArrayAdapter<String> arrAdp= new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_list_item_1,strings);
                    paired.setAdapter(arrAdp);
                }
                else{
                    Toast.makeText(getApplicationContext(), "There are no paired device! Please pair your device...", Toast.LENGTH_SHORT).show();
                }

                paired.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        BluetoothDevice mdevice = null;
                        int index=0;
                        for(BluetoothDevice dvc: devices){
                            if(index==position){
                                mdevice=dvc;
                                Log.println(Log.ERROR,"DEV", String.valueOf(position));
                                ConnectThread clientThread= new ConnectThread(mdevice);//burda threadi mdevice ile cagir. secilen item mdevice.
                                clientThread.start();
                                break;
                            }
                        }
                    }
                });

            }
        });
    }

    //CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD***
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket
            // because mmSocket is final.
            BluetoothSocket tmp = null;
            mmDevice = device;

            try {
                // Get a BluetoothSocket to connect with the given BluetoothDevice.
                // MY_UUID is the app's UUID string, also used in the server code.
                tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
            } catch (IOException e) {
                //Log.e(TAG, "Socket's create() method failed", e);
                Log.println(Log.ERROR,"DEV","CREATERFCOMM METHOD FAILED");
            }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it otherwise slows down the connection.
            myBtAdapter.cancelDiscovery();

            try {
                // Connect to the remote device through the socket. This call blocks
                // until it succeeds or throws an exception.
                mmSocket.connect();
            } catch (IOException connectException) {
                Log.println(Log.ERROR,"DEV","UNABLE TO CONNECT TO CLIENT SOCKET");
                // Unable to connect; close the socket and return.
                try {
                    mmSocket.close();
                } catch (IOException closeException) {
                    //Log.e(TAG, "Could not close the client socket", closeException);
                    Log.println(Log.ERROR,"DEV","COULD NOT CLOSE THE CLIENT SOCKET");
                }
                return;
            }

            // The connection attempt succeeded. Perform work associated with
            // the connection in a separate thread.
            manageMyConnectedSocket(mmSocket);
        }

        // Closes the client socket and causes the thread to finish.
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) {
                //Log.e(TAG, "Could not close the client socket", e);
                Log.println(Log.ERROR,"DEV","COULD NOT CLOSE THE CLIENT SOCKET");
            }
        }
    }
    //CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD*** CLIENT THREAD***

    private void manageMyConnectedSocket(BluetoothSocket mmSocket) {
        Log.println(Log.ERROR,"DEV","I AM IN THE MANAGEMYCONNECTEDSOCKET METHOD, SEEMS INTERESTING");
    }









    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == requestCodeEnable) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), "Bluetooth is enabled succesfully!", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(getApplicationContext(), "Bluetooth enabling has been cancelled!", Toast.LENGTH_SHORT).show();
            }
        } else {

        }
        super.onActivityResult(requestCode, resultCode, data);
    }






    private void bluetoothOnMethod() {
        openBlt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getApplicationContext();
                int duration = Toast.LENGTH_SHORT;
                if(myBtAdapter==null){
                    Toast.makeText(context, "Bluetooth is not available in this device!", duration).show();
                }
                else{
                    if(!myBtAdapter.isEnabled()){ //bluetooth is NOT ON
                        startActivityForResult(enablingIntent, requestCodeEnable);

                    }
                    else{ //bluetooth is already ON
                        Toast.makeText(context,"Bluetooth is ON",duration).show();
                    }

                }
            }
        });
    }
}