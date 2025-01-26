package com.example.uniaxe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashBoard extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        // Initialize buttons
        Button btnCreate = findViewById(R.id.btnCreate);
        Button btnRead = findViewById(R.id.btnRead);
        Button btnUpdate = findViewById(R.id.btnUpdate);

        // Set click listeners for each button
        btnCreate.setOnClickListener(v -> openCreateActivity());
        btnRead.setOnClickListener(v -> openReadActivity());
        btnUpdate.setOnClickListener(v -> openUpdateActivity());
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

    // Method to open UpdateActivity
    private void openUpdateActivity() {
        Intent intent = new Intent(AdminDashBoard.this, UpdateActivity.class);
        startActivity(intent);
    }
}
