package com.example.uniaxe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeachLoginActivity extends AppCompatActivity {
    private EditText emailEditText, passEditText;
    private Button submit;
    private TextView register;
    private ProgressBar progressBar;
    private FirebaseAuth auth;
    private FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teach_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        emailEditText = findViewById(R.id.email);
        passEditText = findViewById(R.id.pass);
        submit = findViewById(R.id.submit);
        register = findViewById(R.id.register);
        progressBar = findViewById(R.id.progressBar);
        auth = FirebaseAuth.getInstance();
        firestore = FirebaseFirestore.getInstance();

        submit.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String pass = passEditText.getText().toString().trim();

            if (email.isEmpty()) {
                emailEditText.setError("Empty!!");
                emailEditText.requestFocus();
                return;
            }
            if (pass.isEmpty()) {
                passEditText.setError("Empty!!");
                passEditText.requestFocus();
                return;
            }

            // Show progress bar
            progressBar.setVisibility(View.VISIBLE);

            auth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    progressBar.setVisibility(View.GONE);
                    FirebaseUser user = auth.getCurrentUser();
                    if (task.isSuccessful()) {
                        if (user != null && user.isEmailVerified()) {
                            // Check if the user is in the 'Teachers' collection
                            firestore.collection("Teachers").document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        if (documentSnapshot.exists()) {
                                            // User is a teacher, proceed to the teacher dashboard
                                            Toast.makeText(getApplicationContext(), "Login successful!", Toast.LENGTH_SHORT).show();
                                            startActivity(new Intent(getApplicationContext(), TeacherDashBoard.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                            finish();
                                        } else {
                                            // User is not a teacher
                                            Toast.makeText(getApplicationContext(), "You are not registered as a teacher!", Toast.LENGTH_SHORT).show();
                                            auth.signOut();
                                        }
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(getApplicationContext(), "Error checking user role.", Toast.LENGTH_SHORT).show();
                                        auth.signOut();
                                    });
                        } else {
                            // Email not verified
                            Toast.makeText(getApplicationContext(), "Please verify your email.", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    } else {
                        // Login failed
                        Toast.makeText(getApplicationContext(), "Login failed. Please check your credentials.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        });

        register.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), TeachSignUp.class));
        });
    }
}
