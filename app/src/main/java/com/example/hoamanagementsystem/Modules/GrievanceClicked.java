package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseGrievanceManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateGrievanceStatusCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;

import java.util.Arrays;
import java.util.List;

public class GrievanceClicked extends AppCompatActivity {
    private String theTitle, theDescription, theType, theLocation, theStatus, theDate, theTicket,incidentReportID;
    private TextView tvType, tvLocation, tvTicket, tvStatus, tvDate, tvTitle, tvDescription;
    private HomeOwnerRentersModel currentUser;
    private ScrollView homeOwnersRentersLayout, adminLayout;
    private Spinner updateStatusSpinner;
    private EditText remarksET;
    private Button saveUpdateBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grievance_clicked);

        Intent data = getIntent();
        theTitle = data.getStringExtra("title");
        theDescription = data.getStringExtra("description");
        theType = data.getStringExtra("type");
        theLocation = data.getStringExtra("location");
        theStatus = data.getStringExtra("status");
        theDate = data.getStringExtra("date");
        theTicket = data.getStringExtra("ticket");
        incidentReportID = data.getStringExtra("incidentReportID");

        tvType = findViewById(R.id.tvType);
        tvLocation = findViewById(R.id.tvLocation);
        tvTicket = findViewById(R.id.tvTicket);
        tvStatus = findViewById(R.id.tvStatus);
        tvDate = findViewById(R.id.tvDate);
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        updateStatusSpinner = findViewById(R.id.updateStatusSpinner);
        remarksET = findViewById(R.id.remarksET);
        saveUpdateBtn = findViewById(R.id.saveUpdateBtn);

        homeOwnersRentersLayout = findViewById(R.id.homeOwnersRentersLayout);
        adminLayout = findViewById(R.id.adminLayout);
        currentUser = UserSession.getInstance().getCurrentUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        displayDatas();
        loadUIBasedOnRole();
        setupSpinner();

        saveUpdateBtn.setOnClickListener(d -> {
            updateStatus();
        });
    }
    private void setupSpinner() {
        List<String> incidentTypes = Arrays.asList(
                "Update Status",
                "Under Investigation",
                "Resolved"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                incidentTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        updateStatusSpinner.setAdapter(adapter);
    }
    private void updateStatus() {
        String theStatus = "";

        String selectedStatus = updateStatusSpinner
                .getSelectedItem()
                .toString();

        if(selectedStatus.equals("Update Status")) {
            Toast.makeText(this, "Select status", Toast.LENGTH_SHORT).show();
        } else if (selectedStatus.equals("Under Investigation")) {
            theStatus = "under_investigation";
        } else if (selectedStatus.equals("Resolved")) {
            theStatus = "resolved";
        }

        String remarks = remarksET
                .getText()
                .toString()
                .trim();

        String dateTime = new java.text.SimpleDateFormat(
                "MMMM dd, yyyy • hh:mm a",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        setLoadingState();
        FirebaseGrievanceManager.updateGrievanceStatus(
                incidentReportID,
                theStatus,
                remarks,
                dateTime,
                new UpdateGrievanceStatusCallback() {
                    @Override
                    public void onSuccess(String message) {

                        Toast.makeText(
                                GrievanceClicked.this,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();
                        setNormalState();
                    }

                    @Override
                    public void onFailure(String error) {

                        Toast.makeText(
                                GrievanceClicked.this,
                                error,
                                Toast.LENGTH_SHORT
                        ).show();
                        setNormalState();
                    }
                });
    }
    private void loadUIBasedOnRole() {
        if(currentUser.getRole().equals("Home Owners") || currentUser.getRole().equals("Renters")) {
            homeOwnersRentersLayout.setVisibility(View.VISIBLE);
            adminLayout.setVisibility(View.GONE);
        } else {
            homeOwnersRentersLayout.setVisibility(View.GONE);
            adminLayout.setVisibility(View.VISIBLE);
        }
    }
    private void displayDatas() {
        tvType.setText(theType);
        tvLocation.setText(theLocation);
        tvTicket.setText(theTicket);
        tvStatus.setText(theStatus);
        tvDate.setText(theDate);
        tvTitle.setText(theTitle);
        tvDescription.setText(theDescription);
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