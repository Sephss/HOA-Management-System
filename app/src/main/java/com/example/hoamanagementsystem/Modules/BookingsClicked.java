package com.example.hoamanagementsystem.Modules;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseBookingSlotManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseBookingsManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateBookingStatusCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;

public class BookingsClicked extends AppCompatActivity {

    private TextView sportCategory, bookingStatus, bookingDate, bookingTime,
            adminRemarksText,
            bookingPurpose, remarksLabel, bookingRemarks, bookerName, dateRequested;
    private View statusPill, statusDot, adminRemarksSection, backButton, divider2;
    private Button cancelReservationBtn, cancelReservationBtnAdmin;
    private EditText cancelRemarks;
    private HomeOwnerRentersModel currentUser;
    private String bookingID, sport, status, requestBookingDate, requestBookingTime;
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_bookings_clicked);
        currentUser = UserSession.getInstance().getCurrentUser();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();

        backButton.setOnClickListener(v -> finish());

        displayBookingDetails();

        cancelReservationBtn.setOnClickListener(v -> showCancelConfirmation());

        cancelReservationBtnAdmin.setOnClickListener(d -> {
            showCancelConfirmationAdmin();
        });


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
        cancelReservationBtn = findViewById(R.id.cancelReservationBtn);
        cancelReservationBtnAdmin = findViewById(R.id.cancelReservationBtnAdmin);
        cancelRemarks = findViewById(R.id.cancelRemarks);
        adminRemarksText = findViewById(R.id.adminRemarksText);

    }

    private void displayBookingDetails() {


        bookingID = getIntent().getStringExtra("bookingID");
        sport = getIntent().getStringExtra("bookingSport");
        status = getIntent().getStringExtra("bookingStatus");
        requestBookingDate = getIntent().getStringExtra("reqeustBookingDate");
        requestBookingTime = getIntent().getStringExtra("requestBookingTime");
        String purpose = getIntent().getStringExtra("bookingPurpose");
        String remarks = getIntent().getStringExtra("bookerRemarks");
        String booker = getIntent().getStringExtra("bookerName");
        String bookedDate = getIntent().getStringExtra("bookedDate");
        String adminRemarks = getIntent().getStringExtra("adminRemarks");

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

        if(status.equals("cancelled")) {
            statusPill.setBackgroundResource(R.drawable.status_pill_cancelled);
            statusDot.setBackgroundResource(R.drawable.booking_accent_cancelled);
            bookingStatus.setTextColor(ContextCompat.getColor(this, R.color.grey));

            cancelReservationBtnAdmin.setVisibility(View.GONE);
            cancelReservationBtn.setVisibility(View.GONE);
            cancelRemarks.setVisibility(View.GONE);

            if (adminRemarks == null || adminRemarks.trim().isEmpty()) {
                adminRemarksSection.setVisibility(View.VISIBLE);
                adminRemarksText.setVisibility(View.GONE);
            } else {
                adminRemarksSection.setVisibility(View.VISIBLE);
                adminRemarksText.setVisibility(View.VISIBLE);
                adminRemarksText.setText(adminRemarks);
            }
        } else {
            statusPill.setBackgroundResource(R.drawable.status_pill_confirmed);
            statusDot.setBackgroundResource(R.drawable.booking_accent_confirmed);
            bookingStatus.setTextColor(ContextCompat.getColor(this, R.color.green));

            if(currentUser.getRole().equals("Home Owners") || currentUser.getRole().equals("Renters")) {
                adminRemarksSection.setVisibility(View.GONE);
                cancelReservationBtn.setVisibility(View.VISIBLE);
            } else {
                cancelRemarks.setVisibility(View.VISIBLE);
                cancelReservationBtnAdmin.setVisibility(View.VISIBLE);
                cancelReservationBtn.setVisibility(View.GONE);
            }
        }



    }

    private void showCancelConfirmationAdmin() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation? This cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> performCancellationAdmin())
                .setNegativeButton("No", null)
                .show();
    }

    private void showCancelConfirmation() {
        new AlertDialog.Builder(this)
                .setTitle("Cancel Reservation")
                .setMessage("Are you sure you want to cancel this reservation? This cannot be undone.")
                .setPositiveButton("Yes, Cancel", (dialog, which) -> performCancellation())
                .setNegativeButton("No", null)
                .show();
    }

    private void performCancellation() {
        cancelReservationBtn.setEnabled(false);
        cancelReservationBtn.setText("Cancelling...");

        FirebaseBookingsManager.cancelBooking(bookingID, new UpdateBookingStatusCallback() {
            @Override
            public void onSuccess() {
                // Booking marked cancelled, now free up the slot
                FirebaseBookingSlotManager.removeBookingSlot(sport, requestBookingDate, requestBookingTime);

                Toast.makeText(BookingsClicked.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                cancelReservationBtn.setEnabled(true);
                cancelReservationBtn.setText("Cancel Reservation");
                Toast.makeText(BookingsClicked.this, "Failed to cancel: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void performCancellationAdmin() {
        cancelReservationBtn.setEnabled(false);
        cancelReservationBtn.setText("Cancelling...");

        String remarks = cancelRemarks.getText().toString();
        if(remarks.isEmpty()) {
            cancelRemarks.setError("Add remarks");
            return;
        }

        FirebaseBookingsManager.cancelBookingAdmin(bookingID, remarks, new UpdateBookingStatusCallback() {
            @Override
            public void onSuccess() {
                // Booking marked cancelled, now free up the slot
                FirebaseBookingSlotManager.removeBookingSlot(sport, requestBookingDate, requestBookingTime);

                Toast.makeText(BookingsClicked.this, "Reservation cancelled", Toast.LENGTH_SHORT).show();
                finish();
            }

            @Override
            public void onFailure(String message) {
                cancelReservationBtn.setEnabled(true);
                cancelReservationBtn.setText("Cancel Reservation");
                Toast.makeText(BookingsClicked.this, "Failed to cancel: " + message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
}