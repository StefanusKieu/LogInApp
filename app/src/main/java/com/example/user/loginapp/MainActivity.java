package com.example.user.loginapp;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Button buttonLogOut,buttonUpload;
    private TextView textUsername;
    private TextView textView;
    private ImageView imageUserProfilePic;

    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mFirebaseDatabase;
    private EditText typingPlace1,typingPlace2;
    private TextView showPlace1,showPlace2;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        firebaseAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseRef = mFirebaseDatabase.getReference();
        if (firebaseAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginPage.class));
        }
        FirebaseUser user = firebaseAuth.getCurrentUser();
        userID = user.getUid();

        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        textUsername = (TextView) findViewById(R.id.textUsername);
        textView = (TextView) findViewById(R.id.textView);
        imageUserProfilePic = (ImageView) findViewById(R.id.imageUserProfilePic);
        textUsername.setText(user.getEmail());
        buttonLogOut.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                } else {
                    // User is signed out

                }
                // ...
            }
        };

        //userInfo.getImageUrl(); //A String. To get profilepic URL
        //LoadImageFromWebOperations(userInfo.getImageUrl()); //A Drawable. To Load Image from URL
        //imageUserProfilePic.setBackground(LoadImageFromWebOperations(userInfo.getImageUrl())); //Set the backgroundimage of ImageView to URL 's image.
        //imageUserProfilePic.setBackground(LoadImageFromWebOperations(userInfo.mImageUrl));

        typingPlace1= (EditText) findViewById(R.id.typingPlace1);
        typingPlace2= (EditText) findViewById(R.id.typingPlace2);
        showPlace1= (TextView) findViewById(R.id.showPlace1);
        showPlace2= (TextView) findViewById(R.id.showPlace2);


    }

    private void showData(DataSnapshot dataSnapShot){
        for (DataSnapshot ds:dataSnapShot.getChildren()){
            UserInformation userInfo= new UserInformation();

            //userInfo.setName(ds.child(userID).getValue(UserInformation.class).getName());
            //userInfo.setEmail(ds.child(userID).getValue(UserInformation.class).getEmail());

            showPlace1.setText(ds.child(userID).getValue(UserInformation.class).getName());
            showPlace2.setText(ds.child(userID).getValue(UserInformation.class).getEmail());
        }
    }

    public static Drawable LoadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }



    @Override
    public void onClick(View view) {
        if (view==buttonLogOut){
            firebaseAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginPage.class));
        }
        if (view==buttonUpload){
            //UserInformation userInformation = new UserInformation(getName.getText().toString().trim(),
            //getEmail.getText().toString().trim(),"empty","empty");
            //mDatabaseRef.child(user.getUid()).setValue(userInformation);
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            firebaseAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


