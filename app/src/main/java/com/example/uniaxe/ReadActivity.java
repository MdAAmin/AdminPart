package com.example.uniaxe;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ReadActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private TextView noDataTextView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private ArrayList<Model> itemList;
    private CustomAdapter customAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);

        // Initialize views
        recyclerView = findViewById(R.id.recyclerView);
        noDataTextView = findViewById(R.id.noData);
        progressBar = findViewById(R.id.progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        itemList = new ArrayList<>();

        // Initialize adapter and set it to RecyclerView
        customAdapter = new CustomAdapter(itemList, this);
        recyclerView.setAdapter(customAdapter);

        // Firebase Database reference
        databaseReference = FirebaseDatabase.getInstance().getReference("PDFs");

        fetchItems();
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
                        itemList.add(item);
                    }
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
                Toast.makeText(ReadActivity.this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
