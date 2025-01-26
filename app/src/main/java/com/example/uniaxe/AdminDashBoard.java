package com.example.uniaxe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AdminDashBoard extends AppCompatActivity {

    private Button btnCreate, btnRead, btnUpdate;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_dash_board);

        // Initialize buttons
        btnCreate = findViewById(R.id.btnCreate);
        btnRead = findViewById(R.id.btnRead);
        btnUpdate = findViewById(R.id.btnUpdate);

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
