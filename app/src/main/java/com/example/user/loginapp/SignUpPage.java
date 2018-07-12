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
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class SignUpPage extends AppCompatActivity implements View.OnClickListener {

    private Button buttonContinue;
    private EditText editEmailAddress;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editUserName;
    private ProgressDialog progressDialog;

    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();


        buttonContinue= (Button) findViewById(R.id.buttonContinue);
        editEmailAddress= (EditText) findViewById(R.id.editEmailAddress);
        editPassword= (EditText) findViewById(R.id.editPassword);
        editConfirmPassword= (EditText) findViewById(R.id.editConfirmPassword);
        editUserName= (EditText) findViewById(R.id.editUserName);

        buttonContinue.setOnClickListener(this);
    }

    private void registerUser(){
        String email = editEmailAddress.getText().toString().trim();
        String password = editPassword.getText().toString().trim();
        String username = editUserName.getText().toString().trim();
        String confirmPassword = editConfirmPassword.getText().toString().trim();


        if(TextUtils.isEmpty(username)){
            Toast.makeText(this,"Please enter your username.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(email)){
            Toast.makeText(this,"Please enter your email.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(TextUtils.isEmpty(password)){
            Toast.makeText(this,"Please enter your password.",Toast.LENGTH_SHORT).show();
            return;
        }
        if(!password.equals(confirmPassword)){
            Toast.makeText(this,"The password and confirmation password do not match. Please make sure they match.",Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.setMessage("Registering User....");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(email,password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete (@NonNull Task<AuthResult> task){
                        progressDialog.dismiss();
                        if (task.isSuccessful()){
                            Toast.makeText(SignUpPage.this,"Registered Successfully",Toast.LENGTH_SHORT).show();
                            saveUserInformation();
                            finish();
                            startActivity(new Intent(getApplicationContext(),MainActivity.class));
                        }else{
                            Toast.makeText(SignUpPage.this,"Could not register.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInformation(){
        String name = editUserName.getText().toString().trim();
        String email = editEmailAddress.getText().toString().trim();
        String password = editPassword.getText().toString().trim();

        UserInformation userInformation = new UserInformation(name,email,password);
        FirebaseUser user = firebaseAuth.getCurrentUser();
        databaseReference.child(user.getUid()).setValue(userInformation);
    }

    @Override
    public void onClick(View view){
        if (view==buttonContinue){
            registerUser();
        }
    }


}
