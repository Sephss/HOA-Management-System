package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateDocumentStatusCallback;
import com.example.hoamanagementsystem.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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
    private Button saveUpdateBtn;
    private EditText remarksET, linkET;
    private String setTheStatus = "none";
    private TextView underReviewTV, approveTV, rejectedTV;
    private String adminLink, adminRemarks;
    private TextView linkTV, remarksTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_documens_clicked);

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

        adminLayout = findViewById(R.id.adminLayout);
        homeOwnersRentersLayout = findViewById(R.id.homeOwnersRentersLayout);
        requestTimeline = findViewById(R.id.textView2);

        requestSubmittedLayout = findViewById(R.id.requestSubmittedLayout);
        requestUnderReviewLayout = findViewById(R.id.requestUnderReviewLayout);
        requestApprovedLayout = findViewById(R.id.requestApprovedLayout);
        requestRejectedLayout = findViewById(R.id.requestRejectedLayout);
        requestCancelledLayout = findViewById(R.id.requestCancelledLayout);
        adminRemarksLayout = findViewById(R.id.adminRemarksLayout);

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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupData();
        setupUIPerRole();
        setupTimeline();
        setupDateTexts();
        setUpUIStateAdmin();

        saveUpdateBtn.setOnClickListener(s -> {
            setUpStatusUpdate();
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
    private void setUpStatusUpdate() {
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

        FirebaseDocumentsManager.updateDocumentRequest(
                requestID,
                setTheStatus,
                remarks,
                link,
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
            linkTV.setText(adminLink);
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
        // Determine which UI to display based on request ownership.
//
// Home Owners and Renters can only access their own requests,
// so their UID will always match the requesterID.
//
// HOA Officials (Admin, President, Secretary, Treasurer, etc.)
// can view requests submitted by other users, so their UID will
// not match the requesterID.
//
// If currentUserUid == requesterID:
//      Show Homeowner/Renter UI
//
// If currentUserUid != requesterID:
//      Show HOA Official UI
        if(theCurrentLoggedInUserID.equals(theUid)) {
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
        documentStatus.setText(theDocumentStatus);
        documentRequestor.setText(theFullName);
        documentRequestorLocation.setText(theBlock + " " + theLot + " " + theStreet);

    }
}