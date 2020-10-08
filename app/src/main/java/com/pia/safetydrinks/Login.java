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

public class Login extends AppCompatActivity {

    private Button mbtnLogin;
    private Button mbtnSendToRegister;
    private EditText mEditTextEmail;
    private EditText mEditTextPassword;

    FirebaseAuth mAuth;

    private String email="";
    private String password = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mEditTextEmail = (EditText) findViewById(R.id.editTextEmail);
        mEditTextPassword = (EditText) findViewById(R.id.editTextPassword);
        mbtnLogin = (Button) findViewById(R.id.btnLogin);
        mAuth = FirebaseAuth.getInstance();

        mbtnSendToRegister = (Button) findViewById(R.id.btnSendToRegister);

        mbtnSendToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, MainActivity.class));
            }
        });

        mbtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = mEditTextEmail.getText().toString();
                password = mEditTextPassword.getText().toString();

                if(!email.isEmpty() && !password.isEmpty()){
                    if(ValidateEmailAdress(mEditTextEmail)){
                        LoginUser();
                    }
                    else {
                        Toast.makeText(Login.this, "Ingresa un email valido", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Login.this, "Complete todos los campos", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //Validacion de email
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
    private void LoginUser(){
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                FirebaseUser user = mAuth.getCurrentUser();

                if (task.isSuccessful()){
                    if(user.isEmailVerified()){
                        startActivity(new Intent(Login.this, bluetoothDevices.class));
                        finish();
                    }
                    else{
                        Toast.makeText(Login.this, "No a verificado su email", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    Toast.makeText(Login.this, "No se inicio correctamente, compruebe sus datos", Toast.LENGTH_SHORT).show();
                }
            }
        });


    }
}
