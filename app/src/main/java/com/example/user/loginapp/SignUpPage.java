package com.example.user.loginapp;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class SignUpPage extends AppCompatActivity implements View.OnClickListener {

    private Button buttonContinue;
    private EditText editEmailAddress;
    private EditText editPassword;
    private EditText editConfirmPassword;
    private EditText editUserName;
    private ProgressDialog progressDialog;
    private TextView textUploadProfile;
    private CircleImageView imageProfile;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri mImageUri;
    private StorageReference mStorageRef;
    private boolean gotPicture=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_page);

        //String haha = "https://firebasestorage.googleapis.com/v0/b/easylogin-15814.appspot.com/o/profilepics%2F1531794669031.png?alt=media&token=24b1e22e-6a45-4274-8644-b4222c4c5c64";
        //mImageUri = Uri.parse(haha);

        progressDialog = new ProgressDialog(this);
        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser()!=null)
        {
            finish();
            startActivity(new Intent(getApplicationContext(),LoginPage.class));
        }
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mStorageRef = FirebaseStorage.getInstance().getReference("profilepics");

        buttonContinue= (Button) findViewById(R.id.buttonContinue);
        editEmailAddress= (EditText) findViewById(R.id.editEmailAddress);
        editPassword= (EditText) findViewById(R.id.editPassword);
        editConfirmPassword= (EditText) findViewById(R.id.editConfirmPassword);
        editUserName= (EditText) findViewById(R.id.editUserName);
        textUploadProfile = (TextView) findViewById(R.id.textUploadProfile);
        imageProfile = (CircleImageView) findViewById(R.id.imageProfile);

        buttonContinue.setOnClickListener(this);

        textUploadProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileChooser();
            }
        });
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


            Picasso.get().load(mImageUri).noFade().into(imageProfile);
            //imageProfile.setImageURI(mImageUri);
            imageProfile.setBackground(null);
            gotPicture = true;
        }
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

        if(gotPicture==false){
            Toast.makeText(this,"Please choose a profile picture. You can remove it later if you wish.",Toast.LENGTH_SHORT).show();
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
                            startActivity(new Intent(SignUpPage.this, LoginPage.class));
                            //finish();
                            //startActivity(new Intent(getApplicationContext(),LoginPage.class));
                        }else{
                            Toast.makeText(SignUpPage.this,"Could not register.",Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void saveUserInformation(){
        //String name = editUserName.getText().toString().trim();
       // String email = editEmailAddress.getText().toString().trim();
        //String password = editPassword.getText().toString().trim();

        StorageReference fileReference = mStorageRef.child(System.currentTimeMillis()
        +"."+getFIleExtension(mImageUri));
        fileReference.putFile(mImageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        UserInformation userInformation = new UserInformation(editUserName.getText().toString().trim(),
                                editEmailAddress.getText().toString().trim(),
                                editPassword.getText().toString().trim(),
                                taskSnapshot.getDownloadUrl().toString().trim());

                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        databaseReference.child(user.getUid()).setValue(userInformation);


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(SignUpPage.this, e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });



    }

    @Override
    public void onClick(View view){
        if (view==buttonContinue){
            registerUser();
        }
    }

    private String getFIleExtension(Uri uri){
        ContentResolver cR= getContentResolver();
        MimeTypeMap mime= MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

}
