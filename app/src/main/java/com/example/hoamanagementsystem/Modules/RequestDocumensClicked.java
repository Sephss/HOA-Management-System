package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.R;

public class RequestDocumensClicked extends AppCompatActivity {
    private String theRole, theUid, theFullName, theEmail, theBlock, theLot, theStreet, theLavanyaPhaseType, theImage, theDocumentType, theDocumentPurpose, theDateSubmitted, theDocumentCategory, theDocumentStatus, documentUnderReviewDate, documentApprovedDate, documentRejectedDate, documentPendingDate;
    private TextView documentType, documentPurpose, dateSubmitted, documentCategory, documentStatus, documentRequestor, documentRequestorLocation;

    private LinearLayout requestSubmittedLayout, requestUnderReviewLayout, requestApprovedLayout, requestRejectedLayout, requestCancelledLayout, adminRemarksLayout;
    private ScrollView adminLayout, homeOwnersRentersLayout;
    private TextView requestTimeline;
    private TextView submittedDT, underReviewDT, approvedDT, rejectedDT, cancelledDT;

    // Admin
    private LinearLayout underReviewBtn, approveBtn, rejectBtn;
    private Button saveUpdateBtn;
    private EditText remarksET, linkET;

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

        // admin
        underReviewBtn = findViewById(R.id.underReviewBtn);
        approveBtn = findViewById(R.id.approveBtn);
        rejectBtn = findViewById(R.id.rejectBtn);
        saveUpdateBtn = findViewById(R.id.saveUpdateBtn);
        remarksET = findViewById(R.id.remarksET);
        linkET = findViewById(R.id.linkET);

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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupData();
        setupUIPerRole();
        setupTimeline();
        setupDateTexts();
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

        if (isNotEmpty(documentPendingDate)) {
            requestSubmittedLayout.setVisibility(View.VISIBLE);
        }

        if ("pending".equalsIgnoreCase(theDocumentStatus)) {

            // Only submitted

        } else if ("under_review".equalsIgnoreCase(theDocumentStatus)) {

            if (isNotEmpty(documentUnderReviewDate)) {
                requestUnderReviewLayout.setVisibility(View.VISIBLE);
            }

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
        if(theRole.equals("Home Owners") || theRole.equals("Renters")) {
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