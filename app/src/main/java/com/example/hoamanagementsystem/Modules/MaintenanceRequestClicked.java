package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseMaintenanceManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateMaintenanceStatusCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;

import java.util.Arrays;
import java.util.List;

public class MaintenanceRequestClicked extends AppCompatActivity {
    private TextView maintenanceType, maintenanceLocation, maintenanceTicket, maintenanceStatus, maintenanceTitle, maintenanceDescription, maintenanceDate, maintenancePriority;
    private String theType, adminRemarksText, dateTimeRepairInProgress, dateCompleted, dateTime, theMaintenanceID, theLocation, theTicket, theStatus, theTitle, theDescription, theDate, thePriority;
    private HomeOwnerRentersModel currentUser;
    private Spinner updateStatusSpinner;
    private EditText remarksET;

    private Button saveUpdateBtn;

    // HOME OWNERS OR RENTERS LAYOUT ELEMENTS
    private LinearLayout requestSubmittedLayout, repairInProgressLayout, completedLayout, adminRemarksLayout;
    private TextView requestSubmittedTV, repairInProgressTV, completedTV, remarksTV;

    private ScrollView adminLayout, homeOwnersRentersLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
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

        homeOwnersRentersLayout = findViewById(R.id.homeOwnersRentersLayout);
        adminLayout = findViewById(R.id.adminLayout);

        updateStatusSpinner = findViewById(R.id.updateStatusSpinner);
        remarksET = findViewById(R.id.remarksET);

        saveUpdateBtn = findViewById(R.id.saveUpdateBtn);

        requestSubmittedLayout = findViewById(R.id.requestSubmittedLayout);
        repairInProgressLayout = findViewById(R.id.repairInProgressLayout);
        completedLayout = findViewById(R.id.completedLayout);
        adminRemarksLayout = findViewById(R.id.adminRemarksLayout);
        requestSubmittedTV = findViewById(R.id.requestSubmittedTV);
        repairInProgressTV = findViewById(R.id.repairInProgressTV);
        completedTV = findViewById(R.id.completedTV);
        remarksTV = findViewById(R.id.remarksTV);

        Intent data = getIntent();
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

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupData();
        showUIBasedOnRole();
        setupSpinner();
        setupProgressHomeOwners();

        saveUpdateBtn.setOnClickListener(d -> {
            updateStatus();
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
        String theStatus = "";
        String selectedStatus = updateStatusSpinner
                .getSelectedItem()
                .toString();

        if(selectedStatus.equals("Update Status")) {
            Toast.makeText(this, "Select status", Toast.LENGTH_SHORT).show();
        } else if (selectedStatus.equals("Repair in progress")) {
            theStatus = "repair_in_progress";
        } else if (selectedStatus.equals("Completed")) {
            theStatus = "completed";
        }

        String remarks = remarksET.getText().toString();

        String dateTime = new java.text.SimpleDateFormat(
                "MMMM dd, yyyy • hh:mm a",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        FirebaseMaintenanceManager.updateMaintenanceStatus(theMaintenanceID, theStatus, remarks, dateTime, new UpdateMaintenanceStatusCallback() {
            @Override
            public void onSuccess(String message) {
                Toast.makeText(MaintenanceRequestClicked.this, message, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(String error) {
                Toast.makeText(MaintenanceRequestClicked.this, error, Toast.LENGTH_SHORT).show();
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
        maintenanceStatus.setText(theStatus);
        maintenanceTitle.setText(theTitle);
        maintenanceDescription.setText(theDescription);
        maintenanceDate.setText(theDate);
        maintenancePriority.setText(thePriority);

    }
    private void setupSpinner() {

        List<String> maintenanceTypes = Arrays.asList(
                "Update Status",
                "Repair in progress",
                "Completed"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                maintenanceTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateStatusSpinner.setAdapter(adapter);
    }
}