package com.example.uniaxe;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class CGPACalculatorActivity extends AppCompatActivity {

    private EditText gradesInput, creditsInput;
    private Button calculateButton;
    private TextView resultTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cgpacalculator);

        gradesInput = findViewById(R.id.gradesInput);
        creditsInput = findViewById(R.id.creditsInput);
        calculateButton = findViewById(R.id.calculateButton);
        resultTextView = findViewById(R.id.resultTextView);

        calculateButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("DefaultLocale")
            @Override
            public void onClick(View v) {
                String gradesText = gradesInput.getText().toString().trim();
                String creditsText = creditsInput.getText().toString().trim();

                if (!gradesText.isEmpty() && !creditsText.isEmpty()) {
                    try {
                        double cgpa = calculateCGPA(gradesText, creditsText);
                        resultTextView.setText(String.format("Your CGPA: %.2f", cgpa));
                        resultTextView.setVisibility(View.VISIBLE); // Make result visible
                    } catch (Exception e) {
                        Toast.makeText(CGPACalculatorActivity.this, "Invalid input. Ensure grades and credits are properly formatted.", Toast.LENGTH_SHORT).show();
                        resultTextView.setVisibility(View.GONE); // Hide result if error occurs
                    }
                } else {
                    Toast.makeText(CGPACalculatorActivity.this, "Please enter both grades and credits.", Toast.LENGTH_SHORT).show();
                    resultTextView.setVisibility(View.GONE); // Hide result if inputs are empty
                }
            }
        });
    }

    private double calculateCGPA(String gradesText, String creditsText) {
        String[] gradesArray = gradesText.split(",");
        String[] creditsArray = creditsText.split(",");

        // Ensure grades and credits arrays have the same length
        if (gradesArray.length != creditsArray.length) {
            throw new IllegalArgumentException("Grades and credits count must match.");
        }

        double totalWeightedGrades = 0;
        double totalCredits = 0;

        for (int i = 0; i < gradesArray.length; i++) {
            try {
                // Parse grades and credits, allowing for floating-point numbers
                double grade = Double.parseDouble(gradesArray[i].trim());
                double credit = Double.parseDouble(creditsArray[i].trim());

                totalWeightedGrades += grade * credit;
                totalCredits += credit;
            } catch (NumberFormatException e) {
                throw new IllegalArgumentException("Invalid grade or credit value. Please enter valid numbers.");
            }
        }

        // Prevent division by zero if totalCredits is 0
        if (totalCredits == 0) {
            throw new ArithmeticException("Total credits cannot be zero.");
        }

        return totalWeightedGrades / totalCredits;
    }
}
