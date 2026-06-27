package com.example.hoamanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupPageFinish extends AppCompatActivity {
    private TextView backLink;
    private EditText blockET, lotET, steetET;
    private Spinner residentTypeSpinner, lavanyaPhaseTypeSpinner;
    private String firstname, middlename, lastname, phonenumber, email, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page_finish);

        backLink = findViewById(R.id.backLink);
        blockET = findViewById(R.id.blockET);
        lotET = findViewById(R.id.lotET);
        steetET = findViewById(R.id.streetET);

        residentTypeSpinner = findViewById(R.id.residentTypeSpinner);
        lavanyaPhaseTypeSpinner = findViewById(R.id.lavanyaPhaseTypeSpinner);

        // GET THE VALUES FROM SIGNUP PAGE
        Intent intent = getIntent();
         firstname = intent.getStringExtra("firstname");
         middlename = intent.getStringExtra("middlename");
         lastname = intent.getStringExtra("lastname");
         phonenumber = intent.getStringExtra("phonenumber");
         email = intent.getStringExtra("email");
         password = intent.getStringExtra("password");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setUpSpinners();
        backLink.setOnClickListener(s -> {
            finish();
        });
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
    private void signUpUser () {
        String block = blockET.getText().toString();
        String lot = lotET.getText().toString();
        String street = steetET.getText().toString();
        String residentType = residentTypeSpinner.getSelectedItem().toString();
        String lavanyaPhaseType = lavanyaPhaseTypeSpinner.getSelectedItem().toString();

        if(block.isEmpty()) {
            blockET.setError("Block is required");
            blockET.requestFocus();
            return;
        }
        if(lot.isEmpty()) {
            lotET.setError("Lot is required");
            lotET.requestFocus();
            return;
        }
        if(street.isEmpty()) {
            steetET.setError("Street is required");
            steetET.requestFocus();
            return;
        }
        if(residentType.equals("Select")) {
            Toast.makeText(this, "Resident Type is required", Toast.LENGTH_SHORT).show();
            residentTypeSpinner.requestFocus();
            return;
        }
        if(lavanyaPhaseType.equals("Select")) {
            Toast.makeText(this, "Lavanya Phase Type is required", Toast.LENGTH_SHORT).show();
            lavanyaPhaseTypeSpinner.requestFocus();
            return;
        }
    }
    private void setUpSpinners() {
        String[] residentTypes = {
                "Select",
                "Home Owners",
                "Renters"
        };

        ArrayAdapter<String> residentTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                residentTypes
        );

        residentTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        residentTypeSpinner.setAdapter(residentTypeAdapter);


// Lavanya Phase Spinner
        String[] lavanyaPhases = {
                "Select",
                "3A",
                "3B",
                "3C"
        };

        ArrayAdapter<String> lavanyaPhaseAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                lavanyaPhases
        );

        lavanyaPhaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        lavanyaPhaseTypeSpinner.setAdapter(lavanyaPhaseAdapter);
    }
}