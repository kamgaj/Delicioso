package com.example.delicioso;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginActivity extends AppCompatActivity {

    FirebaseAuth firebaseAuth;
    EditText userMail, userPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        userMail = findViewById(R.id.MailSignIn);
        userPassword = findViewById(R.id.PasswordSignIn);

        firebaseAuth = FirebaseAuth.getInstance();

        if(firebaseAuth.getCurrentUser() != null ) {
            startActivity(new Intent( LoginActivity.this, MainActivity.class));
            finish();
        }
    }

    public void goToRegisterPageButton(View view) {
        startActivity(new Intent( this, RegisterActivity.class));
    }

    public void signIn(View view) {
        String mail = userMail.getText().toString();
        String password = userPassword.getText().toString();

        if (TextUtils.isEmpty(mail)) {
            userMail.setError("Email is Required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            userMail.setError("Password is Required.");
            return;
        } else if(password.length() < 6) {
            userMail.setError("Password is too short.");
            return;
        }

        firebaseAuth.signInWithEmailAndPassword(mail,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(LoginActivity.this, "Successfully signed in!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent( LoginActivity.this, MainActivity.class));
                } else {
                    Toast.makeText(LoginActivity.this, "Error!" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void forgotPassword(View view) {

        final EditText resetPassword = new EditText(view.getContext());
        final AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(view.getContext());

        passwordResetDialog.setTitle("Reset Password?");
        passwordResetDialog.setMessage("Enter Email to receive reset link.");
        passwordResetDialog.setView(resetPassword);

        passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                String email = resetPassword.getText().toString();
                firebaseAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(LoginActivity.this, "Reset link sent to your email address.", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(LoginActivity.this, "Error! Failed to send reset link." + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
            }
        });

        passwordResetDialog.create().show();
    }
}