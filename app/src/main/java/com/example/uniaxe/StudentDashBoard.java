package com.example.uniaxe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.Nullable;
import androidx.core.view.GravityCompat;

public class StudentDashBoard extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREFS_NAME = "studentPref";
    private static final String KEY_IMAGE_URI = "studentProfileImageUri";

    DrawerLayout drawerLayout;
    ImageButton imageButton;
    NavigationView navigationView;
    TextView headerName, headerEmail;
    ImageView headerImage;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth auth = FirebaseAuth.getInstance();
    SharedPreferences sharedPreferences;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dash_board);

        drawerLayout = findViewById(R.id.studentDrawerLayout);
        imageButton = findViewById(R.id.ButtomStuToggle); // Corrected ID here
        navigationView = findViewById(R.id.studentNavigationView);

        // Set up the header layout views
        View headerView = navigationView.getHeaderView(0);
        headerName = headerView.findViewById(R.id.header1);
        headerEmail = headerView.findViewById(R.id.header2);
        headerImage = headerView.findViewById(R.id.profileView);

        // Initialize SharedPreferences
        sharedPreferences = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);

        // Load saved image from SharedPreferences
        loadProfileImage();

        // Set up image click listener to open gallery
        headerImage.setOnClickListener(v -> openGallery());

        // Fetch student data from Firestore
        fetchStudentData();

        // Open navigation drawer when the toggle button is clicked
        imageButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navCre) {
                showToastAndNavigate("Create Clicked", CreateActivity.class);
            } else if (itemId == R.id.navRe) {
                showToastAndNavigate("Read Clicked", StuReadActivity.class);
            } else if (itemId == R.id.navCGPA) {
                showToastAndNavigate("CGPA Clicked", CGPACalculatorActivity.class);
            } else if (itemId == R.id.navSyl) {
                showToastAndNavigate("Syllabus Clicked", StuSyllabusReadActivity.class);
            } else if (itemId == R.id.navLogout) {
                Toast.makeText(StudentDashBoard.this, "Logging Out...", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(StudentDashBoard.this, StuLoginActivity.class));
                finish();
            } else {
                Toast.makeText(StudentDashBoard.this, "Unknown Option", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer
            return true;
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri selectedImageUri = data.getData();
            if (selectedImageUri != null) {
                // Display the selected image in the header ImageView
                headerImage.setImageURI(selectedImageUri);
                // Save image URI to SharedPreferences
                saveProfileImage(selectedImageUri.toString());
            }
        }
    }

    private void fetchStudentData() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch data from the "Students" collection
            db.collection("Students").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            String name = document.getString("name"); // Ensure this is the correct field name for the student's name in Firestore
                            String email = document.getString("email");

                            // Update the header with student data
                            headerName.setText(name != null ? name : "Name not found");
                            headerEmail.setText(email != null ? email : "Email not found");
                        } else {
                            Toast.makeText(StudentDashBoard.this, "Failed to fetch student data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToastAndNavigate(String message, Class<?> targetActivity) {
        Toast.makeText(StudentDashBoard.this, message, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(StudentDashBoard.this, targetActivity));
    }

    private void saveProfileImage(String imageUri) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_IMAGE_URI, imageUri);
        editor.apply();
    }

    private void loadProfileImage() {
        String savedImageUri = sharedPreferences.getString(KEY_IMAGE_URI, null);
        if (savedImageUri != null) {
            headerImage.setImageURI(Uri.parse(savedImageUri));
        }
    }
}
