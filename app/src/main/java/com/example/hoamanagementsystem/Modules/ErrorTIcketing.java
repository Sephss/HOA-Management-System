package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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
import com.example.hoamanagementsystem.Model.ErrorTicketEntry;
import com.example.hoamanagementsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class ErrorTIcketing extends AppCompatActivity {

    private ImageView backBtn;
    private Button newTicketBtn;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private TextView emptyText;

    private com.example.hoamanagementsystem.Modules.ErrorTicketAdapter adapter;
    private final List<ErrorTicketEntry> ticketList = new ArrayList<>();

    private String uid;
    private DatabaseReference databaseRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_error_ticketing);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uid = FirebaseAuthManager.getCurrentUserUid();

        backBtn = findViewById(R.id.backBtn);
        newTicketBtn = findViewById(R.id.newTicketBtn);
        recyclerView = findViewById(R.id.ticketRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        emptyText = findViewById(R.id.emptyText);

        backBtn.setOnClickListener(v -> finish());
        newTicketBtn.setOnClickListener(v ->
                startActivity(new Intent(ErrorTIcketing.this, CreateErrorTicketing.class)));

        setupRecyclerView();
        fetchTickets();
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new com.example.hoamanagementsystem.Modules.ErrorTicketAdapter(this, ticketList);
        recyclerView.setAdapter(adapter);
    }

    private void fetchTickets() {
        if (uid == null) return;

        progressBar.setVisibility(View.VISIBLE);
        emptyText.setVisibility(View.GONE);

        databaseRef = FirebaseDatabase.getInstance().getReference("errorTicketing").child(uid);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ticketList.clear();

                for (DataSnapshot child : snapshot.getChildren()) {
                    ErrorTicketEntry entry = child.getValue(ErrorTicketEntry.class);
                    if (entry != null) {
                        if (entry.getTicketId() == null || entry.getTicketId().isEmpty()) {
                            entry.setTicketId(child.getKey());
                        }
                        if (entry.getUserId() == null || entry.getUserId().isEmpty()) {
                            entry.setUserId(uid);
                        }
                        ticketList.add(entry);
                    }
                }

                Collections.sort(ticketList, new Comparator<ErrorTicketEntry>() {
                    @Override
                    public int compare(ErrorTicketEntry a, ErrorTicketEntry b) {
                        return Long.compare(b.getTimestamp(), a.getTimestamp());
                    }
                });

                progressBar.setVisibility(View.GONE);

                if (ticketList.isEmpty()) {
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
                emptyText.setText("Failed to load your tickets.");
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