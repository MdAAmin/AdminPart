package com.example.uniaxe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Map;

public class CreateActivity extends AppCompatActivity {

    private static final int PDF_REQ = 1;

    private EditText etQuestionId, etCourseName, etYearName;
    private RadioGroup radioGroup, radioGroupQuestionNote;
    private ImageView pdfIn;
    private ProgressBar progressBar;
    private Uri pdfUri;
    private DatabaseReference reference;
    private String pdfUrl;
    private String selectedSemester, selectedType;

    @SuppressLint("MissingInflatedId")
    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create);

        // Initialize UI components
        etQuestionId = findViewById(R.id.et_course_id);
        etCourseName = findViewById(R.id.et_course_name);
        etYearName = findViewById(R.id.et_year_name);
        Spinner spinner = findViewById(R.id.spinner_semester);
        radioGroup = findViewById(R.id.radioGroup);
        radioGroupQuestionNote = findViewById(R.id.radioGroup_question_note);
        RadioButton radioMid = findViewById(R.id.radio_mid);
        RadioButton radioFinal = findViewById(R.id.radio_final);
        RadioButton radioQuestion = findViewById(R.id.radio_question);
        RadioButton radioNote = findViewById(R.id.radio_note);
        pdfIn = findViewById(R.id.pdf_in);
        Button btnInsertFile = findViewById(R.id.btnInsertFile);
        progressBar = findViewById(R.id.progressBar);

        // Set up the Spinner with semester options
        String[] semesters = {"Select a Semester", "Fall", "Summer", "Spring"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, semesters);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // Spinner item selection listener
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                selectedSemester = parentView.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // Optional: Handle case when no item is selected
            }
        });

        reference = FirebaseDatabase.getInstance().getReference().child("PDFs");

        // Select PDF ImageView
        pdfIn.setOnClickListener(v -> {
            if (ContextCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Intent intent = new Intent();
                intent.setType("application/pdf");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, PDF_REQ);
            } else {
                ActivityCompat.requestPermissions(CreateActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PDF_REQ);
            }
        });

        // Submit Data Button
        btnInsertFile.setOnClickListener(v -> {
            String courseId = etQuestionId.getText().toString().trim();
            String courseName = etCourseName.getText().toString().trim();
            String yearName = etYearName.getText().toString().trim();
            int selectedExamTypeId = radioGroup.getCheckedRadioButtonId();
            String examType = null;
            int selectedTypeId = radioGroupQuestionNote.getCheckedRadioButtonId();
            String pdfType = null;

            // Check which exam type (Mid/Final) is selected
            if (selectedExamTypeId == R.id.radio_mid) {
                examType = "Mid";
            } else if (selectedExamTypeId == R.id.radio_final) {
                examType = "Final";
            }

            // Check which PDF type (Question/Note) is selected
            if (selectedTypeId == R.id.radio_question) {
                pdfType = "Question";
            } else if (selectedTypeId == R.id.radio_note) {
                pdfType = "Note";
            }

            // Validate the inputs
            if (courseId.isEmpty()) {
                etQuestionId.setError("Course ID is required");
                etQuestionId.requestFocus();
            } else if (courseName.isEmpty()) {
                etCourseName.setError("Course Name is required");
                etCourseName.requestFocus();
            } else if (yearName.isEmpty()) {
                etYearName.setError("Year is required");
                etYearName.requestFocus();
            } else if (examType == null) {
                Toast.makeText(CreateActivity.this, "Please select an exam type", Toast.LENGTH_SHORT).show();
            } else if (pdfType == null) {
                Toast.makeText(CreateActivity.this, "Please select a PDF type", Toast.LENGTH_SHORT).show();
            } else if (pdfUri == null) {
                Toast.makeText(CreateActivity.this, "Please select a PDF", Toast.LENGTH_SHORT).show();
            } else if (selectedSemester == null || selectedSemester.equals("Select a Semester")) {
                Toast.makeText(CreateActivity.this, "Please select a semester", Toast.LENGTH_SHORT).show();
            } else {
                progressBar.setVisibility(View.VISIBLE);
                uploadPdfToCloudinary(pdfUri);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_REQ && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            pdfIn.setImageResource(R.drawable.select_pdf); // Update image to indicate selection
            Toast.makeText(CreateActivity.this, "PDF selected", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(CreateActivity.this, "Please select a PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // Upload PDF to Cloudinary
    private void uploadPdfToCloudinary(Uri pdfUri) {
        MediaManager.get().upload(pdfUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {
                    }

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {
                    }

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        pdfUrl = (String) resultData.get("secure_url");
                        uploadData(pdfUrl);
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        Toast.makeText(CreateActivity.this, "Error uploading PDF: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                        progressBar.setVisibility(View.GONE);
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {
                    }
                }).dispatch();
    }

    // Upload data to Firebase after successful PDF upload
    private void uploadData(String pdfUrl) {
        String key = reference.push().getKey();
        String courseId = etQuestionId.getText().toString().trim();
        String courseName = etCourseName.getText().toString().trim();
        String yearName = etYearName.getText().toString().trim();
        int selectedExamTypeId = radioGroup.getCheckedRadioButtonId();
        String examType = (selectedExamTypeId == R.id.radio_mid) ? "Mid" : "Final";
        int selectedTypeId = radioGroupQuestionNote.getCheckedRadioButtonId();
        String pdfType = (selectedTypeId == R.id.radio_question) ? "Question" : "Note";

        // Create a model object to store the data
        Model data = new Model(courseId, courseName, examType, yearName, selectedSemester, pdfType, pdfUrl, key);
        reference.child(key).setValue(data)
                .addOnSuccessListener(unused -> {
                    // Reset form fields after successful upload
                    etQuestionId.setText("");
                    etCourseName.setText("");
                    etYearName.setText("");
                    pdfIn.setImageResource(R.drawable.pdf); // Reset image to default
                    Toast.makeText(CreateActivity.this, "PDF Added Successfully", Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                })
                .addOnFailureListener(e -> {
                    // Handle failure scenario
                    Toast.makeText(CreateActivity.this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                });
    }
}
