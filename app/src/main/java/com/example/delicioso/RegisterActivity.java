package com.example.delicioso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static android.content.ContentValues.TAG;
import static com.example.delicioso.R.*;

public class RegisterActivity extends AppCompatActivity {

    EditText userName, userMail,userPassword;
    FirebaseAuth firebaseAuth;
    ProgressBar progressBar;
    FirebaseFirestore firebaseFirestore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_register);

        userName = findViewById(R.id.name);
        userMail = findViewById(R.id.email);
        userPassword = findViewById(R.id.PasswordSignIn);

        firebaseAuth = FirebaseAuth.getInstance();
        progressBar = findViewById(R.id.progressBar);
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void registerUser(View view) {
        String name = userName.getText().toString();
        String email = userMail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(name)) {
            userName.setError("Name is Required.");
            return;
        }

        if (TextUtils.isEmpty(email)) {
            userMail.setError("Email is Required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            userPassword.setError("Password is Required.");
            return;
        } else if(password.length() < 6) {
            userPassword.setError("Password is too short.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if(task.isSuccessful()) {

                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    firebaseUser.sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(RegisterActivity.this, "Verification Email has been sent.", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error! Email not sent. "+ e.getMessage());
                        }
                    });

                    //Toast.makeText(RegisterPage.this, "User created", Toast.LENGTH_SHORT).show();

                    userID = firebaseAuth.getCurrentUser().getUid();
                    DocumentReference documentReference = firebaseFirestore.collection("users").document(userID);

                    Map<String, Object> user = new HashMap<>();
                    user.put("name",name);
                    user.put("email",email);

                    documentReference.set(user).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.w(TAG, "Error adding document", e);
                        }
                    });

                    startActivity(new Intent( RegisterActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(RegisterActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}