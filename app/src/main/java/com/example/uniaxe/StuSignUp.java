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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class StuSignUp extends AppCompatActivity {
    private EditText nameEditText, emailEditText, passEditText, batchEditText, idEditText;
    private String name, email, pass, batch, id;
    private Button submit;
    private TextView login;
    private ProgressBar progressBar;

    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    // Regular expressions for validation
    private static final String NAME_REGEX = "^[A-Za-z\\s_.]+$"; // Allows letters, spaces, underscores, and periods for student name
    private static final String EMAIL_REGEX = "^[a-z0-9._%+-]+@[a-z0-9.-]+\\.[a-z]{2,}$"; // Email validation
    private static final String PASSWORD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$"; // Password must have letters, numbers, and special characters
    private static final String ID_REGEX = "^[0-9]{16}$"; // ID must be numeric with exactly 16 digits
    private static final String BATCH_REGEX = "^[0-9]+$"; // Batch must be numeric

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_sign_up);

        // Initialize UI elements
        nameEditText = findViewById(R.id.name);
        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);
        batchEditText = findViewById(R.id.et_batch);
        idEditText = findViewById(R.id.et_id);
        submit = findViewById(R.id.submit);
        login = findViewById(R.id.login);
        progressBar = findViewById(R.id.progressBar);

        // Firebase setup
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        // Handle submit button click
        submit.setOnClickListener(v -> {
            name = nameEditText.getText().toString().trim();
            email = emailEditText.getText().toString().trim();
            pass = passEditText.getText().toString().trim();
            batch = batchEditText.getText().toString().trim();
            id = idEditText.getText().toString().trim();

            // Validate inputs
            if (!validateInputs()) return;

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            // Register the user
            auth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                progressBar.setVisibility(View.GONE);
                if (task.isSuccessful()) {
                    FirebaseUser user = auth.getCurrentUser();
                    sendEmailVerification(user); // Send email verification

                    // Store user info in Firestore (excluding the password)
                    assert user != null;
                    DocumentReference df = firestore.collection("Students").document(user.getUid());
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("email", email);
                    userInfo.put("name", name);
                    userInfo.put("batch", batch);
                    userInfo.put("id", id);
                    userInfo.put("uid", user.getUid());

                    df.set(userInfo).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getApplicationContext(), "Successfully Registered!", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), StuLoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(getApplicationContext(), "Error saving data: " + task1.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                        Toast.makeText(getApplicationContext(), "User Already Exists!", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        // Handle login button click
        login.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), StuLoginActivity.class));
            finish();
        });
    }

    // Validate inputs
    private boolean validateInputs() {
        if (name.isEmpty()) {
            nameEditText.setError("Name is required!");
            nameEditText.requestFocus();
            return false;
        } else if (!Pattern.matches(NAME_REGEX, name)) {
            nameEditText.setError("Name can only contain alphabets, spaces, underscores, and periods!");
            nameEditText.requestFocus();
            return false;
        } else if (email.isEmpty()) {
            emailEditText.setError("Email is required!");
            emailEditText.requestFocus();
            return false;
        } else if (!Pattern.matches(EMAIL_REGEX, email)) {
            emailEditText.setError("Enter a valid email format!");
            emailEditText.requestFocus();
            return false;
        } else if (pass.isEmpty()) {
            passEditText.setError("Password is required!");
            passEditText.requestFocus();
            return false;
        } else if (!Pattern.matches(PASSWORD_REGEX, pass)) {
            passEditText.setError("Password must include letters, digits, and special characters, with at least 8 characters.");
            passEditText.requestFocus();
            return false;
        } else if (batch.isEmpty()) {
            batchEditText.setError("Batch is required!");
            batchEditText.requestFocus();
            return false;
        } else if (!Pattern.matches(BATCH_REGEX, batch)) {
            batchEditText.setError("Batch must be numeric!");
            batchEditText.requestFocus();
            return false;
        } else if (id.isEmpty()) {
            idEditText.setError("ID is required!");
            idEditText.requestFocus();
            return false;
        } else if (!Pattern.matches(ID_REGEX, id)) {
            idEditText.setError("ID must be exactly 16 digits.");
            idEditText.requestFocus();
            return false;
        }
        return true;
    }

    // Send email verification
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
