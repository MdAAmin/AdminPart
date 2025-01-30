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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class AdminLogin extends AppCompatActivity {
    private EditText emailEditText, passEditText;
    private String email, pass;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_login);

        emailEditText = findViewById(R.id.adminEmail);
        passEditText = findViewById(R.id.adminPass);
        Button submit = findViewById(R.id.adminLogin);
        TextView register = findViewById(R.id.adminSignUp);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        submit.setOnClickListener(v -> {
            email = emailEditText.getText().toString().trim();
            pass = passEditText.getText().toString().trim();

            // Validation checks for empty fields
            if (email.isEmpty()) {
                emailEditText.setError("Empty!!");
                emailEditText.requestFocus();
            } else if (pass.isEmpty()) {
                passEditText.setError("Empty!!");
                passEditText.requestFocus();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    if (task.isSuccessful()) {
                        FirebaseUser user = auth.getCurrentUser();
                        if (user != null && user.isEmailVerified()) {
                            navigateToAdminDashboard(user); // Navigate to admin dashboard
                        } else {
                            // Email not verified
                            Toast.makeText(getApplicationContext(), "Please verify your email.", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    } else {
                        // Login failed
                        Toast.makeText(getApplicationContext(), "Login Failed!!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        // Register button click listener
        register.setOnClickListener(v -> {
            finish();
            startActivity(new Intent(getApplicationContext(), AdminSignUp.class)); // Navigate to admin sign-up
        });
    }

    // Navigates to admin dashboard if the user is verified as an admin
    private void navigateToAdminDashboard(FirebaseUser user) {
        DocumentReference docRef = firestore.collection("Admins").document(user.getUid());
        docRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    // Admin login successful
                    Toast.makeText(getApplicationContext(), "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(getApplicationContext(), AdminDashBoard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    finish();
                } else {
                    // User not found in Admins collection
                    Toast.makeText(getApplicationContext(), "User not found in Admins collection!", Toast.LENGTH_SHORT).show();
                    auth.signOut();
                }
            } else {
                // Error fetching user data
                Toast.makeText(getApplicationContext(), "Failed to fetch user data!", Toast.LENGTH_SHORT).show();
                auth.signOut();
            }
        });
    }
}
