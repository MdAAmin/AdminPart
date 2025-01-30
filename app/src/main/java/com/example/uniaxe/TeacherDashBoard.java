package com.example.uniaxe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class TeacherDashBoard extends AppCompatActivity {

    private Button btnCreateTeacher, btnReadGuide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_dash_board);



        // Initialize Buttons
        btnCreateTeacher = findViewById(R.id.btnCreateTeacher);
        btnReadGuide = findViewById(R.id.btnReadGuide);

        // Set Click Listeners
        btnCreateTeacher.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashBoard.this, CreateGuideTeacherActivity.class);
            startActivity(intent);
        });

        btnReadGuide.setOnClickListener(v -> {
            Intent intent = new Intent(TeacherDashBoard.this, ReadGuideTeacherActivity.class);
            startActivity(intent);
        });
    }
}
