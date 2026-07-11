package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDocumentsManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateDocumentCallback;
import com.example.hoamanagementsystem.Model.DocumentRequestModel;
import com.example.hoamanagementsystem.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class RequestDocuments extends AppCompatActivity {
    private Spinner requestCategorySpinner, documentTypeSpinner;
    private EditText purposeEditText, remarksEditText;
    private LinearLayout documentTypeLayout;
    private Button submitRequestButton;
    private String uid;

    private String theRole, theUid, theFullName, theEmail, theBlock, theLot, theStreet, theLavanyaPhaseType, theImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_request_documents);

        requestCategorySpinner = findViewById(R.id.requestCategorySpinner);
        documentTypeSpinner = findViewById(R.id.documentTypeSpinner);
        purposeEditText = findViewById(R.id.purposeEditText);
        remarksEditText = findViewById(R.id.remarksEditText);
        documentTypeLayout = findViewById(R.id.documentTypeLayout);
        submitRequestButton = findViewById(R.id.submitRequestButton);

        uid = FirebaseAuthManager.getCurrentUserUid();

        Intent datas = getIntent();
        theRole = datas.getStringExtra("role");
        theUid = datas.getStringExtra("uid");
        theFullName = datas.getStringExtra("name");
        theEmail = datas.getStringExtra("email");
        theBlock = datas.getStringExtra("block");
        theLot = datas.getStringExtra("lot");
        theStreet = datas.getStringExtra("street");
        theLavanyaPhaseType = datas.getStringExtra("lavanyaPhaseType");
        theImage = datas.getStringExtra("image");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpSpinner();

        submitRequestButton.setOnClickListener(g -> {
            submitDocumentRequest();
        });
    }
    private void submitDocumentRequest() {
        String requestCategory = requestCategorySpinner.getSelectedItem().toString();
        String documentType = documentTypeSpinner.getSelectedItem().toString();
        String purpose = purposeEditText.getText().toString();
        String remarks = remarksEditText.getText().toString();

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
        long timestamp = System.currentTimeMillis();
        String theTimeStamp = String.valueOf(timestamp);
        if (requestCategory.equals("Select Request Category")) {
            Toast.makeText(this, "Please select a request category", Toast.LENGTH_SHORT).show();
            return;
        }
        if (documentType.equals("Select Document Type")) {
            Toast.makeText(this, "Please select a document type", Toast.LENGTH_SHORT).show();
            return;
        }
        if (purpose.isEmpty()) {
            Toast.makeText(this, "Please enter a purpose", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState();

        DocumentRequestModel details = new DocumentRequestModel(requestCategory, documentType, purpose, remarks, "", "", uid, theFullName, theImage, theEmail, theBlock, theLot, theStreet, theRole,theLavanyaPhaseType, "pending", "", currentDate, currentTime, theTimeStamp, "", "", "", "","", "","", "");

        FirebaseDocumentsManager.createDocumentRequest(details, new CreateDocumentCallback() {
            @Override
            public void onSuccess(String success) {
                navigateTo(SuccessDocumentRequestSubmitted.class);
                setNormalState();
                finish();
            }

            @Override
            public void onFailure(String failed) {
                Toast.makeText(RequestDocuments.this, failed, Toast.LENGTH_SHORT).show();
                setNormalState();
            }
        });

    }
    private void setUpSpinner() {

        List<String> requestCategories = new ArrayList<>();
        requestCategories.add("Select Request Category");
        requestCategories.add("Documents & Forms");
        requestCategories.add("Membership");
        requestCategories.add("Elecom");
        requestCategories.add("Finance");

        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                requestCategories
        );

        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        requestCategorySpinner.setAdapter(categoryAdapter);

        documentTypeLayout.setVisibility(View.GONE);

        requestCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    documentTypeLayout.setVisibility(View.GONE);
                    return;
                }

                documentTypeLayout.setVisibility(View.VISIBLE);

                List<String> documentTypes = new ArrayList<>();
                documentTypes.add("Select Document Type");

                String selectedCategory = parent.getItemAtPosition(position).toString();

                switch (selectedCategory) {

                    case "Documents & Forms":
                        documentTypes.add("Certificate of Residency");
                        documentTypes.add("HOA Clearance");
                        documentTypes.add("Certificate of Good Standing");
                        documentTypes.add("Construction Permit Form");
                        documentTypes.add("Renovation Permit Form");
                        documentTypes.add("Other Documents");
                        break;

                    case "Membership":
                        documentTypes.add("Membership Verification");
                        documentTypes.add("Membership Certificate");
                        documentTypes.add("Membership Record Copy");
                        documentTypes.add("Membership Application Form");
                        documentTypes.add("Other Membership Documents");
                        break;

                    case "Elecom":
                        documentTypes.add("Election Guidelines");
                        documentTypes.add("Candidate Application Form");
                        documentTypes.add("Candidate Requirements");
                        documentTypes.add("Election Schedule");
                        documentTypes.add("Election Results");
                        documentTypes.add("Other Election Documents");
                        break;

                    case "Finance":
                        documentTypes.add("Statement of Account");
                        documentTypes.add("Official Receipt");
                        documentTypes.add("Payment Verification");
                        documentTypes.add("Billing Concern");
                        documentTypes.add("Assessment Fee Inquiry");
                        documentTypes.add("Payment Plan Request");
                        documentTypes.add("Payment Extension Request");
                        documentTypes.add("Refund Request");
                        documentTypes.add("Late Fee Concern");
                        documentTypes.add("Overpayment Concern");
                        documentTypes.add("Account Balance Inquiry");
                        documentTypes.add("Special Assessment Inquiry");
                        documentTypes.add("Ownership Transfer Request");
                        documentTypes.add("Deposit Refund Request");
                        documentTypes.add("Expense Reimbursement");
                        documentTypes.add("Financial Records Request");
                        documentTypes.add("Others");
                        break;
                }

                ArrayAdapter<String> documentAdapter = new ArrayAdapter<>(
                        RequestDocuments.this,
                        android.R.layout.simple_spinner_item,
                        documentTypes
                );

                documentAdapter.setDropDownViewResource(
                        android.R.layout.simple_spinner_dropdown_item
                );

                documentTypeSpinner.setAdapter(documentAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }
    private void setLoadingState() {
        submitRequestButton.setEnabled(false);
        submitRequestButton.setAlpha(0.5f);
        submitRequestButton.setText("Submitting...");
    }

    private void setNormalState() {
        submitRequestButton.setEnabled(true);
        submitRequestButton.setAlpha(1f);
        submitRequestButton.setText("Submit Request");
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
}