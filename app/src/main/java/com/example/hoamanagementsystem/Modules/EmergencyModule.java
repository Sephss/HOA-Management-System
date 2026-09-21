package com.example.hoamanagementsystem.Modules;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.EmergencyEntry;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.adapters.EmergencyAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EmergencyModule extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;

    private EmergencyAdapter adapter;
    private final List<EmergencyEntry> emergencyList = new ArrayList<>();

    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_emergency_module);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.emergencyRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);

        backBtn.setOnClickListener(v -> finish());

        setupRecyclerView();
        fetchEmergencyEntries();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new EmergencyAdapter(this, emergencyList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchEmergencyEntries() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        databaseRef = FirebaseDatabase.getInstance().getReference("EmergencyDirectories");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                emergencyList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    EmergencyEntry entry = child.getValue(EmergencyEntry.class);
                    if (entry != null) {
                        // fall back to the key if entryId isn't stored
                        if (entry.getEntryId() == null || entry.getEntryId().isEmpty()) {
                            entry.setEntryId(child.getKey());
                        }
                        emergencyList.add(entry);
                    }
                }

                // Sort so the newest created entry appears first
                Collections.sort(emergencyList, new Comparator<EmergencyEntry>() {
                    @Override
                    public int compare(EmergencyEntry a, EmergencyEntry b) {
                        return Long.compare(b.getTimestamp(), a.getTimestamp());
                    }
                });

                progressBar.setVisibility(View.GONE);

                if (emergencyList.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                emptyText.setText("Failed to load emergency entries.");
                emptyText.setVisibility(View.VISIBLE);
            }
        };

        databaseRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Prevent memory leaks / stale listeners
        if (databaseRef != null && valueEventListener != null) {
            databaseRef.removeEventListener(valueEventListener);
        }
    }
}