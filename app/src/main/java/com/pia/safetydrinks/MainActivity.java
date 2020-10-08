package com.pia.safetydrinks;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private EditText mEditTextName;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;
    private Button mBtnRegister;
    private Button mBtnSendToLoginButton;

    private String name = "";
    private String email = "";
    private String password = "";
    FirebaseAuth mAuth;
    DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mEditTextName = (EditText) findViewById(R.id.editTextName);
        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mBtnRegister = (Button) findViewById(R.id.btnRegister);
        mBtnSendToLoginButton = (Button) findViewById(R.id.btnSengToLogin);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = mEditTextName.getText().toString();
                email = mEditTextEmail.getText().toString();
                password = mEditTextPassword.getText().toString();

                if(!name.isEmpty() && !email.isEmpty() && !password.isEmpty()){
                    if(password.length() >= 6 ){
                        if (ValidateEmailAdress(mEditTextEmail)){
                            registerUser();
                        }
                        else {
                            Toast.makeText(MainActivity.this, "Ingresa un email valido", Toast.LENGTH_SHORT).show();
                        }
                    }

                    else{
                        Toast.makeText(MainActivity.this, "Tu contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(MainActivity.this, "Debes Completar los campos", Toast.LENGTH_SHORT).show();
                }

            }
        });

        mBtnSendToLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Login.class));
            }
        });


    }


    private void registerUser(){
        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                Map<String, Object>mapDeDatos = new HashMap<>();
                mapDeDatos.put("Nombre", name);
                mapDeDatos.put("E-Mail", email);
                mapDeDatos.put("Contraseña", password);

                mDatabase.child("Usuario").push().setValue(mapDeDatos).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();
                            user.sendEmailVerification();
                            Toast.makeText(MainActivity.this, "Favor de validar su correo electronico", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(MainActivity.this, Login.class));
                            finish();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "No se registro Correctamente", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }
        });

    }

    private boolean  ValidateEmailAdress(EditText mEditTextEmail){
        String emailInput = mEditTextEmail.getText().toString();

        if(!emailInput.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(emailInput).matches()){
            return true;
            //cool
        }
        else{
            Toast.makeText(this, "introducir un email valido", Toast.LENGTH_SHORT).show();
            return false;
        }
    }


}
