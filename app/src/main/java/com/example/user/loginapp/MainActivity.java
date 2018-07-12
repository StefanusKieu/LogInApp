package com.example.user.loginapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private Button buttonLogOut;
    private TextView textUsername;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        firebaseAuth=FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        if (firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginPage.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        textUsername = (TextView) findViewById(R.id.textUsername);

        textUsername.setText(user.getEmail());

        buttonLogOut.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view==buttonLogOut){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginPage.class));
        }
    }
}
