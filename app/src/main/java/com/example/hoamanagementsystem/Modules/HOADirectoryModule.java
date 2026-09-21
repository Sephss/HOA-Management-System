package com.example.hoamanagementsystem.Modules;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.HOARuleEntry;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.adapters.HOARuleAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class HOADirectoryModule extends AppCompatActivity {

    private ImageView backBtn;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;

    private HOARuleAdapter adapter;
    private final List<HOARuleEntry> ruleList = new ArrayList<>();

    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_hoadirectory_module);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        backBtn = findViewById(R.id.backBtn);
        recyclerView = findViewById(R.id.hoaRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);

        backBtn.setOnClickListener(v -> finish());

        setupRecyclerView();
        fetchHOARules();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HOARuleAdapter(this, ruleList);
        recyclerView.setAdapter(adapter);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        divider.setDrawable(ContextCompat.getDrawable(this, R.drawable.divider_line));
        recyclerView.addItemDecoration(divider);
    }

    private void fetchHOARules() {
        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        databaseRef = FirebaseDatabase.getInstance().getReference("HOARules");

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ruleList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    HOARuleEntry entry = child.getValue(HOARuleEntry.class);
                    if (entry != null) {
                        if (entry.getRuleId() == null || entry.getRuleId().isEmpty()) {
                            entry.setRuleId(child.getKey());
                        }
                        ruleList.add(entry);
                    }
                }

                // Sort so the newest created rule appears first
                Collections.sort(ruleList, new Comparator<HOARuleEntry>() {
                    @Override
                    public int compare(HOARuleEntry a, HOARuleEntry b) {
                        return Long.compare(b.getTimestamp(), a.getTimestamp());
                    }
                });

                progressBar.setVisibility(View.GONE);

                if (ruleList.isEmpty()) {
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
                emptyText.setText("Failed to load HOA rules.");
                emptyText.setVisibility(View.VISIBLE);
            }
        };

        databaseRef.addValueEventListener(valueEventListener);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseRef != null && valueEventListener != null) {
            databaseRef.removeEventListener(valueEventListener);
        }
    }
}