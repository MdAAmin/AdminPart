package com.example.uniaxe;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UpdateGuideActivity extends AppCompatActivity {

    private static final int PDF_REQUEST_CODE = 2;

    private EditText couNameEditText, couIdEditText, batchEditText;
    private TextView pdfInfoTextView, selectPdfTextView;
    private Button updateButton;
    private ProgressBar progressBar;

    private DatabaseReference databaseReference;
    private String key, couName, couId, batch, pdfUrl;
    private Uri pdfUri;

    // Regex patterns
    private static final String COU_ID_PATTERN = "^(CSE|GED)-\\d{4}$";  // Course ID format: CSE-XXXX or GED-XXXX
    private static final String COU_NAME_PATTERN = "^[A-Za-z\\s,&]+$";  // Only alphabetic, spaces, commas, or ampersands for course name
    private static final String BATCH_PATTERN = "^[0-9]+$";  // Numeric value for batch

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_guide);

        // Initialize views
        couNameEditText = findViewById(R.id.cou_name);
        couIdEditText = findViewById(R.id.cou_id);
        batchEditText = findViewById(R.id.batch_id);
        pdfInfoTextView = findViewById(R.id.pdf_in);
        updateButton = findViewById(R.id.updateButton);
        selectPdfTextView = findViewById(R.id.selectPdf);
        progressBar = findViewById(R.id.progressBar);

        // Get Firebase reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Syllabus");

        // Get data from intent
        key = getIntent().getStringExtra("key");
        couName = getIntent().getStringExtra("couName");
        couId = getIntent().getStringExtra("couId");
        pdfUrl = getIntent().getStringExtra("pdfUrl");
        batch = getIntent().getStringExtra("batch");

        // Set existing data in fields
        couNameEditText.setText(couName);
        couIdEditText.setText(couId);
        batchEditText.setText(batch);
        pdfInfoTextView.setText(pdfUrl != null ? "PDF: " + pdfUrl : "No PDF Selected");

        // Select PDF
        selectPdfTextView.setOnClickListener(v -> requestFilePermission());

        // Update record
        updateButton.setOnClickListener(v -> updateRecord());
    }

    private void requestFilePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13+ use READ_MEDIA_IMAGES
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES) == PackageManager.PERMISSION_GRANTED) {
                pickPdfFile();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_MEDIA_IMAGES}, PDF_REQUEST_CODE);
            }
        } else {
            // For older Android versions use READ_EXTERNAL_STORAGE
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                pickPdfFile();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PDF_REQUEST_CODE);
            }
        }
    }

    private void pickPdfFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        startActivityForResult(intent, PDF_REQUEST_CODE);
    }

    private void updateRecord() {
        couName = couNameEditText.getText().toString().trim();
        couId = couIdEditText.getText().toString().trim();
        batch = batchEditText.getText().toString().trim();

        // Validate fields using regular expressions
        if (!validateInput(couName, COU_NAME_PATTERN)) {  // Validate course name
            Toast.makeText(this, "Invalid course name. Only alphabetic, spaces, commas, or ampersands are allowed!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateInput(couId, COU_ID_PATTERN)) {  // Validate course ID
            Toast.makeText(this, "Invalid course ID. Format must be CSE-XXXX or GED-XXXX!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!validateInput(batch, BATCH_PATTERN)) {  // Validate batch
            Toast.makeText(this, "Invalid batch ID. Only numeric values are allowed!", Toast.LENGTH_SHORT).show();
            return;
        }

        progressBar.setVisibility(View.VISIBLE);

        if (pdfUri == null) {
            // Update metadata only
            updateData(pdfUrl);
        } else {
            // Update metadata and PDF URL
            uploadPdfToCloudinary(pdfUri);
        }
    }

    private boolean validateInput(String input, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(input);
        return matcher.matches();
    }

    private void updateData(String newPdfUrl) {
        Map<String, Object> updatedData = new HashMap<>();
        updatedData.put("couName", couName);
        updatedData.put("couId", couId);
        updatedData.put("batch", batch);  // Ensure batch is updated
        updatedData.put("pdfUrl", newPdfUrl);

        databaseReference.child(key).updateChildren(updatedData)
                .addOnSuccessListener(unused -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Updated successfully!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE);
                    Toast.makeText(this, "Failed to update: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void uploadPdfToCloudinary(Uri pdfUri) {
        MediaManager.get().upload(pdfUri)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        pdfUrl = (String) resultData.get("secure_url");
                        runOnUiThread(() -> updateData(pdfUrl)); // Ensure UI update is done on the main thread
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        runOnUiThread(() -> {
                            Toast.makeText(UpdateGuideActivity.this, "Error uploading PDF: " + error.getDescription(), Toast.LENGTH_SHORT).show();
                            progressBar.setVisibility(View.GONE);
                        });
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) {}
                }).dispatch();
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PDF_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            pdfUri = data.getData();
            pdfInfoTextView.setText("PDF selected: " + getDisplayNameFromUri(pdfUri));
            Toast.makeText(this, "PDF selected", Toast.LENGTH_SHORT).show();
        }
    }

    private String getDisplayNameFromUri(Uri uri) {
        String displayName = "Unknown";
        ContentResolver contentResolver = getContentResolver();
        Cursor cursor = contentResolver.query(uri, null, null, null, null);
        if (cursor != null) {
            int nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            if (nameIndex >= 0 && cursor.moveToFirst()) {
                displayName = cursor.getString(nameIndex);
            }
            cursor.close();
        }
        return displayName;
    }
}
