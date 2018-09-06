package com.example.lakshaysharma.bloggersparadise;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private static Button registerBtn;
    private static TextView loginTxtView;
    private static EditText usernameField, passwordField, emailField;

    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        registerBtn = findViewById(R.id.registerBtn);
        loginTxtView = findViewById(R.id.loginTxtView);
        usernameField = findViewById(R.id.usernameField);
        passwordField = findViewById(R.id.passwordField);
        emailField = findViewById(R.id.emailField);

        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users");

        // if the user is already registered, start login activity
        loginTxtView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent loginIntent = new Intent(RegisterActivity.this, LoginActivity.class);
                loginIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(loginIntent);
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameField.getText().toString().trim();
                String password = passwordField.getText().toString().trim();
                String email = emailField.getText().toString().trim();

                if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password) && !TextUtils.isEmpty(email)){
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            String userID = mAuth.getCurrentUser().getUid();
                            DatabaseReference currentDbRef = mDatabase.child(userID);
                            currentDbRef.child("Username").setValue(username);
                            currentDbRef.child("Image").setValue("Default");
                            Toast.makeText(RegisterActivity.this, "Registered Successfully", Toast.LENGTH_LONG).show();
                            Intent profileIntent = new Intent(RegisterActivity.this, MainActivity.class);
                            profileIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                            startActivity(profileIntent);
                        }
                    });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Please Fill All Fields", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
