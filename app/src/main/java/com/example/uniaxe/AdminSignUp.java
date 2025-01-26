package com.example.uniaxe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class AdminSignUp extends AppCompatActivity {
    private EditText adminNameEditText, emailEditText, passEditText, confirmPassEditText, idNumEditText;
    private String adminName, email, pass, confirmPass, idNum;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_sign_up);

        adminNameEditText = findViewById(R.id.adminName);
        emailEditText = findViewById(R.id.adminEmail);
        passEditText = findViewById(R.id.adminPass);
        confirmPassEditText = findViewById(R.id.adminConfirmPass);
        idNumEditText = findViewById(R.id.adminIdNum);
        Button submit = findViewById(R.id.adminSubmit);
        TextView login = findViewById(R.id.adminAlreadyRegistered);
        progressBar = findViewById(R.id.adminProgressBar);

        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        submit.setOnClickListener(v -> {
            adminName = adminNameEditText.getText().toString().trim();
            email = emailEditText.getText().toString().trim();
            pass = passEditText.getText().toString().trim();
            confirmPass = confirmPassEditText.getText().toString().trim();
            idNum = idNumEditText.getText().toString().trim();

            if (adminName.isEmpty()) {
                adminNameEditText.setError("Empty!!");
                adminNameEditText.requestFocus();
            } else if (email.isEmpty()) {
                emailEditText.setError("Empty!!");
                emailEditText.requestFocus();
            } else if (pass.isEmpty()) {
                passEditText.setError("Empty!!");
                passEditText.requestFocus();
            } else if (confirmPass.isEmpty()) {
                confirmPassEditText.setError("Empty!!");
                confirmPassEditText.requestFocus();
            } else if (!pass.equals(confirmPass)) {
                confirmPassEditText.setError("Passwords do not match!!");
                confirmPassEditText.requestFocus();
            } else if (idNum.isEmpty()) {
                idNumEditText.setError("Empty!!");
                idNumEditText.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        sendEmailVerification(user); // Send email verification
                        assert user != null;
                        DocumentReference df = firestore.collection("Admins").document(user.getUid());
                        Map<String, String> adminInfo = new HashMap<>();
                        adminInfo.put("email", email);
                        adminInfo.put("adminName", adminName);
                        adminInfo.put("idNum", idNum);
                        adminInfo.put("uid", user.getUid());

                        df.set(adminInfo);
                        Toast.makeText(getApplicationContext(), "Successfully Registered!!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), AdminLogin.class));
                        finish();
                    } else {
                        if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                            Toast.makeText(getApplicationContext(), "User Already Exist!!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error: " + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        login.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), AdminLogin.class));
            finish();
        });
    }

    private void sendEmailVerification(FirebaseUser user) {
        if (user != null && !user.isEmailVerified()) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Error sending verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
}
