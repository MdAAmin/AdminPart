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
import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.HashMap;
import java.util.Map;

public class UpdateActivity extends AppCompatActivity {

    private static final int PDF_REQUEST_CODE = 2;

    private static final String COURSE_ID_REGEX = "^(CSE|GED)-\\d{4}$";
    private static final String COURSE_NAME_REGEX = "^[A-Za-z\\s,&]+$";
    private static final String YEAR_REGEX = "^(20[0-9]{2}|2100)$";

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

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("PDFs");

        // Fetch data from intent
        key = getIntent().getStringExtra("key");

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

        // Set existing data
        etCourseId.setText(courseId);
        etCourseName.setText(courseName);
        etYearName.setText(year);

        setupSpinner(semester);

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

            if (courseId.isEmpty() || !courseId.matches(COURSE_ID_REGEX)) {
                etCourseId.setError("Invalid Course ID (Format: CSE-XXXX or GED-XXXX)");
                etCourseId.requestFocus();
            } else if (courseName.isEmpty() || !courseName.matches(COURSE_NAME_REGEX)) {
                etCourseName.setError("Invalid Course Name (Alphabets and spaces only)");
                etCourseName.requestFocus();
            } else if (year.isEmpty() || !year.matches(YEAR_REGEX)) {
                etYearName.setError("Invalid Year (Must be between 2000 and 2100)");
                etYearName.requestFocus();
            } else if (semester == null || semester.equals("Select a Semester")) {
                Toast.makeText(this, "Please select a semester", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);

                // Upload new PDF if selected, else keep existing
                if (pdfUri == null) {
                    updateData(pdfUrl);
                } else {
                    uploadPdfToCloudinary();
                }
            }
        });
    }

    private void setupSpinner(String fetchedSemester) {
        String[] semesters = {"Select a Semester", "Fall", "Summer", "Winter"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerSemester.setAdapter(adapter);

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

    private void uploadPdfToCloudinary() {
        if (pdfUri != null) {
            MediaManager.get().upload(pdfUri)
                    .callback(new UploadCallback() {
                        @Override
                        public void onStart(String requestId) {
                            progressBar.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onProgress(String requestId, long bytes, long totalBytes) {
                        }

                        @Override
                        public void onSuccess(String requestId, Map resultData) {
                            String newPdfUrl = resultData.get("secure_url").toString();
                            updateData(newPdfUrl);
                        }

                        @Override
                        public void onError(String requestId, ErrorInfo error) {
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(UpdateActivity.this, "Upload Failed: " + error, Toast.LENGTH_SHORT).show();

                        }

                        @Override
                        public void onReschedule(String requestId, ErrorInfo error) {

                        }

                    }).dispatch();
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
                    startActivity(new Intent(this, AdminDashBoard.class));
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
