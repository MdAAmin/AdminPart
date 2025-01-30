package com.example.uniaxe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class StudentDashBoard extends AppCompatActivity {

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dash_board);

        // Initialize buttons
        Button btnCreate = findViewById(R.id.btnCreate);
        Button btnRead = findViewById(R.id.btnRead);
        Button btnSylabus = findViewById(R.id.btnSylabus);
        Button btnCGPA = findViewById(R.id.btnCGPA);

        // Set click listeners for each button
        btnCreate.setOnClickListener(v -> openCreateActivity());
        btnRead.setOnClickListener(v -> openReadActivity());
        btnSylabus.setOnClickListener(v -> openSyllabusActivity());
        btnCGPA.setOnClickListener(v -> openCGPAActivity());
    }

    // Method to open CreateActivity
    private void openCreateActivity() {
        Intent intent = new Intent(StudentDashBoard.this, CreateActivity.class);
        startActivity(intent);
    }

    // Method to open ReadActivity
    private void openReadActivity() {
        Intent intent = new Intent(StudentDashBoard.this, StuReadActivity.class);
        startActivity(intent);
    }

    // Method to open SylabusActivity
    private void openSyllabusActivity() {
        Intent intent = new Intent(StudentDashBoard.this, StuSyllabusReadActivity.class);
        startActivity(intent);
    }

    // Method to open CGPAActivity
    private void openCGPAActivity() {
        Intent intent = new Intent(StudentDashBoard.this, CGPACalculatorActivity.class);
        startActivity(intent);
    }
}
