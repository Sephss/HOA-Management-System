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

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.Model.CommunitySolicitationEntry;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.adapters.CommunitySolicitationAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommunitySolicitationPage extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;

    private CommunitySolicitationAdapter adapter;
    private final List<CommunitySolicitationEntry> solicitList = new ArrayList<>();
    private final Map<String, Boolean> submittedMap = new HashMap<>();

    private String uid;
    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_community_solicitation_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uid = FirebaseAuthManager.getCurrentUserUid();

        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.solicitRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);

        backBtn.setOnClickListener(v -> finish());

        setupRecyclerView();
        fetchSolicitations();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommunitySolicitationAdapter(this, solicitList, submittedMap);
        recyclerView.setAdapter(adapter);
    }

    private void fetchSolicitations() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        databaseRef = FirebaseDatabase.getInstance().getReference("CommunitySolicitations");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                solicitList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    CommunitySolicitationEntry entry = child.getValue(CommunitySolicitationEntry.class);
                    if (entry != null) {
                        if (entry.getSolicitId() == null || entry.getSolicitId().isEmpty()) {
                            entry.setSolicitId(child.getKey());
                        }
                        solicitList.add(entry);
                    }
                }

                // Newest first
                Collections.sort(solicitList, new Comparator<CommunitySolicitationEntry>() {
                    @Override
                    public int compare(CommunitySolicitationEntry a, CommunitySolicitationEntry b) {
                        return Long.compare(b.getTimestamp(), a.getTimestamp());
                    }
                });

                progressBar.setVisibility(View.GONE);

                if (solicitList.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    checkSubmittedStatuses();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                emptyText.setText("Failed to load solicitations.");
                emptyText.setVisibility(View.VISIBLE);
            }
        };

        databaseRef.addValueEventListener(valueEventListener);
    }

    /** For each solicitation, checks whether the current user already has a contribution on file. */
    private void checkSubmittedStatuses() {
        if (uid == null) return;

        for (CommunitySolicitationEntry entry : solicitList) {
            String solicitId = entry.getSolicitId();
            if (solicitId == null) continue;

            FirebaseDatabase.getInstance()
                    .getReference("CommunitySolicitations")
                    .child(solicitId)
                    .child("contributions")
                    .child(uid)
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            submittedMap.put(solicitId, snapshot.exists());
                            adapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onCancelled(DatabaseError error) {
                            // ignore — button just won't reflect submitted state
                        }
                    });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseRef != null && valueEventListener != null) {
            databaseRef.removeEventListener(valueEventListener);
        }
    }
}