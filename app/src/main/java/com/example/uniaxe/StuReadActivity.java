package com.example.uniaxe;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class StuReadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataTextView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private ArrayList<Model> itemList;
    private ArrayList<Model> filteredItemList; // List for filtered results
    private StuCustomAdapter stuCustomAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stu_read);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noData);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>(); // Initialize the filtered list

        stuCustomAdapter = new StuCustomAdapter(filteredItemList, this);
        recyclerView.setAdapter(stuCustomAdapter);

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("PDFs");

        // Fetch items from Firebase
        fetchItems();

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false; // Optional: Handle search submit action if needed
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                filterList(newText);
                return true;
            }
        });
    }

    private void fetchItems() {
        progressBar.setVisibility(View.VISIBLE);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                itemList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        Model item = dataSnapshot.getValue(Model.class);
                        if (item != null) {
                            itemList.add(item);
                        }
                    }
                    filteredItemList.clear();
                    filteredItemList.addAll(itemList); // Initially display all items
                    stuCustomAdapter.notifyDataSetChanged();
                    noDataTextView.setVisibility(View.GONE);
                } else {
                    noDataTextView.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(StuReadActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter list based on query
    private void filterList(String query) {
        filteredItemList.clear();
        if (query.isEmpty()) {
            filteredItemList.addAll(itemList); // If no search query, show all items
        } else {
            for (Model item : itemList) {
                // Search by course name, ID, semester, and exam type (midterm/final)
                if (item.getCourseName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getCourseId().toLowerCase().contains(query.toLowerCase()) ||
                        item.getSemester().toLowerCase().contains(query.toLowerCase()) ||
                        item.getExamType().toLowerCase().contains(query.toLowerCase())) {
                    filteredItemList.add(item);
                }
            }
        }
        stuCustomAdapter.notifyDataSetChanged(); // Notify adapter of changes
    }
}
