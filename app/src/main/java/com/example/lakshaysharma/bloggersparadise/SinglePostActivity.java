package com.example.lakshaysharma.bloggersparadise;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class SinglePostActivity extends AppCompatActivity {

    private ImageView image;
    private TextView title;
    private TextView description;
    private Button deleteBtn;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;

    private String POST_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_single_post);

        image = findViewById(R.id.singleImageview);
        title = findViewById(R.id.singleTitle);
        description = findViewById(R.id.singleDesc);
        deleteBtn = findViewById(R.id.deleteBtn);

        mDatabase = FirebaseDatabase.getInstance().getReference().child("BloggersParadise");
        mAuth = FirebaseAuth.getInstance();

        POST_KEY = getIntent().getStringExtra("PostID");

        // set delete button invisible for all users other than the author
        deleteBtn.setVisibility(View.INVISIBLE);

        // delete button action
        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mDatabase.child(POST_KEY).removeValue();
                Intent mainIntent = new Intent(SinglePostActivity.this, MainActivity.class);
                startActivity(mainIntent);
            }
        });

        // display the contents of the blog
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String postTitle = (String) dataSnapshot.child("title").getValue();
                String postDesc = (String) dataSnapshot.child("desc").getValue();
                String imageURL = (String) dataSnapshot.child("imageURL").getValue();
                String uid = (String) dataSnapshot.child("uid").getValue();
                title.setText(postTitle);
                description.setText(postDesc);
                Picasso.get().load(imageURL).into(image);

                // set the delete button visible if the user is the original author
                if(mAuth.getCurrentUser().getUid().equals(uid)){
                    deleteBtn.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("SinglePost onCanclled: ", databaseError.toString());
            }
        });
    }
}
