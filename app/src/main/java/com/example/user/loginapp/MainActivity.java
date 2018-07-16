package com.example.user.loginapp;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button buttonLogOut,buttonUpload;
    private TextView textUsername;
    private TextView textView;
    private ImageView imageUserProfilePic;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private EditText typingPlace1;
    private String userID;
    private int i=1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();

        if (mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginPage.class));
        }
        FirebaseUser user = mAuth.getCurrentUser();
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

        // Read from the database
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Object value = dataSnapshot.getValue();
                showData(dataSnapshot);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
               }
        });

        buttonUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                i++;
                if (i%2==0){
                typingPlace1.setEnabled(true);
                //typingPlace1.setBackgroundResource(R.drawable.custom_edit_text_bg);
                typingPlace1.setBackgroundResource(android.R.color.white);
                buttonUpload.setText("Save change");
                }
                else{
                    typingPlace1.setEnabled(false);
                    typingPlace1.setBackgroundResource(android.R.color.transparent);

                    buttonUpload.setText("CHANGE USERNAME");

                    String newValue = typingPlace1.getText().toString();
                    //String newAttribute="Age";
                    FirebaseUser user = mAuth.getCurrentUser();
                    userID = user.getUid();

                    //Updating like a boss!
                    myRef.child(userID).child("name").setValue(newValue);
                }

            }
        });

        //userInfo.getImageUrl(); //A String. To get profilepic URL
        //LoadImageFromWebOperations(userInfo.getImageUrl()); //A Drawable. To Load Image from URL
        //imageUserProfilePic.setBackground(LoadImageFromWebOperations(userInfo.getImageUrl())); //Set the backgroundimage of ImageView to URL 's image.
        //imageUserProfilePic.setBackground(LoadImageFromWebOperations(userInfo.mImageUrl));

        typingPlace1= (EditText) findViewById(R.id.typingPlace1);






    }

    private void showData(DataSnapshot dataSnapshot){
            UserInformation uInfo = new UserInformation();

            uInfo.setName(dataSnapshot.child(userID).getValue(UserInformation.class).getName()); //set the name
            uInfo.setEmail(dataSnapshot.child(userID).getValue(UserInformation.class).getEmail()); //set the email
            uInfo.setImageUrl(dataSnapshot.child(userID).getValue(UserInformation.class).getImageUrl());

            String text=uInfo.getName();
            final String ip=uInfo.getImageUrl();

            typingPlace1.setText(text);
            new DownLoadImageTask(imageUserProfilePic).execute(ip);


    }

    private class DownLoadImageTask extends AsyncTask<String,Void,Bitmap> {
        ImageView imageView;

        public DownLoadImageTask(ImageView imageView){
            this.imageView = imageView;
        }

        /*
            doInBackground(Params... params)
                Override this method to perform a computation on a background thread.
         */
        protected Bitmap doInBackground(String...urls){
            String urlOfImage = urls[0];
            Bitmap logo = null;
            try{
                InputStream is = new URL(urlOfImage).openStream();
                /*
                    decodeStream(InputStream is)
                        Decode an input stream into a bitmap.
                 */
                logo = BitmapFactory.decodeStream(is);
            }catch(Exception e){ // Catch the download exception
                e.printStackTrace();
            }
            return logo;
        }

        /*
            onPostExecute(Result result)
                Runs on the UI thread after doInBackground(Params...).
         */
        protected void onPostExecute(Bitmap result){
            imageView.setImageBitmap(result);
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
            mAuth.signOut();
            finish();
            startActivity(new Intent(this, LoginPage.class));
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}


