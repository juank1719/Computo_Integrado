package com.pia.safetydrinks;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


public class Profile extends AppCompatActivity {
//------------
    Handler bluetoothIn;
    final int handlerState = 0;
    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket= null;
    private StringBuilder DataStringIN = new StringBuilder();
    private ConnectedThread myConexionBT;

    private static final UUID BTMODULEUUID = UUID.fromString("19B10001-E8F2-537E-4F6C-D104768A1214");

    private static String address = null;

    private FirebaseAuth mAuth;

    private Button mbtnSignOut;

    private TextView mBuffer;

    private EditText edtTextEnviarID;
    private Button enviarID, borrarHuella, registrarHuella, cerrarSecion, btnConectar, btnDesconectar;
    private TextView respuesta;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        edtTextEnviarID = findViewById(R.id.editTextSendID);

        enviarID = findViewById(R.id.buttonEnviarID);
        borrarHuella = findViewById(R.id.buttonBorrarHuella);
        registrarHuella = findViewById(R.id.buttonRegistraHuella);
        cerrarSecion = findViewById(R.id.btnSignOut);
        btnConectar = findViewById(R.id.buttonConectar);
        btnDesconectar = findViewById(R.id.buttonDesconectar);

        respuesta = findViewById(R.id.textViewStatus);


        mAuth = FirebaseAuth.getInstance();
        mbtnSignOut = (Button) findViewById(R.id.btnSignOut);
        mbtnSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(Profile.this, MainActivity.class));
                finish();
            }
        });

        bluetoothIn = new Handler() {
            public void handleMessage(android.os.Message msg) {
                if (msg.what == handlerState) {
                    char MyCaracter = (char) msg.obj;

                    if (MyCaracter == 'A') {
                        respuesta.setText("LETRA A MANDADA");
                    }
                    if (MyCaracter == 'B') {
                        respuesta.setText("LETRA B MANDADA");
                    }//Interacción con los datos de ingreso
                }
            }
        };
        PorFavor();
    }
    private void PorFavor(){
        btAdapter = BluetoothAdapter.getDefaultAdapter();
        VerificarEstadoBT();
        enviarID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String obtenerDatos = edtTextEnviarID.getText().toString();
                //respuesta.setText(obtenerDatos);
                myConexionBT.write((obtenerDatos));
            }
        });


        //Button borrar huella
        borrarHuella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               // myConexionBT.write("B");

            }
        });
        //Button registrar huella
        registrarHuella.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              //  myConexionBT.write("A");

            }
        });
        btnConectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Profile.this, bluetoothDevices.class));
            }
        });


        btnDesconectar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(btSocket!=null){
                    try {btSocket.close();}
                    catch(IOException e){
                        Toast.makeText(Profile.this, "Error", Toast.LENGTH_SHORT).show();
                    }
                }
                finish();
            }
        });


    }


    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws  IOException{
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }
    @Override

    public void onResume() {
        super.onResume();

        Intent intent = getIntent();
        final String address = intent.getStringExtra(bluetoothDevices.EXTRA_DEVICE_ADDRESS);
        BluetoothDevice device = btAdapter.getRemoteDevice(address);

        try
        {
            btSocket = createBluetoothSocket(device);
        } catch (IOException e) {
            Toast.makeText(getBaseContext(), "La creacción del Socket fallo", Toast.LENGTH_LONG).show();
        }
        // Establece la conexión con el socket Bluetooth.
        try
        {
            btSocket.connect();
        } catch (IOException e) {
            try {
                btSocket.close();
            } catch (IOException e2) {}
        }

        myConexionBT = new ConnectedThread(btSocket);
        myConexionBT.start();

    }

 /*   @Override
    public void onPause()
    {
        super.onPause();
        try
        { // Cuando se sale de la aplicación esta parte permite que no se deje abierto el socket
            btSocket.close();
        } catch (IOException e2) {}
    }

  */

    private void VerificarEstadoBT(){
        if(btAdapter==null){
            Toast.makeText(getBaseContext(), "El dispositivo no soporta bluetooth", Toast.LENGTH_LONG).show();
            //Toast.makeText(this, "El dispositivo no soporta bluetooth", Toast.LENGTH_SHORT).show();
        }
        else{
            if(btAdapter.isEnabled()){
            }
            else{
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent,1);
            }
        }
    }

    private class ConnectedThread extends Thread{
        private final InputStream mmInStream;
        private final OutputStream mmOutputStream;

        public ConnectedThread(BluetoothSocket socket){
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try{
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();

            }catch (IOException e){}
            mmInStream = tmpIn;
            mmOutputStream = tmpOut;
        }

        public void run(){
            byte[] byte_in = new byte[256];
            int bytes;
            while (true) {
                try {
                    bytes = mmInStream.read(byte_in);
                    String readMessage = new String(byte_in, 0, bytes);
                    // Envia los datos obtenidos hacia el evento via handler
                    bluetoothIn.obtainMessage(handlerState, bytes, -1, readMessage).sendToTarget();
                } catch (IOException e) {
                    break;
                }
            }

        }

        public void write(String input){
            try{
                mmOutputStream.write(input.getBytes());
            }catch (IOException e){
                //Toast.makeText(Profile.this, "La conexion fallo", Toast.LENGTH_SHORT).show();
                Toast.makeText(getBaseContext(), "La Conexión fallo", Toast.LENGTH_LONG).show();
                finish();
            }

        }

    }




}


