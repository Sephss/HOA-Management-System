package com.example.hoamanagementsystem;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class SignupPage extends AppCompatActivity {
    private TextView loginLink;
    private EditText firstNameET, lastNameET, middleNameET, phoneNumberET, emailAddressET, passwordET;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page);

        loginLink = findViewById(R.id.loginLink);

        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);
        middleNameET = findViewById(R.id.middleNameET);
        phoneNumberET = findViewById(R.id.phoneNumberET);
        emailAddressET = findViewById(R.id.emailAddressET);
        passwordET = findViewById(R.id.passwordET);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loginLink.setOnClickListener(f -> {
            finish();
        });
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
    private void homeOwnerRenterGetData() {
        String firstname = firstNameET.getText().toString();
        String middlename = middleNameET.getText().toString();
        String lastname = lastNameET.getText().toString();
        String phonenumber = phoneNumberET.getText().toString();
        String email = emailAddressET.getText().toString();
        String password = passwordET.getText().toString();

        if(firstname.isEmpty()) {
            firstNameET.setError("Firstname is required");
            firstNameET.requestFocus();
            return;
        }

        if(middlename.isEmpty()) {
            middleNameET.setError("Middlename is required");
            middleNameET.requestFocus();
            return;
        }

        if(lastname.isEmpty()) {
            lastNameET.setError("Lastname is required");
            lastNameET.requestFocus();
            return;
        }

        if(phonenumber.isEmpty()) {
            phoneNumberET.setError("Phone number is required");
            phoneNumberET.requestFocus();
            return;
        }

        if(email.isEmpty()) {
            emailAddressET.setError("Email address is required");
            emailAddressET.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passwordET.setError("Password is required");
            passwordET.requestFocus();
            return;

        }
        Intent intent = new Intent(SignupPage.this, SignupPageFinish.class);
        intent.putExtra("firstname", firstname);
        intent.putExtra("middlename", middlename);
        intent.putExtra("lastname", lastname);
        intent.putExtra("phonenumber", phonenumber);
        intent.putExtra("email", email);
        intent.putExtra("password", password);
        startActivity(intent);
    }
}