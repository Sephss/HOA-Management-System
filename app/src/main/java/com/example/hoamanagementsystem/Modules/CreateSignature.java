package com.example.hoamanagementsystem.Modules;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAnnouncementManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.SetAttendanceStatusCallback;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.cloudinary.addImage;
import com.github.gcacace.signaturepad.views.SignaturePad;

public class CreateSignature extends AppCompatActivity {

    public static final String EXTRA_ANNOUNCEMENT_ID = "announcementId";
    public static final String EXTRA_STATUS = "status";
    public static final String EXTRA_REASON = "reason";
    public static final String EXTRA_HOMEOWNER_NAME = "homeownerName";
    public static final String EXTRA_BLOCK = "block";
    public static final String EXTRA_LOT = "lot";
    public static final String EXTRA_STREET = "street";
    public static final String EXTRA_ROLE = "role";
    public static final String EXTRA_LAVANYA_PHASE_TYPE = "lavanyaPhaseType";

    private SignaturePad signaturePad;
    private Button btnClear, btnSave;
    private ImageView backBtn;

    private String announcementId, status, reason, homeownerName, block, lot, street, role, lavanyaPhaseType;
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_signature);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        readExtras();

        signaturePad = findViewById(R.id.signaturePad);
        btnClear = findViewById(R.id.btnClear);
        btnSave = findViewById(R.id.btnSave);
        backBtn = findViewById(R.id.backBtn);
        btnSave.setEnabled(false);

        signaturePad.setOnSignedListener(new SignaturePad.OnSignedListener() {
            @Override
            public void onStartSigning() { }

            @Override
            public void onSigned() {
                btnSave.setEnabled(true);
            }

            @Override
            public void onClear() {
                btnSave.setEnabled(false);
            }
        });

        backBtn.setOnClickListener(d-> {
            finish();
        });

        btnClear.setOnClickListener(v -> signaturePad.clear());

        btnSave.setOnClickListener(v -> onSaveClicked());
    }

    private void readExtras() {
        announcementId = getIntent().getStringExtra(EXTRA_ANNOUNCEMENT_ID);
        status = getIntent().getStringExtra(EXTRA_STATUS);
        reason = getIntent().getStringExtra(EXTRA_REASON);
        homeownerName = getIntent().getStringExtra(EXTRA_HOMEOWNER_NAME);
        block = getIntent().getStringExtra(EXTRA_BLOCK);
        lot = getIntent().getStringExtra(EXTRA_LOT);
        street = getIntent().getStringExtra(EXTRA_STREET);
        role = getIntent().getStringExtra(EXTRA_ROLE);
        lavanyaPhaseType = getIntent().getStringExtra(EXTRA_LAVANYA_PHASE_TYPE);

        if (announcementId == null || status == null) {
            Toast.makeText(this, "Missing attendance details.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void onSaveClicked() {
        if (signaturePad.isEmpty()) {
            Toast.makeText(this, "Please sign before saving.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        Bitmap bitmap = signaturePad.getSignatureBitmap();

        addImage.uploadBitmap(this, bitmap, new addImage.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                saveAttendance(imageUrl);
            }

            @Override
            public void onFailure(Exception e) {
                setLoading(false);
                Toast.makeText(CreateSignature.this,
                        "Failed to upload signature: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveAttendance(String signatureUrl) {
        FirebaseAnnouncementManager.setAttendanceStatus(
                announcementId, status, reason,
                homeownerName, block, lot, street, role, lavanyaPhaseType, signatureUrl,
                new SetAttendanceStatusCallback() {
                    @Override
                    public void onSuccess(String newStatus) {
                        setLoading(false);
                        Toast.makeText(CreateSignature.this, "Response saved", Toast.LENGTH_SHORT).show();
                        setResult(RESULT_OK);
                        finish();
                    }

                    @Override
                    public void onFailure(String message) {
                        setLoading(false);
                        Toast.makeText(CreateSignature.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void setLoading(boolean loading) {
        btnSave.setEnabled(!loading);
        btnClear.setEnabled(!loading);
    }
}