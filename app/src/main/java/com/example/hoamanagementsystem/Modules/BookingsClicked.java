package com.example.hoamanagementsystem.Modules;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.R;

public class BookingsClicked extends AppCompatActivity {

    private TextView sportCategory, bookingStatus, bookingDate, bookingTime,
            bookingPurpose, remarksLabel, bookingRemarks, bookerName, dateRequested;
    private View statusPill, statusDot, adminRemarksSection, backButton, divider2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings_clicked);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();

        backButton.setOnClickListener(v -> finish());

        displayBookingDetails();
    }

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        sportCategory = findViewById(R.id.sportCategory);
        statusPill = findViewById(R.id.statusPill);
        statusDot = findViewById(R.id.statusDot);
        bookingStatus = findViewById(R.id.bookingStatus);
        bookingDate = findViewById(R.id.bookingDate);
        bookingTime = findViewById(R.id.bookingTime);
        bookingPurpose = findViewById(R.id.bookingPurpose);
        remarksLabel = findViewById(R.id.remarksLabel);
        bookingRemarks = findViewById(R.id.bookingRemarks);
        divider2 = findViewById(R.id.divider2);
        bookerName = findViewById(R.id.bookerName);
        dateRequested = findViewById(R.id.dateRequested);
        adminRemarksSection = findViewById(R.id.adminRemarksSection);
    }

    private void displayBookingDetails() {
        String sport = getIntent().getStringExtra("bookingSport");
        String status = getIntent().getStringExtra("bookingStatus");
        String requestBookingDate = getIntent().getStringExtra("reqeustBookingDate");
        String requestBookingTime = getIntent().getStringExtra("requestBookingTime");
        String purpose = getIntent().getStringExtra("bookingPurpose");
        String remarks = getIntent().getStringExtra("bookerRemarks");
        String booker = getIntent().getStringExtra("bookerName");
        String bookedDate = getIntent().getStringExtra("bookedDate");

        sportCategory.setText(sport);
        bookingDate.setText(requestBookingDate);
        bookingTime.setText(requestBookingTime);
        bookingPurpose.setText(purpose);
        bookerName.setText(booker);
        dateRequested.setText(bookedDate);

        if (remarks == null || remarks.trim().isEmpty()) {
            remarksLabel.setVisibility(View.GONE);
            bookingRemarks.setVisibility(View.GONE);
            divider2.setVisibility(View.GONE);
        } else {
            remarksLabel.setVisibility(View.VISIBLE);
            bookingRemarks.setVisibility(View.VISIBLE);
            divider2.setVisibility(View.VISIBLE);
            bookingRemarks.setText(remarks);
        }

        bookingStatus.setText(capitalize(status));

        if ("cancelled".equalsIgnoreCase(status)) {
            statusPill.setBackgroundResource(R.drawable.status_pill_cancelled);
            statusDot.setBackgroundResource(R.drawable.booking_accent_cancelled);
            bookingStatus.setTextColor(ContextCompat.getColor(this, R.color.grey));
            adminRemarksSection.setVisibility(View.VISIBLE);
        } else {
            statusPill.setBackgroundResource(R.drawable.status_pill_confirmed);
            statusDot.setBackgroundResource(R.drawable.booking_accent_confirmed);
            bookingStatus.setTextColor(ContextCompat.getColor(this, R.color.green));
            adminRemarksSection.setVisibility(View.GONE);
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}