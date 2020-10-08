package com.pia.safetydrinks;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Set;

public class bluetoothDevices extends AppCompatActivity {

    private static final String TAG = "bluetoothDevices";

    ListView IdLista;

    public static String EXTRA_DEVICE_ADDRESS = "device_address";

    private BluetoothAdapter mtAdapter;

    private ArrayAdapter mPaireDevicesArrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bluetooth_devices);
    }

    public void onResume(){
        super.onResume();

        VerificarEstadoBT();

        mPaireDevicesArrayAdapter = new ArrayAdapter(this, R.layout.devicesfound);

        IdLista = (ListView) findViewById(R.id.IdLista);
        IdLista.setAdapter(mPaireDevicesArrayAdapter);
        IdLista.setOnItemClickListener(mDeviceClickListener);

        mtAdapter = BluetoothAdapter.getDefaultAdapter();

        Set<BluetoothDevice> pairedDevices = mtAdapter.getBondedDevices();


        if(pairedDevices.size() >0 ){
            for(BluetoothDevice device : pairedDevices){
                mPaireDevicesArrayAdapter.add(device.getName() + "\n"+ device.getAddress());
            }
        }
    }

    private AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView adapterView, View view, int i, long l) {

            String info = ((TextView) view).getText().toString();
            String address = info.substring(info.length() - 17);

            finishAffinity();

            Intent intent = new Intent(bluetoothDevices.this, Profile.class);
            intent.putExtra(EXTRA_DEVICE_ADDRESS, address);
            startActivity(intent);
        }
    };

    private void VerificarEstadoBT(){
        mtAdapter = BluetoothAdapter.getDefaultAdapter();
        if(mtAdapter==null){
            Toast.makeText(this, "El dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();
            return;
        }
        else{
            if(mtAdapter.isEnabled()){
                Log.d(TAG, "...Bluetooth Activado... ");
            }
            else{
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,1);
            }
        }
    }
}
