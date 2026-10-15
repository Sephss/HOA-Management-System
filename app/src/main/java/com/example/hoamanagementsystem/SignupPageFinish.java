package com.example.hoamanagementsystem;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
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

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.RegisterHomeownerRenterCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Modules.HomePage;
import com.example.hoamanagementsystem.Modules.ProfilePage;

public class SignupPageFinish extends AppCompatActivity {
    private TextView backLink;
    private EditText blockET, lotET, steetET;
    private Spinner residentTypeSpinner, lavanyaPhaseTypeSpinner;
    private String firstname, middlename, lastname, phonenumber, email, password;
    private Button signupBtn;
    private TextView resendEmailVerificationBtn, viewPrivacyNoticeLink;
    private CheckBox privacyConsentCheckbox, privacyConsentCheckboxDisagree;
    private boolean isUpdatingCheckboxes = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_signup_page_finish);

        backLink = findViewById(R.id.backLink);
        blockET = findViewById(R.id.blockET);
        lotET = findViewById(R.id.lotET);
        steetET = findViewById(R.id.streetET);

        signupBtn = findViewById(R.id.signupBtn);
        resendEmailVerificationBtn = findViewById(R.id.resendEmailVerificationBtn);
        viewPrivacyNoticeLink = findViewById(R.id.viewPrivacyNoticeLink);
        privacyConsentCheckbox = findViewById(R.id.privacyConsentCheckbox);
        privacyConsentCheckboxDisagree = findViewById(R.id.privacyConsentCheckboxDisagree);

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

            Insets systemBars = insets.getInsets(
                    WindowInsetsCompat.Type.systemBars()
            );

            Insets ime = insets.getInsets(
                    WindowInsetsCompat.Type.ime()
            );

            int bottom = Math.max(
                    systemBars.bottom,
                    ime.bottom
            );

            v.setPadding(
                    systemBars.left,
                    systemBars.top,
                    systemBars.right,
                    bottom
            );

            return insets;
        });
        setUpSpinners();
        setupPrivacyNotice();
        setupPrivacyConsent();
        backLink.setOnClickListener(s -> {
            finish();
        });
        signupBtn.setOnClickListener(g -> {
            signUpUser();
        });



        resendEmailVerificationBtn.setOnClickListener(v -> {
            resendEmailVerificationBtn.setEnabled(false); // prevent spam-clicking

            FirebaseAuthManager.resendEmailVerification(new RegisterHomeownerRenterCallback() {
                @Override
                public void onSuccess(String success) {
                    Toast.makeText(SignupPageFinish.this, success, Toast.LENGTH_SHORT).show();
                    startResendCooldown();
                }

                @Override
                public void onFailure(String failed) {
                    Toast.makeText(SignupPageFinish.this, failed, Toast.LENGTH_SHORT).show();
                    resendEmailVerificationBtn.setEnabled(true); // no cooldown on failure, let them retry
                }
            });
        });
    }
    private void startResendCooldown() {
        new CountDownTimer(10000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                long secondsLeft = millisUntilFinished / 1000;
                resendEmailVerificationBtn.setText("Resend in " + secondsLeft + "s");
            }

            @Override
            public void onFinish() {
                resendEmailVerificationBtn.setText("Resend email verification");
                resendEmailVerificationBtn.setEnabled(true);
            }
        }.start();
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

        setLoadingState();

        HomeOwnerRentersModel details = new HomeOwnerRentersModel(firstname, middlename, lastname, phonenumber, email, block, lot, street, residentType, lavanyaPhaseType, "", "none", "no", "no", "no", "no", "none", "none", "none");

        FirebaseAuthManager.signupUser(email, password, details, new RegisterHomeownerRenterCallback() {
            @Override
            public void onSuccess(String success) {
                resendEmailVerificationBtn.setVisibility(View.VISIBLE);
                setNormalState();
                showVerificationSentDialog(email);
            }

            @Override
            public void onFailure(String failed) {
                setNormalState();
                Toast.makeText(SignupPageFinish.this, failed, Toast.LENGTH_SHORT).show();
            }
        });
    }
    private void showVerificationSentDialog(String userEmail) {
        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_verify_email, null);

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView emailText = dialogView.findViewById(R.id.dialogEmail);
        emailText.setText(userEmail);

        Button okBtn = dialogView.findViewById(R.id.dialogOkBtn);
        okBtn.setOnClickListener(v -> {
            dialog.dismiss();
            Intent intent = new Intent(SignupPageFinish.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        dialog.show();
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
    private void setLoadingState() {
        signupBtn.setEnabled(false);
        signupBtn.setAlpha(0.5f);
        signupBtn.setText("Signing in...");
    }

    private void setNormalState() {
        signupBtn.setEnabled(true);
        signupBtn.setAlpha(1f);
        signupBtn.setText("Signin");
    }

    private void setupPrivacyNotice() {
        TextView viewPrivacyNoticeLink = findViewById(R.id.viewPrivacyNoticeLink);

        // Underline the clickable text
        viewPrivacyNoticeLink.setPaintFlags(
                viewPrivacyNoticeLink.getPaintFlags()
                        | android.graphics.Paint.UNDERLINE_TEXT_FLAG
        );

        viewPrivacyNoticeLink.setOnClickListener(v -> {
            String privacyNotice =
                    "Lavanya Phase 3 Resident Registration\n\n" +
                            "DATA PRIVACY NOTICE AND CONSENT\n\n" +

                            "The Lavanya Phase 3 Homeowners Association, Inc. " +
                            "(LP3HOA) is collecting the personal information provided " +
                            "in this form solely for the purpose of reconciling and " +
                            "updating the Association's official Masterlist of Homeowners " +
                            "and Residents, verifying membership records, facilitating " +
                            "official HOA communications, administering community services, " +
                            "and complying with applicable laws, the Association's By-Laws, " +
                            "and the regulations of the Department of Human Settlements " +
                            "and Urban Development (DSHUD).\n\n" +

                            "The personal information collected may include your name, " +
                            "address, contact details, household information, proof of " +
                            "ownership or occupancy, emergency contact information, and " +
                            "other information necessary for HOA administration.\n\n" +

                            "Your personal data shall be processed fairly and lawfully, " +
                            "kept confidential, and protected through appropriate " +
                            "organizational, physical, and technical security measures. " +
                            "The Association shall not disclose your personal information " +
                            "to unauthorized persons or entities except when required " +
                            "by law or when necessary for the legitimate functions " +
                            "of the Association.\n\n" +

                            "To comply with data minimization under the Data Privacy Act, " +
                            "LP3HOA will not be collecting information that is not " +
                            "necessary for HOA administration, such as:\n\n" +

                            "\u2022 Religion\n" +
                            "\u2022 Political affiliation\n" +
                            "\u2022 Employment details (unless needed for a specific HOA program)\n" +
                            "\u2022 Salary or income\n" +
                            "\u2022 Tax Identification Number (TIN)\n" +
                            "\u2022 Social Security System (SSS), GSIS, PhilHealth, " +
                            "or Pag-IBIG numbers\n" +
                            "\u2022 Bank account or credit card information\n" +
                            "\u2022 Medical information (unless voluntarily provided " +
                            "for emergency purposes)\n\n" +

                            "By completing and submitting this form, you acknowledge " +
                            "that you have read and understood this Privacy Notice and " +
                            "voluntarily consent to the collection, use, storage, and " +
                            "processing of your personal information by the Lavanya Phase 3 " +
                            "Homeowners Association, Inc. for the purposes stated above, " +
                            "in accordance with the Data Privacy Act of 2012 " +
                            "(Republic Act No. 10173) and its implementing rules " +
                            "and regulations.";

            TextView noticeText = new TextView(this);
            noticeText.setText(privacyNotice);
            noticeText.setTextSize(14);
            noticeText.setTextColor(
                    androidx.core.content.ContextCompat.getColor(
                            this, android.R.color.black
                    )
            );
            noticeText.setPadding(40, 20, 40, 20);
            noticeText.setTextIsSelectable(true);
            noticeText.setLineSpacing(4, 1.0f);

            ScrollView scrollView = new ScrollView(this);
            scrollView.setFillViewport(false);
            scrollView.addView(noticeText);

            new androidx.appcompat.app.AlertDialog.Builder(this)
                    .setTitle("Data Privacy Notice and Consent")
                    .setView(scrollView)
                    .setPositiveButton("Close", (dialog, which) -> dialog.dismiss())
                    .show();
        });
    }

    private void setupPrivacyConsent() {
        privacyConsentCheckbox = findViewById(R.id.privacyConsentCheckbox);
        privacyConsentCheckboxDisagree =
                findViewById(R.id.privacyConsentCheckboxDisagree);
        signupBtn = findViewById(R.id.signupBtn);

        // Initial state: signup is disabled
        signupBtn.setEnabled(false);
        signupBtn.setAlpha(0.5f);

        // Agree checkbox
        privacyConsentCheckbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isUpdatingCheckboxes) return;

            isUpdatingCheckboxes = true;

            if (isChecked) {
                privacyConsentCheckboxDisagree.setChecked(false);
            }

            isUpdatingCheckboxes = false;

            updateSignupButton();
        });

        // Disagree checkbox
        privacyConsentCheckboxDisagree.setOnCheckedChangeListener(
                (buttonView, isChecked) -> {
                    if (isUpdatingCheckboxes) return;

                    isUpdatingCheckboxes = true;

                    if (isChecked) {
                        privacyConsentCheckbox.setChecked(false);
                    }

                    isUpdatingCheckboxes = false;

                    updateSignupButton();

                    if (isChecked) {
                        showDisagreePrivacyDialog();
                    }
                }
        );
    }

    private void updateSignupButton() {
        boolean hasAgreed = privacyConsentCheckbox.isChecked();

        signupBtn.setEnabled(hasAgreed);
        signupBtn.setAlpha(hasAgreed ? 1.0f : 0.5f);
    }

    private void showDisagreePrivacyDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
                .setTitle("Privacy Notice Not Accepted")
                .setMessage(
                        "You have selected Disagree to the Data Privacy Notice " +
                                "and Consent. You cannot proceed with resident " +
                                "registration without agreeing to the stated terms.\n\n" +
                                "Would you like to exit registration or return to " +
                                "the form to change your selection?"
                )
                .setCancelable(false)
                .setNegativeButton("Exit", (dialog, which) -> {
                  finishAffinity();
                })
                .setPositiveButton("Continue", (dialog, which) -> {
                    // Allow the user to change their selection.
                    // Disagree remains selected until they change it.
                })
                .show();
    }

}