package com.example.uniaxe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView studentImage = findViewById(R.id.studentImage);
        ImageView teacherImage = findViewById(R.id.teacherImage);
        ImageView adminImage = findViewById(R.id.adminImage);

        // Make the student image clickable
        studentImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StuSignUp.class);
                startActivity(intent);
            }
        });

        // Make the teacher image clickable
        teacherImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TeachSignUp.class);
                startActivity(intent);
            }
        });

        // Make the admin image clickable
        adminImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminSignUp.class);
                startActivity(intent);
            }
        });
    }
}
