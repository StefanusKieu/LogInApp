package com.example.user.loginapp;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity implements  View.OnClickListener{

    private Button buttonLogIn;
    private EditText enterEmail;
    private EditText enterPassword;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        enterEmail= (EditText) findViewById(R.id.enterEmail);
        enterPassword= (EditText) findViewById(R.id.enterPassword);
        buttonLogIn= (Button) findViewById(R.id.buttonLogIn);

        buttonLogIn.setOnClickListener(this);
        progressDialog= new ProgressDialog(this);
    }

    public void goToSignUp(View view) {
        finish();
        Toast.makeText(this,"Please Sign Up here.",Toast.LENGTH_SHORT).show();
        Intent i = new Intent(this, SignUpPage.class);
        startActivity(i);
    }

    private void userLogin(){
        String email = enterEmail.getText().toString().trim();
        String password = enterPassword.getText().toString().trim();
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password.",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Logging in ....");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                progressDialog.dismiss();

                if(task.isSuccessful()){
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }else{
                    Toast.makeText(LoginPage.this,"Could not log in.",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void onClick(View view) {
        if (view==buttonLogIn){
            userLogin();
        }
    }
}
