package com.example.hoamanagementsystem.Modules;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.Model.ErrorTicketEntry;
import com.example.hoamanagementsystem.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class ErrorTicketDetailsModule extends AppCompatActivity {

    private ImageView backBtn, detailImage;
    private TextView detailCategory, detailStatus, detailTitle, detailMeta, detailDescription;
    private ProgressBar progressBar;
    private ScrollView contentScroll;

    private String ticketId, userId;
    private DatabaseReference ticketRef;
    private ValueEventListener valueEventListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_error_ticket_details_module);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        ticketId = getIntent().getStringExtra("ticketId");
        userId = getIntent().getStringExtra("userId");

        initViews();
        backBtn.setOnClickListener(v -> finish());

        if (TextUtils.isEmpty(ticketId) || TextUtils.isEmpty(userId)) {
            Toast.makeText(this, "Unable to load ticket details.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        fetchTicketDetails();
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        detailImage = findViewById(R.id.detailImage);
        detailCategory = findViewById(R.id.detailCategory);
        detailStatus = findViewById(R.id.detailStatus);
        detailTitle = findViewById(R.id.detailTitle);
        detailMeta = findViewById(R.id.detailMeta);
        detailDescription = findViewById(R.id.detailDescription);
        progressBar = findViewById(R.id.progressBar);
        contentScroll = findViewById(R.id.contentScroll);
    }

    private void fetchTicketDetails() {
        progressBar.setVisibility(View.VISIBLE);
        contentScroll.setVisibility(View.GONE);

        ticketRef = FirebaseDatabase.getInstance()
                .getReference("errorTicketing")
                .child(userId)
                .child(ticketId);

        valueEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                ErrorTicketEntry entry = snapshot.getValue(ErrorTicketEntry.class);
                progressBar.setVisibility(View.GONE);

                if (entry == null) {
                    Toast.makeText(ErrorTicketDetailsModule.this,
                            "Ticket not found.", Toast.LENGTH_SHORT).show();
                    finish();
                    return;
                }

                contentScroll.setVisibility(View.VISIBLE);
                bindData(entry);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ErrorTicketDetailsModule.this,
                        "Failed to load ticket.", Toast.LENGTH_SHORT).show();
            }
        };

        ticketRef.addValueEventListener(valueEventListener);
    }

    private void bindData(ErrorTicketEntry entry) {
        detailTitle.setText(entry.getTitle());
        detailDescription.setText(entry.getDescription());

        // Category chip
        String category = entry.getCategory();
        detailCategory.setText(category);
        if (!TextUtils.isEmpty(category) && category.toLowerCase().contains("bug")) {
            detailCategory.setBackgroundResource(R.drawable.bg_chip_bug);
        } else {
            detailCategory.setBackgroundResource(R.drawable.bg_chip_suggestion);
        }

        // Status chip
        String status = entry.getStatus();
        if (TextUtils.isEmpty(status)) status = "Pending";
        detailStatus.setText(status);
        switch (status) {
            case "In Progress":
                detailStatus.setBackgroundResource(R.drawable.bg_chip_status_progress);
                break;
            case "Resolved":
                detailStatus.setBackgroundResource(R.drawable.bg_chip_status_resolved);
                break;
            case "Rejected":
                detailStatus.setBackgroundResource(R.drawable.bg_chip_status_rejected);
                break;
            default:
                detailStatus.setBackgroundResource(R.drawable.bg_chip_status_pending);
                break;
        }

        // Meta
        String meta = entry.getSubmittedByName() != null ? entry.getSubmittedByName() : "You";
        String dateTime = "";
        if (!TextUtils.isEmpty(entry.getDateCreated())) dateTime = entry.getDateCreated();
        if (!TextUtils.isEmpty(entry.getTimeCreated())) {
            dateTime += (dateTime.isEmpty() ? "" : " • ") + entry.getTimeCreated();
        }
        if (!dateTime.isEmpty()) meta += " • " + dateTime;
        detailMeta.setText(meta);

        // Image
        String imageUrl = entry.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            detailImage.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(imageUrl)
                    .into(detailImage);
        } else {
            detailImage.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (ticketRef != null && valueEventListener != null) {
            ticketRef.removeEventListener(valueEventListener);
        }
    }
}