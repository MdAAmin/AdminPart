package com.example.uniaxe;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button goToStuSignupButton = findViewById(R.id.goToStuSignupButton);
        Button goToAdminLoginButton = findViewById(R.id.goToAdminLoginButton);
        Button goToTeachSignupButton = findViewById(R.id.goToTeachSignupButton);

        goToStuSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, StuSignUp.class);
                startActivity(intent);
            }
        });

        goToAdminLoginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, AdminDashBoard.class);
                startActivity(intent);
            }
        });

        goToTeachSignupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, TeachSignUp.class);
                startActivity(intent);
            }
        });
    }
}


