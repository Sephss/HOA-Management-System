package com.example.hoamanagementsystem.Modules;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDocumentsManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseNotificationManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateNotificationCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateDocumentStatusCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Model.NotificationModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class RequestDocumensClicked extends AppCompatActivity {
    private String theRole, theUid, theFullName, theEmail, theBlock, theLot, theStreet, theLavanyaPhaseType, theImage, theDocumentType, theDocumentPurpose, theDateSubmitted, theDocumentCategory, theDocumentStatus, documentUnderReviewDate, documentApprovedDate, documentRejectedDate, documentPendingDate;
    private TextView documentType, documentPurpose, dateSubmitted, documentCategory, documentStatus, documentRequestor, documentRequestorLocation;
    private String requestID;
    private LinearLayout requestSubmittedLayout, requestUnderReviewLayout, requestApprovedLayout, requestRejectedLayout, requestCancelledLayout, adminRemarksLayout;
    private ScrollView adminLayout, homeOwnersRentersLayout;
    private TextView requestTimeline;
    private TextView submittedDT, underReviewDT, approvedDT, rejectedDT, cancelledDT;
    private String theCurrentLoggedInUserID;
    // Admin
    private LinearLayout underReviewBtn, approveBtn, rejectBtn;
    private Button saveUpdateBtn, cancelRequestBtn;
    private EditText remarksET, linkET;
    private String setTheStatus = "none";
    private TextView underReviewTV, approveTV, rejectedTV;
    private String adminLink, adminRemarks;
    private TextView linkTV, remarksTV;
    private ImageView backBtn;
    private String theUserRole;

    private HomeOwnerRentersModel currentUser;

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_request_documens_clicked);
        currentUser = UserSession.getInstance().getCurrentUser();

        documentType = findViewById(R.id.documentType);
        documentPurpose = findViewById(R.id.documentPurpose);
        dateSubmitted = findViewById(R.id.dateSubmitted);
        documentCategory = findViewById(R.id.documentCategory);
        documentStatus = findViewById(R.id.documentStatus);
        documentRequestor = findViewById(R.id.documentRequestor);
        documentRequestorLocation = findViewById(R.id.documentRequestorLocation);

        submittedDT = findViewById(R.id.submittedDT);
        underReviewDT = findViewById(R.id.underReviewDT);
        approvedDT = findViewById(R.id.approvedDT);
        rejectedDT = findViewById(R.id.rejectedDT);
        cancelledDT = findViewById(R.id.cancelledDT);

        theUserRole = currentUser.getRole();
        if(theUserRole.equals("Home Owners") || theUserRole.equals("Renters")) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }

        backBtn= findViewById(R.id.backBtn);

        adminLayout = findViewById(R.id.adminLayout);
        homeOwnersRentersLayout = findViewById(R.id.homeOwnersRentersLayout);
        requestTimeline = findViewById(R.id.textView2);

        requestSubmittedLayout = findViewById(R.id.requestSubmittedLayout);
        requestUnderReviewLayout = findViewById(R.id.requestUnderReviewLayout);
        requestApprovedLayout = findViewById(R.id.requestApprovedLayout);
        requestRejectedLayout = findViewById(R.id.requestRejectedLayout);
        requestCancelledLayout = findViewById(R.id.requestCancelledLayout);
        adminRemarksLayout = findViewById(R.id.adminRemarksLayout);
        cancelRequestBtn = findViewById(R.id.cancelRequestBtn);
        theCurrentLoggedInUserID = FirebaseAuthManager.getCurrentUserUid();

        // admin
        underReviewBtn = findViewById(R.id.underReviewBtn);
        approveBtn = findViewById(R.id.approveBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        saveUpdateBtn = findViewById(R.id.saveUpdateBtn);
        remarksET = findViewById(R.id.remarksET);
        linkET = findViewById(R.id.linkET);
        underReviewTV = findViewById(R.id.underReviewTV);
        approveTV = findViewById(R.id.approveTV);
        rejectedTV = findViewById(R.id.rejectedTV);
        linkTV = findViewById(R.id.linkTV);
        remarksTV = findViewById(R.id.remarksTV);


        Intent data = getIntent();
        theRole = data.getStringExtra("role");
        theUid = data.getStringExtra("uid");
        theFullName = data.getStringExtra("name");
        theEmail = data.getStringExtra("email");
        theBlock = data.getStringExtra("block");
        theLot = data.getStringExtra("lot");
        theStreet = data.getStringExtra("street");
        theLavanyaPhaseType = data.getStringExtra("lavanyaPhaseType");
        theImage = data.getStringExtra("image");

        theDocumentType = data.getStringExtra("documentType");
        theDocumentPurpose = data.getStringExtra("documentPurpose");
        theDateSubmitted = data.getStringExtra("documentDateSubmitted");
        theDocumentCategory = data.getStringExtra("documentCategory");
        theDocumentStatus = data.getStringExtra("documentStatus");

        documentUnderReviewDate = data.getStringExtra("documentUnderReviewDate");
        documentApprovedDate = data.getStringExtra("documentApprovedDate");
        documentRejectedDate = data.getStringExtra("documentRejectedDate");
        documentPendingDate = data.getStringExtra("documentPendingDate");

        adminLink = data.getStringExtra("adminLink");
        adminRemarks = data.getStringExtra("adminRemarks");

        requestID = data.getStringExtra("requestID");



        setupData();
        setupUIPerRole();
        setupTimeline();
        setupDateTexts();
        setUpUIStateAdmin();

        backBtn.setOnClickListener(s -> {
            finish();
        });

        saveUpdateBtn.setOnClickListener(s -> {
            setUpStatusUpdate();
        });

        cancelRequestBtn.setOnClickListener(v -> {
            new AlertDialog.Builder(this)
                    .setTitle("Cancel Request")
                    .setMessage("Are you sure you want to cancel this request?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        String dateTime =
                                new SimpleDateFormat(
                                        "MMMM dd, yyyy • hh:mm a",
                                        Locale.getDefault()
                                ).format(new Date());

                        setTheStatus = "cancelled";


                         cancelTheRequest();
                        cancelledDT.setText(dateTime);
                        requestCancelledLayout.setVisibility(View.VISIBLE);
                        cancelRequestBtn.setVisibility(View.GONE);

                        Toast.makeText(this, "Request cancelled.", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                    .show();
        });

        linkTV.setOnClickListener(d -> {
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(adminLink));
            startActivity(intent);
        });

        underReviewBtn.setOnClickListener(v -> {
            setTheStatus = "under_review";
            underReviewBtn.setBackgroundResource(R.drawable.rounded_background);
            approveBtn.setBackgroundResource(R.drawable.document_request_bg);
            rejectBtn.setBackgroundResource(R.drawable.document_request_bg);

            underReviewTV.setTextColor(ContextCompat.getColor(this, R.color.white));
            approveTV.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
            rejectedTV.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
        });
        approveBtn.setOnClickListener(v -> {
            setTheStatus = "approved";
            underReviewBtn.setBackgroundResource(R.drawable.document_request_bg);
            approveBtn.setBackgroundResource(R.drawable.rounded_background);
            rejectBtn.setBackgroundResource(R.drawable.document_request_bg);

            underReviewTV.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
            approveTV.setTextColor(ContextCompat.getColor(this, R.color.white));
            rejectedTV.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
        });
        rejectBtn.setOnClickListener(v -> {
            setTheStatus = "rejected";
            underReviewBtn.setBackgroundResource(R.drawable.document_request_bg);
            approveBtn.setBackgroundResource(R.drawable.document_request_bg);
            rejectBtn.setBackgroundResource(R.drawable.rounded_background);

            underReviewTV.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
            approveTV.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
            rejectedTV.setTextColor(ContextCompat.getColor(this, R.color.white));
        });
    }
    private void cancelTheRequest() {
        if(setTheStatus.equals("none")) {
            Toast.makeText(this, "Please select a status to update", Toast.LENGTH_SHORT).show();
            return;
        }


        String dateTime =
                new SimpleDateFormat(
                        "MMMM dd, yyyy • hh:mm a",
                        Locale.getDefault()
                ).format(new Date());


        FirebaseDocumentsManager.updateDocumentRequest(
                requestID,
                setTheStatus,
                "cancelled by user",
                "cancelled by user",
                dateTime,
                new UpdateDocumentStatusCallback() {
                    @Override
                    public void onSuccess(String message) {

                        Toast.makeText(
                                RequestDocumensClicked.this,
                                message,
                                Toast.LENGTH_SHORT
                        ).show();

                        finish();
                    }

                    @Override
                    public void onFailure(String error) {

                        Toast.makeText(
                                RequestDocumensClicked.this,
                                error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
    private void setUpStatusUpdate() {
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

        if(setTheStatus.equals("none")) {
            Toast.makeText(this, "Please select a status to update", Toast.LENGTH_SHORT).show();
            return;
        }

        String remarks =
                remarksET.getText().toString().trim();

        String link =
                linkET.getText().toString().trim();

        String dateTime =
                new SimpleDateFormat(
                        "MMMM dd, yyyy • hh:mm a",
                        Locale.getDefault()
                ).format(new Date());

        if(remarks.isEmpty()) {
            remarksET.setError("Please enter a remarks");
            remarksET.requestFocus();
            return;
        }

        if (!link.isEmpty()) {

            Uri uri = Uri.parse(link);

            boolean isValid =
                    (uri.getScheme() != null) &&
                            (uri.getHost() != null) &&
                            (uri.getScheme().equals("http") || uri.getScheme().equals("https"));

            if (!isValid) {
                linkET.setError("Enter a valid URL starting with http:// or https://");
                linkET.requestFocus();
                return;
            }
        }

        String notifMessage;

        switch (setTheStatus.toLowerCase()) {

            case "under_review":
                notifMessage = "Your " + theDocumentType + " request is now under review.";
                break;

            case "approved":
                notifMessage = "Your " + theDocumentType + " request has been approved.";
                break;

            case "rejected":
                notifMessage = "Your " + theDocumentType + " request has been rejected. Please review the remarks for more information.";
                break;


            default:
                notifMessage = "Your document request has been updated.";
                break;
        }

        setLoadingState();
        FirebaseDocumentsManager.updateDocumentRequest(
                requestID,
                setTheStatus,
                remarks,
                link,
                dateTime,
                new UpdateDocumentStatusCallback() {
                    @Override
                    public void onSuccess(String message) {

                        NotificationModel data = new NotificationModel("", theUid,"", remarks, theDocumentType, setTheStatus, currentDate, currentTime, requestID, "no", theTimestamp, notifMessage);

                        FirebaseNotificationManager.createNotification(data, new CreateNotificationCallback() {
                            @Override
                            public void onSuccess(String success) {
                                Toast.makeText(
                                        RequestDocumensClicked.this,
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
                        setNormalState();
                        Toast.makeText(
                                RequestDocumensClicked.this,
                                error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
    private void setUpUIStateAdmin() {

        if(theDocumentStatus.equals("under_review")) {
            underReviewBtn.setEnabled(false);
            underReviewBtn.setAlpha(0.5f);

            underReviewBtn.setBackgroundResource(R.drawable.rounded_background);
            underReviewTV.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if(theDocumentStatus.equals("approved")) {
            approveBtn.setEnabled(false);
            approveBtn.setAlpha(0.5f);
            underReviewBtn.setEnabled(false);
            underReviewBtn.setAlpha(0.5f);
            rejectBtn.setEnabled(false);
            rejectBtn.setAlpha(0.5f);

            approveBtn.setBackgroundResource(R.drawable.rounded_background);
            approveTV.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if(theDocumentStatus.equals("rejected")) {
            approveBtn.setEnabled(false);
            approveBtn.setAlpha(0.5f);
            underReviewBtn.setEnabled(false);
            underReviewBtn.setAlpha(0.5f);
            rejectBtn.setEnabled(false);
            rejectBtn.setAlpha(0.5f);

            rejectBtn.setBackgroundResource(R.drawable.rounded_background);
            rejectedTV.setTextColor(ContextCompat.getColor(this, R.color.white));
        } else if (theDocumentStatus.equals("cancelled")) {
            approveBtn.setEnabled(false);
            approveBtn.setAlpha(0.5f);
            underReviewBtn.setEnabled(false);
            underReviewBtn.setAlpha(0.5f);
            rejectBtn.setEnabled(false);
            rejectBtn.setAlpha(0.5f);

        }
    }
    private void setupDateTexts() {
        submittedDT.setText(documentPendingDate);
        underReviewDT.setText(documentUnderReviewDate);
        approvedDT.setText(documentApprovedDate);
        rejectedDT.setText(documentRejectedDate);
    }
    private boolean isNotEmpty(String value) {
        return value != null && !value.trim().isEmpty();
    }

    private void setupTimeline() {

        requestSubmittedLayout.setVisibility(View.GONE);
        requestUnderReviewLayout.setVisibility(View.GONE);
        requestApprovedLayout.setVisibility(View.GONE);
        requestRejectedLayout.setVisibility(View.GONE);
        requestCancelledLayout.setVisibility(View.GONE);
        adminRemarksLayout.setVisibility(View.GONE);

        remarksTV.setText(adminRemarks);

        if(adminLink == null || adminLink.trim().isEmpty()) {
            linkTV.setVisibility(View.GONE);
        } else {
            linkTV.setText("View attachment");
            linkTV.setVisibility(View.VISIBLE);
        }

        if (isNotEmpty(documentPendingDate)) {
            requestSubmittedLayout.setVisibility(View.VISIBLE);
        }

        if ("pending".equalsIgnoreCase(theDocumentStatus)) {

            // Only submitted

        } else if ("under_review".equalsIgnoreCase(theDocumentStatus)) {

            if (isNotEmpty(documentUnderReviewDate)) {
                requestUnderReviewLayout.setVisibility(View.VISIBLE);
            }
            adminRemarksLayout.setVisibility(View.VISIBLE);

        } else if ("approved".equalsIgnoreCase(theDocumentStatus)) {

            if (isNotEmpty(documentUnderReviewDate)) {
                requestUnderReviewLayout.setVisibility(View.VISIBLE);
            }

            if (isNotEmpty(documentApprovedDate)) {
                requestApprovedLayout.setVisibility(View.VISIBLE);
            }

            adminRemarksLayout.setVisibility(View.VISIBLE);

        } else if ("rejected".equalsIgnoreCase(theDocumentStatus)) {

            if (isNotEmpty(documentUnderReviewDate)) {
                requestUnderReviewLayout.setVisibility(View.VISIBLE);
            }

            if (isNotEmpty(documentRejectedDate)) {
                requestRejectedLayout.setVisibility(View.VISIBLE);
            }

            adminRemarksLayout.setVisibility(View.VISIBLE);

        } else if ("cancelled".equalsIgnoreCase(theDocumentStatus)) {

            if (isNotEmpty(documentUnderReviewDate)) {
                requestUnderReviewLayout.setVisibility(View.VISIBLE);
            }

            requestCancelledLayout.setVisibility(View.VISIBLE);
        }

    }
    private void setupUIPerRole() {

        String role = currentUser.getRole();

        if(role.equals("Home Owners") || role.equals("Renters")) {
            requestTimeline.setVisibility(View.VISIBLE);
            homeOwnersRentersLayout.setVisibility(View.VISIBLE);
            adminLayout.setVisibility(View.GONE);
        } else {
            adminLayout.setVisibility(View.VISIBLE);
            requestTimeline.setVisibility(View.GONE);
            homeOwnersRentersLayout.setVisibility(View.GONE);
        }

    }
    private void setupData() {
        documentType.setText(theDocumentType);
        documentPurpose.setText(theDocumentPurpose);
        dateSubmitted.setText(theDateSubmitted);
        documentCategory.setText(theDocumentCategory);
        documentRequestor.setText(theFullName);
        documentRequestorLocation.setText(theBlock + " " + theLot + " " + theStreet);

        switch (theDocumentStatus) {

            case "pending":
                documentStatus.setBackgroundResource(
                        R.drawable.pending_document_request
                );
                documentStatus.setTextColor(
                        this.getColor(R.color.darkyellow)

                );
                documentStatus.setText("Pending");

                cancelRequestBtn.setVisibility(View.VISIBLE);
                break;

            case "under review":
                documentStatus.setBackgroundResource(
                        R.drawable.under_review_color
                );
                documentStatus.setTextColor(
                        this.getColor(R.color.darkyellow)
                );
                documentStatus.setText("Under Review");
                break;

            case "approved":
            case "approved and ready":
                documentStatus.setBackgroundResource(
                        R.drawable.approved_and_ready_color
                );
                documentStatus.setTextColor(
                        this.getColor(R.color.green)
                );
                documentStatus.setText("Approved");
                break;

            case "rejected":
                documentStatus.setBackgroundResource(
                        R.drawable.rejected_color
                );
                documentStatus.setTextColor(
                        this.getColor(R.color.rejectedtext)
                );
                documentStatus.setText("Rejected");
                break;

            case "cancelled":
                documentStatus.setBackgroundResource(
                        R.drawable.cancelled_color
                );
                documentStatus.setTextColor(
                        this.getColor(R.color.black)
                );
                documentStatus.setText("Cancelled");
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