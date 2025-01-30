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

public class ReadGuideTeacherActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataTextView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private ArrayList<ModelTeacher> itemList;
    private ArrayList<ModelTeacher> filteredItemList; // New list for filtered results
    private TeacherCustomAdapter customAdapter;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_guide_teacher);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noData);
        progressBar = findViewById(R.id.progressBar);
        searchView = findViewById(R.id.searchView); // Initialize the search view

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        itemList = new ArrayList<>();
        filteredItemList = new ArrayList<>();  // Initialize the filtered list

        customAdapter = new TeacherCustomAdapter(filteredItemList, this);
        recyclerView.setAdapter(customAdapter);

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("Syllabus");

        // Fetch items from Firebase
        fetchItems();

        // Search functionality
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;  // Optional, handle search submit action if needed
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
                filteredItemList.clear();
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        ModelTeacher item = dataSnapshot.getValue(ModelTeacher.class);
                        if (item != null) {
                            itemList.add(item);
                        }
                    }

                    // Add all items to filtered list initially
                    filteredItemList.addAll(itemList);
                    customAdapter.notifyDataSetChanged();
                    noDataTextView.setVisibility(View.GONE);
                } else {
                    noDataTextView.setVisibility(View.VISIBLE);
                }
                progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReadGuideTeacherActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Filter list based on query
    public void filterList(String query) {
        filteredItemList.clear();
        if (query.isEmpty()) {
            filteredItemList.addAll(itemList);  // Show all items if query is empty
        } else {
            for (ModelTeacher item : itemList) {
                if (item.getTeacherName().toLowerCase().contains(query.toLowerCase()) || // Added this line
                        item.getCouName().toLowerCase().contains(query.toLowerCase()) ||
                        item.getCouId().toLowerCase().contains(query.toLowerCase()) || // Added this line
                        item.getBatch().toLowerCase().contains(query.toLowerCase())) {
                    filteredItemList.add(item);
                }
            }
        }
        customAdapter.notifyDataSetChanged();
    }
}
