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

import com.example.hoamanagementsystem.FirebaseServices.FirebaseGrievanceManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseNotificationManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateNotificationCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateGrievanceStatusCallback;
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

public class GrievanceClicked extends AppCompatActivity {
    private String theTitle, theDescription, theType, theLocation, theStatus, theReporterID, theDate, theTicket,incidentReportID, theUnderInvestigationDate, theResolvedDate, theAdminRemarks;
    private TextView tvType, tvLocation, tvTicket, tvStatus, tvDate, tvTitle, tvDescription;
    private HomeOwnerRentersModel currentUser;
    private ScrollView homeOwnersRentersLayout, adminLayout;
    private Spinner updateStatusSpinner;
    private EditText remarksET;
    private Button saveUpdateBtn;
    private String notifStatus = "none";
    private ImageView backBtn;

    private TextView viewImageBtn, closeBtn;
    private ImageView imageDisplay;
    private String theIncidentImage;
    private String theUserRole;

    private LinearLayout adminRemarksLayout, resolvedLayout, underInvestigationLayout, reportFiledLayout;
    private TextView dateFiled, underInvestigationTV, resolvedTV, remarksTV;

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        theUnderInvestigationDate = data.getStringExtra("dateUnderInvestigation");
        theResolvedDate = data.getStringExtra("dateResolved");
        theAdminRemarks = data.getStringExtra("adminRemarks");
        theReporterID = data.getStringExtra("reporterID");

        theIncidentImage = data.getStringExtra("incidentImage");

        imageDisplay = findViewById(R.id.imageDisplay);
        viewImageBtn = findViewById(R.id.viewImageBtn);
        closeBtn = findViewById(R.id.closeBtn);

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

        adminRemarksLayout = findViewById(R.id.adminRemarksLayout);
        resolvedLayout = findViewById(R.id.resolvedLayout);
        underInvestigationLayout = findViewById(R.id.underInvestigationLayout);
        reportFiledLayout = findViewById(R.id.reportFiledLayout);
        dateFiled = findViewById(R.id.dateFiled);
        underInvestigationTV = findViewById(R.id.underInvestigationTV);
        resolvedTV = findViewById(R.id.resolvedTV);
        remarksTV = findViewById(R.id.remarksTV);

        backBtn = findViewById(R.id.backBtn);

        homeOwnersRentersLayout = findViewById(R.id.homeOwnersRentersLayout);
        adminLayout = findViewById(R.id.adminLayout);
        currentUser = UserSession.getInstance().getCurrentUser();

        theUserRole = currentUser.getRole();
        if(theUserRole.equals("Home Owners") || theUserRole.equals("Renters")) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }


        displayDatas();
        loadUIBasedOnRole();
        setupSpinner();
        setupProgressHomeowners();



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

        backBtn.setOnClickListener(d -> {
            finish();
        });
    }
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }
    private void setupProgressHomeowners() {

        // Hide everything first
        reportFiledLayout.setVisibility(View.GONE);
        underInvestigationLayout.setVisibility(View.GONE);
        resolvedLayout.setVisibility(View.GONE);
        adminRemarksLayout.setVisibility(View.GONE);

        remarksTV.setText(theAdminRemarks);

        // Report Filed
        if (isNotEmpty(theDate)) {
            reportFiledLayout.setVisibility(View.VISIBLE);
            dateFiled.setText(theDate);
        }

        if ("pending".equalsIgnoreCase(theStatus)) {

            // Only Report Filed

        } else if ("under_investigation".equalsIgnoreCase(theStatus)) {

            if (isNotEmpty(theUnderInvestigationDate)) {
                underInvestigationLayout.setVisibility(View.VISIBLE);
                underInvestigationTV.setText(theUnderInvestigationDate);
            }

        } else if ("resolved".equalsIgnoreCase(theStatus)) {

            if (isNotEmpty(theUnderInvestigationDate)) {
                underInvestigationLayout.setVisibility(View.VISIBLE);
                underInvestigationTV.setText(theUnderInvestigationDate);
            }

            if (isNotEmpty(theResolvedDate)) {
                resolvedLayout.setVisibility(View.VISIBLE);
                resolvedTV.setText(theResolvedDate);
            }

            if (isNotEmpty(theAdminRemarks)) {
                adminRemarksLayout.setVisibility(View.VISIBLE);
            }
        }
    }
    private void setupSpinner() {

        List<String> statusList = Arrays.asList(
                "Update Status",
                "Under Investigation",
                "Resolved"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(
                this,
                R.layout.spinner_item,
                statusList
        ) {

            @Override
            public boolean isEnabled(int position) {

                if (theStatus == null) {
                    return true;
                }

                switch (theStatus) {

                    case "under_investigation":
                        // Disable only "Update Status"
                        return position != 0;

                    case "resolved":
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
        } else if (selectedStatus.equals("Under Investigation")) {
            theStatus = "under_investigation";
            notifStatus = "under_investigation";
        } else if (selectedStatus.equals("Resolved")) {
            theStatus = "resolved";
            notifStatus = "resolved";
        }


        String remarks = remarksET
                .getText()
                .toString()
                .trim();

        String dateTime = new java.text.SimpleDateFormat(
                "MMMM dd, yyyy • hh:mm a",
                java.util.Locale.getDefault()
        ).format(new java.util.Date());

        String notifMessage;

        switch (theStatus.toLowerCase()) {

            case "under_investigation":
                notifMessage = "Your incident report is now under investigation.";
                break;

            case "resolved":
                notifMessage = "Your incident report has been marked as resolved.";
                break;

            default:
                notifMessage = "Your incident report has been updated.";
                break;
        }

        setLoadingState();
        FirebaseGrievanceManager.updateGrievanceStatus(
                incidentReportID,
                theStatus,
                remarks,
                dateTime,
                new UpdateGrievanceStatusCallback() {
                    @Override
                    public void onSuccess(String message) {


                        NotificationModel data = new NotificationModel("", theReporterID,"", remarks, theType, notifStatus, currentDate, currentTime, incidentReportID, "no", theTimestamp, notifMessage);

                        FirebaseNotificationManager.createNotification(data, new CreateNotificationCallback() {
                            @Override
                            public void onSuccess(String success) {
                                Toast.makeText(
                                        GrievanceClicked.this,
                                        message,
                                        Toast.LENGTH_SHORT
                                ).show();
                                setNormalState();
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

        Picasso.get().load(theIncidentImage).into(imageDisplay);

        switch(theStatus) {
            case "pending":
                tvStatus.setBackgroundResource(
                        R.drawable.pending_document_request
                );

                tvStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.darkyellow)
                );

                tvStatus.setText("Pending");
                break;

            case "under_investigation":
                tvStatus.setBackgroundResource(
                        R.drawable.under_review_color
                );

                tvStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.darkyellow)
                );

                tvStatus.setText("Under Investigation");
                break;

            case "resolved":
                tvStatus.setBackgroundResource(
                        R.drawable.approved_and_ready_color
                );

                tvStatus.setTextColor(
                        ContextCompat.getColor(this, R.color.green)
                );

                tvStatus.setText("Resolved");
                break;

        }
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