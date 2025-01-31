package com.example.uniaxe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashBoard extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        // Initialize ImageViews (to make them clickable)
        ImageView btnCreate = findViewById(R.id.btnCreate);
        ImageView btnRead = findViewById(R.id.btnRead);

        // Set click listeners for each ImageView
        btnCreate.setOnClickListener(v -> openCreateActivity());
        btnRead.setOnClickListener(v -> openReadActivity());
    }

    // Method to open CreateActivity
    private void openCreateActivity() {
        Intent intent = new Intent(AdminDashBoard.this, CreateActivity.class);
        startActivity(intent);
    }

    // Method to open ReadActivity
    private void openReadActivity() {
        Intent intent = new Intent(AdminDashBoard.this, ReadActivity.class);
        startActivity(intent);
    }


}
