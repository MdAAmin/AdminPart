package com.example.uniaxe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.android.material.navigation.NavigationView;

public class TeacherDashBoard extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final String PREFS_NAME = "teacherPref";
    private static final String KEY_IMAGE_URI = "teacherProfileImageUri";

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
        setContentView(R.layout.activity_teacher_dash_board);

        drawerLayout = findViewById(R.id.teacherDrawerLayout);
        imageButton = findViewById(R.id.navToggleTechButton);  // Corrected ID here
        navigationView = findViewById(R.id.teacherNavigationView);

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

        // Fetch teacher data from Firestore
        fetchTeacherData();

        // Open navigation drawer when the toggle button is clicked
        imageButton.setOnClickListener(view -> drawerLayout.openDrawer(GravityCompat.START));

        // Handle navigation item clicks
        navigationView.setNavigationItemSelectedListener(item -> {
            int itemId = item.getItemId();

            if (itemId == R.id.navCreateTeacher) {
                showToastAndNavigate("Create Clicked", CreateGuideTeacherActivity.class);
            } else if (itemId == R.id.navReadGuide) {
                showToastAndNavigate("Read Clicked", ReadGuideTeacherActivity.class);
            } else if (itemId == R.id.navLogoutTeacher) {
                Toast.makeText(TeacherDashBoard.this, "Logging Out...", Toast.LENGTH_SHORT).show();
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(TeacherDashBoard.this, TeachLoginActivity.class));
                finish();
            } else {
                Toast.makeText(TeacherDashBoard.this, "Unknown Option", Toast.LENGTH_SHORT).show();
            }

            drawerLayout.closeDrawer(GravityCompat.START); // Close the drawer
            return true;
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
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

    private void fetchTeacherData() {
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch data from the "Teachers" collection
            db.collection("Teachers").document(userId)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult() != null) {
                            DocumentSnapshot document = task.getResult();
                            String name = document.getString("name"); // Ensure this is the correct field name for the teacher's name in Firestore
                            String email = document.getString("email");

                            // Update the header with teacher data
                            headerName.setText(name != null ? name : "Name not found");
                            headerEmail.setText(email != null ? email : "Email not found");
                        } else {
                            Toast.makeText(TeacherDashBoard.this, "Failed to fetch teacher data", Toast.LENGTH_SHORT).show();
                        }
                    });
        } else {
            Toast.makeText(this, "No user logged in", Toast.LENGTH_SHORT).show();
        }
    }

    private void showToastAndNavigate(String message, Class<?> targetActivity) {
        Toast.makeText(TeacherDashBoard.this, message, Toast.LENGTH_SHORT).show();
        startActivity(new Intent(TeacherDashBoard.this, targetActivity));
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
