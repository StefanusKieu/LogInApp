package com.example.user.loginapp;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.InputStream;
import java.net.URL;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Button buttonLogOut,buttonUpload,buttonChangePic,buttonRemovePic;
    private TextView textUsername,sideUsername;
    private TextView textView;
    private ImageView imageUserProfilePic,sideProfilePic;
    private DatabaseReference myRef;
    private FirebaseDatabase mFirebaseDatabase;
    private EditText typingPlace1;
    private String userID;
    private int i=1;
    private int j=1;
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth=FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("profilepics");
        if (mAuth.getCurrentUser()==null){
            finish();
            startActivity(new Intent(this, LoginPage.class));
        }
        FirebaseUser user = mAuth.getCurrentUser();
        userID = user.getUid();

        buttonLogOut = (Button) findViewById(R.id.buttonLogOut);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonChangePic = (Button) findViewById(R.id.buttonChangePic);
        buttonRemovePic = (Button) findViewById(R.id.buttonRemovePic);
        textUsername = (TextView) findViewById(R.id.textUsername);
        textView = (TextView) findViewById(R.id.textView);
        imageUserProfilePic = (ImageView) findViewById(R.id.imageUserProfilePic);
        textUsername.setText(user.getEmail());
        buttonLogOut.setOnClickListener(this);
        buttonUpload.setOnClickListener(this);
        buttonChangePic.setOnClickListener(this);
        buttonRemovePic.setOnClickListener(this);

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
                    Toast.makeText(MainActivity.this,"Username Updated",Toast.LENGTH_SHORT).show();
                }

            }
        });

        //userInfo.getImageUrl(); //A String. To get profilepic URL
        //LoadImageFromWebOperations(userInfo.getImageUrl()); //A Drawable. To Load Image from URL
        //imageUserProfilePic.setBackground(LoadImageFromWebOperations(userInfo.getImageUrl())); //Set the backgroundimage of ImageView to URL 's image.
        //imageUserProfilePic.setBackground(LoadImageFromWebOperations(userInfo.mImageUrl));

        typingPlace1= (EditText) findViewById(R.id.typingPlace1);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView =  navigationView.getHeaderView(0);
        sideUsername = (TextView)hView.findViewById(R.id.sideUsername);
        TextView sideEmail = (TextView)hView.findViewById(R.id.sideEmail);
        sideEmail.setText(user.getEmail());




    }

    private void showData(DataSnapshot dataSnapshot){
            UserInformation uInfo = new UserInformation();

            uInfo.setName(dataSnapshot.child(userID).getValue(UserInformation.class).getName()); //set the name
            uInfo.setEmail(dataSnapshot.child(userID).getValue(UserInformation.class).getEmail()); //set the email
            uInfo.setImageUrl(dataSnapshot.child(userID).getValue(UserInformation.class).getImageUrl());

            String text=uInfo.getName();
            final String ip=uInfo.getImageUrl();

            typingPlace1.setText(text);
            sideUsername.setText(text);
            new DownLoadImageTask(imageUserProfilePic).execute(ip);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            View hView =  navigationView.getHeaderView(0);
            ImageView sideProfilePic = (ImageView) hView.findViewById(R.id.sideProfilePic);
            new DownLoadImageTask(sideProfilePic).execute(ip);
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
        if (view==buttonRemovePic){
            String newValue = "https://firebasestorage.googleapis.com/v0/b/easylogin-15814.appspot.com/o/profilepics%2F1531794669031.png?alt=media&token=24b1e22e-6a45-4274-8644-b4222c4c5c64";
            FirebaseUser user = mAuth.getCurrentUser();
            userID = user.getUid();
            //Updating picture like a boss!
            myRef.child(userID).child("imageUrl").setValue(newValue);
            imageUserProfilePic.setBackgroundResource(R.drawable.profilepic);
        }
        if (view==buttonChangePic) {
            i++;
            if (i % 2 == 0) {
                openFileChooser();

                buttonChangePic.setBackgroundResource(android.R.color.holo_red_light);
                buttonChangePic.setText("Save change");
            } else {
                StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
                        +"."+getFIleExtension(mImageUri));
                fileReference.putFile(mImageUri)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                String newValue = taskSnapshot.getDownloadUrl().toString().trim();
                                FirebaseUser user = mAuth.getCurrentUser();
                                userID = user.getUid();
                                //Updating picture like a boss!
                                myRef.child(userID).child("imageUrl").setValue(newValue);

                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

                buttonChangePic.setText("Change Profile Picture");
                buttonChangePic.setBackgroundResource(android.R.drawable.btn_default);
                Toast.makeText(MainActivity.this,"Profile Picture Updated",Toast.LENGTH_SHORT).show();

            }

        }

    }

    private void openFileChooser(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==PICK_IMAGE_REQUEST&&resultCode==RESULT_OK
                &&data != null && data.getData()!=null){
            mImageUri = data.getData();

            Picasso.get().load(mImageUri).into(imageUserProfilePic);
            //imageProfile.setImageURI(mImageUri);
            imageUserProfilePic.setBackground(null);
        }
    }
    private String getFIleExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
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


