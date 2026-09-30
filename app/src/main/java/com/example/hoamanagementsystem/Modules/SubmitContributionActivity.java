package com.example.hoamanagementsystem.Modules;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDatabaseManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserDatasCallback;
import com.example.hoamanagementsystem.Model.ContributionEntry;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.cloudinary.addImage;
import com.github.gcacace.signaturepad.views.SignaturePad;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class SubmitContributionActivity extends AppCompatActivity {

    private String uid;
    private String contributorName;
    private String solicitId;

    private ImageView backBtn;
    private TextView solicitTitleText, bankNameText, accountNameText, accountNumberText;
    private View bankDetailsCard;
    private EditText amountInput, controlNumberInput;
    private SignaturePad signaturePad;
    private TextView clearSignatureBtn, submitBtn;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_contribution);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        uid = FirebaseAuthManager.getCurrentUserUid();
        solicitId = getIntent().getStringExtra("solicitId");

        initViews();
        bindHeaderInfo();
        getUserDetails();

        backBtn.setOnClickListener(v -> finish());
        clearSignatureBtn.setOnClickListener(v -> signaturePad.clear());
        submitBtn.setOnClickListener(v -> submitContribution());
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        solicitTitleText = findViewById(R.id.solicitTitleText);
        bankDetailsCard = findViewById(R.id.bankDetailsCard);
        bankNameText = findViewById(R.id.bankNameText);
        accountNameText = findViewById(R.id.accountNameText);
        accountNumberText = findViewById(R.id.accountNumberText);
        amountInput = findViewById(R.id.amountInput);
        controlNumberInput = findViewById(R.id.controlNumberInput);
        signaturePad = findViewById(R.id.signaturePad);
        clearSignatureBtn = findViewById(R.id.clearSignatureBtn);
        submitBtn = findViewById(R.id.submitBtn);
        progressBar = findViewById(R.id.progressBar);
    }

    private void bindHeaderInfo() {
        String title = getIntent().getStringExtra("solicitTitle");
        String bankName = getIntent().getStringExtra("bankName");
        String accountName = getIntent().getStringExtra("accountName");
        String accountNumber = getIntent().getStringExtra("accountNumber");

        solicitTitleText.setText(TextUtils.isEmpty(title) ? "Solicitation" : title);

        boolean hasBankInfo = !TextUtils.isEmpty(bankName) || !TextUtils.isEmpty(accountNumber);
        if (hasBankInfo) {
            bankDetailsCard.setVisibility(View.VISIBLE);
            bankNameText.setText(TextUtils.isEmpty(bankName) ? "—" : "Bank: " + bankName);
            accountNameText.setText(TextUtils.isEmpty(accountName) ? "—" : "Name: " + accountName);
            accountNumberText.setText(TextUtils.isEmpty(accountNumber) ? "—" : "Acct #: " + accountNumber);
        } else {
            bankDetailsCard.setVisibility(View.GONE);
        }
    }

    private void getUserDetails() {
        FirebaseDatabaseManager.getUserDatas(uid, new UserDatasCallback() {
            @Override
            public void onSuccess(HomeOwnerRentersModel user) {
                contributorName = user.getFirstName() + " " + user.getLastName();
            }

            @Override
            public void onFailure(String message) {
                contributorName = "Unknown User";
            }
        });
    }

    private void submitContribution() {
        String amount = amountInput.getText().toString().trim();
        String controlNumber = controlNumberInput.getText().toString().trim();

        if (TextUtils.isEmpty(amount)) {
            amountInput.setError("Amount is required");
            amountInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(controlNumber)) {
            controlNumberInput.setError("Reference/control number is required");
            controlNumberInput.requestFocus();
            return;
        }
        if (signaturePad.isEmpty()) {
            Toast.makeText(this, "Please sign before submitting.", Toast.LENGTH_SHORT).show();
            return;
        }
        if (uid == null || solicitId == null) {
            Toast.makeText(this, "Unable to submit right now. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Uri signatureUri = saveSignatureToCache(signaturePad.getSignatureBitmap());
        if (signatureUri == null) {
            setLoading(false);
            Toast.makeText(this, "Failed to process signature.", Toast.LENGTH_SHORT).show();
            return;
        }

        addImage.uploadImage(this, signatureUri, new addImage.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                saveContributionToFirebase(amount, controlNumber, imageUrl);
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                Toast.makeText(SubmitContributionActivity.this,
                        "Signature upload failed. Try again.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    /** Saves the signature Bitmap to a cache file and returns a content:// Uri via FileProvider. */
    private Uri saveSignatureToCache(Bitmap bitmap) {
        try {
            File cacheDir = new File(getCacheDir(), "signatures");
            if (!cacheDir.exists()) cacheDir.mkdirs();
            File file = new File(cacheDir, "signature_" + System.currentTimeMillis() + ".png");

            try (FileOutputStream out = new FileOutputStream(file)) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
            }

            return FileProvider.getUriForFile(
                    this,
                    getApplicationContext().getPackageName() + ".fileprovider",
                    file
            );
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private void saveContributionToFirebase(String amount, String controlNumber, String signatureUrl) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("CommunitySolicitations")
                .child(solicitId)
                .child("contributions")
                .child(uid);

        long timestamp = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String date = dateFormat.format(timestamp);
        String time = timeFormat.format(timestamp);

        ContributionEntry entry = new ContributionEntry(
                uid,
                contributorName != null ? contributorName : "Unknown User",
                controlNumber,
                amount,
                signatureUrl,
                date,
                time,
                timestamp
        );

        ref.setValue(entry)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this, "Contribution submitted. Thank you!", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Failed to submit: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        submitBtn.setEnabled(!loading);
        submitBtn.setAlpha(loading ? 0.6f : 1f);
    }
}