package com.example.lakshaysharma.bloggersparadise;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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


public class PostActivity extends AppCompatActivity {

    private EditText textTitle;
    private EditText textDesc;
    private ImageButton imageButton;
    private Button postButton;

    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private FirebaseDatabase mFireBase;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private DatabaseReference mDatabseUsers;
    private Uri uri = null;
    private static final int GALLERY_REQ_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        // initialize all the widgets in the XML layout
        textTitle = findViewById(R.id.textTitle);
        textDesc = findViewById(R.id.textDesc);
        imageButton = findViewById(R.id.imageBtn);
        postButton = findViewById(R.id.postBtn);

        // initialize the Firebase references
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mDatabaseRef = mFireBase.getInstance().getReference("BloggersParadise");
        mAuth = FirebaseAuth.getInstance();

        // get the user id of the current user
        mUser = mAuth.getCurrentUser();
        mDatabseUsers = FirebaseDatabase.getInstance().getReference()
                .child("Users").child(mUser.getUid());

        // let user select an image from the device on Image Button click
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_REQ_CODE);
            }
        });

        // post the image to the database on POST button click
        postButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Toast.makeText(PostActivity.this, "Posting Content....", Toast.LENGTH_SHORT).show();

                // get the Title and the Description that the user entered
                final String title = textTitle.getText().toString().trim();
                final String description = textDesc.getText().toString().trim();

                // check whether the fields were empty
                if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(description)){

                    // get/create a path to store images on the database in "/post_images" child
                    final StorageReference filePath = mStorageRef.child("/post_images")
                            .child(uri.getLastPathSegment());
                    // put the image that the user selected in the filePath
                    filePath.putFile(uri);
                    final Task<Uri> imageTask = filePath.putFile(uri).continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if(!task.isSuccessful()){
                                throw task.getException();
                            }

                            //get image URL
                            return filePath.getDownloadUrl();
                        }

                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {

                            Toast.makeText(PostActivity.this, "Upload Succesful", Toast.LENGTH_SHORT).show();
                            Uri downloadURL = task.getResult();

                            // add content to the database reference
                            addContent(title, description, downloadURL);
                        }
                    });
                }
            }
        });

    }

    private void addContent(final String title,final String description,final Uri downloadURL){

        final DatabaseReference newPost = mDatabaseRef.push();
        mDatabseUsers.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                newPost.child("title").setValue(title);
                newPost.child("desc").setValue(description);
                newPost.child("imageURL").setValue(downloadURL.toString());
                newPost.child("uid").setValue(dataSnapshot.child("name").getValue())
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isComplete()){
                                    Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                    startActivity(intent);
                                }
                            }
                        });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d("PostActivity onCancel: ", databaseError.toString());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_REQ_CODE && requestCode == RESULT_OK){
            uri = data.getData();
            imageButton.setImageURI(uri);
        }
    }
}
