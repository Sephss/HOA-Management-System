package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseMaintenanceManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseNotificationManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateNotificationCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateMaintenanceStatusCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Model.NotificationModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class MaintenanceRequestClicked extends AppCompatActivity {
    private TextView maintenanceType, maintenanceLocation, maintenanceTicket, maintenanceStatus, maintenanceTitle, maintenanceDescription, maintenanceDate, maintenancePriority;
    private String theType, theSubmitterID, adminRemarksText, dateTimeRepairInProgress, dateCompleted, dateTime, theMaintenanceID, theLocation, theTicket, theStatus, theTitle, theDescription, theDate, thePriority;
    private HomeOwnerRentersModel currentUser;
    private Spinner updateStatusSpinner;
    private EditText remarksET;
    private String notifStatus = "none";
    private ImageView backBtn;
    private String theUserRole;

    private TextView viewImageBtn, closeBtn;
    private ImageView imageDisplay;
    private String theIncidentImage;

    private Button saveUpdateBtn;

    // HOME OWNERS OR RENTERS LAYOUT ELEMENTS
    private LinearLayout requestSubmittedLayout, repairInProgressLayout, completedLayout, adminRemarksLayout;
    private TextView requestSubmittedTV, repairInProgressTV, completedTV, remarksTV;

    private ScrollView adminLayout, homeOwnersRentersLayout;

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_maintenance_request_clicked);

        maintenanceType = findViewById(R.id.maintenanceType);
        maintenanceLocation = findViewById(R.id.maintenanceLocation);
        maintenanceTicket = findViewById(R.id.maintenanceTicket);
        maintenanceStatus = findViewById(R.id.maintenanceStatus);
        maintenanceTitle = findViewById(R.id.maintenanceTitle);
        maintenanceDescription = findViewById(R.id.maintenanceDescription);
        maintenanceDate = findViewById(R.id.dateSubmitted);
        maintenancePriority = findViewById(R.id.maintenancePriority);
        currentUser = UserSession.getInstance().getCurrentUser();

        theUserRole = currentUser.getRole();

        if(theUserRole.equals("Home Owners") || theUserRole.equals("Renters")) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }

        homeOwnersRentersLayout = findViewById(R.id.homeOwnersRentersLayout);
        adminLayout = findViewById(R.id.adminLayout);

        updateStatusSpinner = findViewById(R.id.updateStatusSpinner);
        remarksET = findViewById(R.id.remarksET);

        saveUpdateBtn = findViewById(R.id.saveUpdateBtn);

        backBtn= findViewById(R.id.backBtn);

        requestSubmittedLayout = findViewById(R.id.requestSubmittedLayout);
        repairInProgressLayout = findViewById(R.id.repairInProgressLayout);
        completedLayout = findViewById(R.id.completedLayout);
        adminRemarksLayout = findViewById(R.id.adminRemarksLayout);
        requestSubmittedTV = findViewById(R.id.requestSubmittedTV);
        repairInProgressTV = findViewById(R.id.repairInProgressTV);
        completedTV = findViewById(R.id.completedTV);
        remarksTV = findViewById(R.id.remarksTV);


        imageDisplay = findViewById(R.id.imageDisplay);
        viewImageBtn = findViewById(R.id.viewImageBtn);
        closeBtn = findViewById(R.id.closeBtn);

        Intent data = getIntent();
        theIncidentImage = data.getStringExtra("maintenanceImage");
        theType = data.getStringExtra("maintenanceType");
        theLocation = data.getStringExtra("maintenanceLocation");
        theTicket = data.getStringExtra("maintenanceTicket");
        theStatus = data.getStringExtra("maintenanceStatus");
        theTitle = data.getStringExtra("maintenanceTitle");
        theDescription = data.getStringExtra("maintenanceDescription");
        theDate = data.getStringExtra("maintenanceDate");
        thePriority = data.getStringExtra("maintenancePriority");
        theMaintenanceID = data.getStringExtra("maintenanceID");
        dateTime = data.getStringExtra("dateTime");
        dateTimeRepairInProgress = data.getStringExtra("dateTimeRepairInProgress");
        dateCompleted = data.getStringExtra("dateCompleted");
        adminRemarksText = data.getStringExtra("adminRemarksText");
        theSubmitterID = data.getStringExtra("submitterID");


        setupData();
        showUIBasedOnRole();
        setupSpinner();
        setupProgressHomeOwners();

        viewImageBtn.setOnClickListener(d -> {
            viewImageBtn.setVisibility(View.GONE);
            closeBtn.setVisibility(View.VISIBLE);
            imageDisplay.setVisibility(View.VISIBLE);
        });

        closeBtn.setOnClickListener(d -> {
            viewImageBtn.setVisibility(View.VISIBLE);
            closeBtn.setVisibility(View.GONE);
            imageDisplay.setVisibility(View.GONE);
        });

        saveUpdateBtn.setOnClickListener(d -> {
            updateStatus();
        });

        backBtn.setOnClickListener(f -> {
            finish();
        });
    }
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    private void setupProgressHomeOwners() {

        // Hide everything first
        requestSubmittedLayout.setVisibility(View.GONE);
        repairInProgressLayout.setVisibility(View.GONE);
        completedLayout.setVisibility(View.GONE);
        adminRemarksLayout.setVisibility(View.GONE);

        // Set remarks text
        remarksTV.setText(adminRemarksText);

        // Always show submitted if date exists
        if (isNotEmpty(dateTime)) {
            requestSubmittedLayout.setVisibility(View.VISIBLE);
            requestSubmittedTV.setText(dateTime);
        }

        if ("pending".equalsIgnoreCase(theStatus)) {

            // Only submitted

        } else if ("repair_in_progress".equalsIgnoreCase(theStatus)) {

            if (isNotEmpty(dateTimeRepairInProgress)) {
                repairInProgressLayout.setVisibility(View.VISIBLE);
                repairInProgressTV.setText(dateTimeRepairInProgress);
            }

            if (isNotEmpty(adminRemarksText)) {
                adminRemarksLayout.setVisibility(View.VISIBLE);
            }

        } else if ("completed".equalsIgnoreCase(theStatus)) {

            if (isNotEmpty(dateTimeRepairInProgress)) {
                repairInProgressLayout.setVisibility(View.VISIBLE);
                repairInProgressTV.setText(dateTimeRepairInProgress);
            }

            if (isNotEmpty(dateCompleted)) {
                completedLayout.setVisibility(View.VISIBLE);
                completedTV.setText(dateCompleted);
            }

            if (isNotEmpty(adminRemarksText)) {
                adminRemarksLayout.setVisibility(View.VISIBLE);
            }
        }
    }
    private void updateStatus() {
        long timestamp = System.currentTimeMillis();
        String theTimestamp = String.valueOf(timestamp);
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd, yyyy",
                Locale.ENGLISH
        );

        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        String currentDate = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "hh:mm a",
                Locale.ENGLISH
        );

        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        String currentTime = timeFormat.format(new Date());

        String theStatus = "";
        String selectedStatus = updateStatusSpinner
                .getSelectedItem()
                .toString();

        if(selectedStatus.equals("Update Status")) {
            Toast.makeText(this, "Select status", Toast.LENGTH_SHORT).show();
        } else if (selectedStatus.equals("Repair in progress")) {
            theStatus = "repair_in_progress";
            notifStatus = "repair_in_progress";
        } else if (selectedStatus.equals("Completed")) {
            theStatus = "completed";
            notifStatus = "repair_in_progress";
        }

        String remarks = remarksET.getText().toString();

        String dateTime = new java.text.SimpleDateFormat(
                "MMMM dd, yyyy • hh:mm a",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        String notifMessage;

        switch (theStatus.toLowerCase()) {

            case "repair_in_progress":
                notifMessage = "Your maintenance request is now in progress.";
                break;

            case "completed":
                notifMessage = "Your maintenance request has been marked as completed.";
                break;

            default:
                notifMessage = "Your maintenance request has been updated.";
                break;
        }

        setLoadingState();
        FirebaseMaintenanceManager.updateMaintenanceStatus(theMaintenanceID, theStatus, remarks, dateTime, new UpdateMaintenanceStatusCallback() {
            @Override
            public void onSuccess(String message) {

                NotificationModel data = new NotificationModel("", theSubmitterID,"", remarks, theType, notifStatus, currentDate, currentTime, theMaintenanceID, "no", theTimestamp, notifMessage);

                FirebaseNotificationManager.createNotification(data, new CreateNotificationCallback() {
                    @Override
                    public void onSuccess(String success) {
                        setNormalState();
                        Toast.makeText(
                                MaintenanceRequestClicked.this,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();
                        finish();
                    }

                    @Override
                    public void onFailure(String error) {
                        setNormalState();
                    }
                });

            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MaintenanceRequestClicked.this, error, Toast.LENGTH_SHORT).show();
                setNormalState();
            }
        });

    }

    private void showUIBasedOnRole() {
        if(currentUser.getRole().equals("Home Owners") || currentUser.getRole().equals("Renters")) {
            homeOwnersRentersLayout.setVisibility(View.VISIBLE);
            adminLayout.setVisibility(View.GONE);
        } else {
            homeOwnersRentersLayout.setVisibility(View.GONE);
            adminLayout.setVisibility(View.VISIBLE);
        }
    }
    private void setupData() {
        maintenanceType.setText(theType);
        maintenanceLocation.setText(theLocation);
        maintenanceTicket.setText(theTicket);
        maintenanceTitle.setText(theTitle);
        maintenanceDescription.setText(theDescription);
        maintenanceDate.setText(theDate);
        maintenancePriority.setText(thePriority);

        Picasso.get().load(theIncidentImage).into(imageDisplay);

        switch(theStatus) {
            case "pending":
                maintenanceStatus.setBackgroundResource(
                        R.drawable.pending_document_request
                );

                maintenanceStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.darkyellow)
                );

                maintenanceStatus.setText("Pending");
                break;

            case "repair_in_progress":
                maintenanceStatus.setBackgroundResource(
                        R.drawable.under_review_color
                );

                maintenanceStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.darkyellow)
                );

                maintenanceStatus.setText("In Progress");
                break;

            case "completed":
                maintenanceStatus.setBackgroundResource(
                        R.drawable.approved_and_ready_color
                );

                maintenanceStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.green)
                );

                maintenanceStatus.setText("Completed");
                break;

        }

    }
    private void setupSpinner() {

        List<String> maintenanceTypes = Arrays.asList(
                "Update Status",
                "Repair in progress",
                "Completed"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this, R.layout.spinner_item,
                maintenanceTypes
        ) {
            @Override
            public boolean isEnabled(int position) {

                if (theStatus == null) {
                    return true;
                }

                switch (theStatus) {

                    case "repair_in_progress":
                        // Disable only "Update Status"
                        return position != 0;

                    case "completed":
                        // Disable "Update Status" and "Under Investigation"
                        return position == 2;

                    default: // pending
                        return true;
                }
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {

                View view = super.getDropDownView(position, convertView, parent);

                TextView tv = view.findViewById(android.R.id.text1);

                if (isEnabled(position)) {
                    tv.setTextColor(Color.BLACK);
                } else {
                    tv.setTextColor(Color.GRAY);
                }

                return view;
            }
        };

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateStatusSpinner.setAdapter(adapter);
    }
    private void setLoadingState() {
        saveUpdateBtn.setEnabled(false);
        saveUpdateBtn.setAlpha(0.5f);
        saveUpdateBtn.setText("Saving...");
    }

    private void setNormalState() {
        saveUpdateBtn.setEnabled(true);
        saveUpdateBtn.setAlpha(1f);
        saveUpdateBtn.setText("Save Update");
    }
}