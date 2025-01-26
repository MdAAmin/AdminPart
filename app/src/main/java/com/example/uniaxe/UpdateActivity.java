package com.example.uniaxe;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.*;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    private static final int PDF_REQUEST_CODE = 2;

    private EditText etCourseId, etCourseName, etYearName;
    private Spinner spinnerSemester;
    private RadioGroup radioGroupExamType, radioGroupQuestionNote;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private String key, courseId, courseName, semester, examType, pdfType, year, pdfUrl;
    private Uri pdfUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize views
        etCourseId = findViewById(R.id.et_course_id);
        etCourseName = findViewById(R.id.et_course_name);
        etYearName = findViewById(R.id.et_year_name);
        spinnerSemester = findViewById(R.id.spinner_semester);
        radioGroupExamType = findViewById(R.id.radioGroup);
        radioGroupQuestionNote = findViewById(R.id.radioGroup_question_note);
        ImageView pdfIn = findViewById(R.id.pdf_in);
        progressBar = findViewById(R.id.progressBar);
        Button btnUpdateFile = findViewById(R.id.btnUpdateFile);

        // Get Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("PDFs");

        // Fetch data from intent
        key = getIntent().getStringExtra("key");

        // Validate if the key is missing
        if (key == null || key.isEmpty()) {
            Toast.makeText(this, "Wrong entry! Key is missing.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        courseId = getIntent().getStringExtra("courseId");
        courseName = getIntent().getStringExtra("courseName");
        semester = getIntent().getStringExtra("semester");
        examType = getIntent().getStringExtra("examType");
        pdfType = getIntent().getStringExtra("pdfType");
        year = getIntent().getStringExtra("year");
        pdfUrl = getIntent().getStringExtra("pdfUrl");

        // Set existing data in fields
        etCourseId.setText(courseId);
        etCourseName.setText(courseName);
        etYearName.setText(year);

        // Setup Spinner
        setupSpinner(semester);

        // Set RadioGroup values
        if ("Mid".equalsIgnoreCase(examType)) {
            radioGroupExamType.check(R.id.radio_mid);
        } else {
            radioGroupExamType.check(R.id.radio_final);
        }

        if ("Question".equalsIgnoreCase(pdfType)) {
            radioGroupQuestionNote.check(R.id.radio_question);
        } else {
            radioGroupQuestionNote.check(R.id.radio_note);
        }

        // Handle PDF selection
        pdfIn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("application/pdf");
                startActivityForResult(intent, PDF_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PDF_REQUEST_CODE);
            }
        });

        // Update button click listener
        btnUpdateFile.setOnClickListener(v -> {
            courseId = etCourseId.getText().toString().trim();
            courseName = etCourseName.getText().toString().trim();
            year = etYearName.getText().toString().trim();
            semester = spinnerSemester.getSelectedItem().toString();
            examType = radioGroupExamType.getCheckedRadioButtonId() == R.id.radio_mid ? "Mid" : "Final";
            pdfType = radioGroupQuestionNote.getCheckedRadioButtonId() == R.id.radio_question ? "Question" : "Note";

            if (courseId.isEmpty() || courseName.isEmpty() || year.isEmpty()) {
                Toast.makeText(this, "All fields are required!", Toast.LENGTH_SHORT).show();
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            if (pdfUri == null) {
                // Update without new PDF
                updateData(pdfUrl);
            } else {
                // Update with new PDF (Handle upload logic here)
                String newPdfUrl = "your_pdf_upload_url_here"; // Replace with actual uploaded URL
                updateData(newPdfUrl);
            }
        });
    }

    private void setupSpinner(String fetchedSemester) {
        String[] semesters = {"Select a Semester", "Fall", "Summer", "Winter"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(adapter);

        // Pre-select the fetched value
        if (fetchedSemester != null) {
            int index = 0;
            for (int i = 0; i < semesters.length; i++) {
                if (semesters[i].equalsIgnoreCase(fetchedSemester)) {
                    index = i;
                    break;
                }
            }
            spinnerSemester.setSelection(index);
        }
    }

    private void updateData(String newPdfUrl) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("courseId", courseId);
        updatedData.put("courseName", courseName);
        updatedData.put("semester", semester);
        updatedData.put("examType", examType);
        updatedData.put("pdfType", pdfType);
        updatedData.put("year", year);
        updatedData.put("pdfUrl", newPdfUrl);

        databaseReference.child(key).updateChildren(updatedData)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(this, AdminDashBoard.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // To start a new activity
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show();
        }
    }
}
