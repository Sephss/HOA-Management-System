package com.example.hoamanagementsystem.Modules;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.R;

import java.util.Arrays;
import java.util.List;

public class SubmitFinanceRequest extends AppCompatActivity {
    private Spinner reportTypeSpinner;
    private EditText purposeET, additionalRemarksET;
    private Button submitRequestBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_finance_request);

        reportTypeSpinner = findViewById(R.id.reportTypeSpinner);
        purposeET = findViewById(R.id.purposeET);
        additionalRemarksET = findViewById(R.id.additionalRemarksET);
        submitRequestBtn = findViewById(R.id.submitRequestBtn);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupSpinner();
    }
    private void setupSpinner() {

        List<String> maintenanceTypes = Arrays.asList(
                "Select Financial Request Type",
                "Statement of Account",
                "Official Receipt",
                "Payment Verification",
                "Billing Concern",
                "Assessment Fee Inquiry",
                "Payment Plan Request",
                "Payment Extension Request",
                "Refund Request",
                "Late Fee Concern",
                "Overpayment Concern",
                "Account Balance Inquiry",
                "Special Assessment Inquiry",
                "Ownership Transfer Request",
                "Deposit Refund Request",
                "Expense Reimbursement",
                "Financial Records Request",
                "Others"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                maintenanceTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        reportTypeSpinner.setAdapter(adapter);
    }
}