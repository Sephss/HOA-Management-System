package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDuplicateGrievanceManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseGrievanceManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseTicketCounterManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.DuplicateCheckCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.SubmitGrievanceCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.TicketCallback;
import com.example.hoamanagementsystem.Model.GrievanceModel;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.cloudinary.addImage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SubmitGrievanceReport extends AppCompatActivity {
    private EditText incidentTitleET, incidentDescriptionET, incidentLocationET;
    private Spinner incidentTypeSpinner;
    private LinearLayout selectImage;
    private ImageView imageDisplay;
    private HomeOwnerRentersModel currentUser;
    private String currentUserID;
    private ImageView backBtn;

    private Button submitReportBtn;

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST = 1;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_grievance_report);
        currentUser = UserSession.getInstance().getCurrentUser();
        incidentTitleET = findViewById(R.id.incidentTitleET);
        incidentDescriptionET = findViewById(R.id.incidentDescriptionET);
        incidentLocationET = findViewById(R.id.incidentLocationET);
        backBtn = findViewById(R.id.backBtn);

        incidentTypeSpinner = findViewById(R.id.incidentTypeSpinner);
        selectImage = findViewById(R.id.selectImage);
        imageDisplay = findViewById(R.id.imageDisplay);
        submitReportBtn = findViewById(R.id.submitReportBtn);
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
        currentUserID = FirebaseAuthManager.getCurrentUserUid();

        setupSpinner();
        backBtn.setOnClickListener(s -> finish());
        selectImage.setOnClickListener(f -> choosePhotoFromGallery());
        imageDisplay.setOnClickListener(g -> choosePhotoFromGallery());
        submitReportBtn.setOnClickListener(l -> submitReport());
    }

    private void submitReport() {
        final String title = incidentTitleET.getText().toString().trim();
        final String description = incidentDescriptionET.getText().toString().trim();
        final String location = incidentLocationET.getText().toString().trim();
        final String incidentType = incidentTypeSpinner.getSelectedItem().toString();

        if (title.isEmpty()) {
            incidentTitleET.setError("Title is required");
            incidentTitleET.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            incidentDescriptionET.setError("Description is required");
            incidentDescriptionET.requestFocus();
            return;
        }

        if (location.isEmpty()) {
            incidentLocationET.setError("Location is required");
            incidentLocationET.requestFocus();
            return;
        }

        if (incidentType.equals("Select Incident Type")) {
            Toast.makeText(this, "Please Select Incident Type", Toast.LENGTH_SHORT).show();
            return;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Please upload proof of evidence", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState();

        // 1) Check for an existing report of the same issue BEFORE uploading anything
        FirebaseDuplicateGrievanceManager.checkDuplicate(
                incidentType, location, title, description,
                new DuplicateCheckCallback() {
                    @Override
                    public void onResult(boolean isDuplicate, String existingTicket, String existingStatus,
                                         String existingDate, String existingReporterId) {
                        if (isFinishing() || isDestroyed()) return;

                        if (isDuplicate) {
                            setNormalState();
                            boolean ownReport = existingReporterId != null
                                    && existingReporterId.equals(currentUserID);
                            showDuplicateDialog(ownReport, existingTicket, existingStatus, existingDate);
                        } else {
                            // 2) No duplicate: continue with the normal flow
                            uploadAndCreateReport(incidentType, title, description, location);
                        }
                    }

                    @Override
                    public void onError(String error) {
                        if (isFinishing() || isDestroyed()) return;
                        setNormalState();
                        Toast.makeText(SubmitGrievanceReport.this,
                                "Unable to verify existing reports: " + error, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showDuplicateDialog(boolean ownReport, String ticket, String status, String date) {
        String who = ownReport
                ? "You have already submitted a report for this issue."
                : "This issue has already been reported by another resident.";

        String message = who
                + "\n\nTicket No.: " + ticket
                + "\nStatus: " + capitalize(status)
                + "\nDate submitted: " + date
                + "\n\nThe HOA is already handling it, so a duplicate report can't be submitted.";

        new AlertDialog.Builder(this)
                .setTitle("Issue Already Reported")
                .setMessage(message)
                .setPositiveButton("OK", null)
                .show();
    }

    private String capitalize(String s) {
        if (s == null || s.isEmpty()) return "Pending";
        return s.substring(0, 1).toUpperCase(Locale.ENGLISH) + s.substring(1);
    }

    private void uploadAndCreateReport(String incidentType, String title,
                                       String description, String location) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        final String currentDate = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        final String currentTime = timeFormat.format(new Date());

        final String theTimeStamp = String.valueOf(System.currentTimeMillis());

        addImage.uploadImage(this, imageUri, new addImage.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {

                FirebaseTicketCounterManager.generateTicketNumber(new TicketCallback() {
                    @Override
                    public void onSuccess(String ticketNumber) {
                        GrievanceModel grievanceModel = new GrievanceModel(
                                incidentType, title, description, location, imageUrl, "",
                                currentUserID, "pending", ticketNumber, currentDate, currentTime,
                                "", "", "", theTimeStamp);

                        FirebaseGrievanceManager.createReport(grievanceModel, new SubmitGrievanceCallback() {
                            @Override
                            public void onSuccess(String Success) {
                                setNormalState();
                                navigateTo(SuccessGrievanceReport.class);
                                finish();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(SubmitGrievanceReport.this, error, Toast.LENGTH_SHORT).show();
                                setNormalState();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(SubmitGrievanceReport.this, error, Toast.LENGTH_SHORT).show();
                        setNormalState();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SubmitGrievanceReport.this,
                        "Failed to upload image " + e.getMessage(), Toast.LENGTH_SHORT).show();
                setNormalState();
            }
        });
    }

    private void setupSpinner() {
        List<String> incidentTypes = Arrays.asList(
                "Select Incident Type",
                "Noise Disturbance",
                "Waste/Sanitation",
                "Safety Hazard",
                "Others",
                "Illegal Parking",
                "Vandalism",
                "Suspicious Activity",
                "Pet-Related Complaint",
                "Property Damage",
                "Unauthorized Construction"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.spinner_item, incidentTypes);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incidentTypeSpinner.setAdapter(adapter);
    }

    public void choosePhotoFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            imageDisplay.setVisibility(View.VISIBLE);
            selectImage.setVisibility(View.GONE);
            imageDisplay.setImageURI(imageUri);
        }
    }

    private void setLoadingState() {
        submitReportBtn.setEnabled(false);
        submitReportBtn.setAlpha(0.5f);
        submitReportBtn.setText("Submitting...");
    }

    private void setNormalState() {
        submitReportBtn.setEnabled(true);
        submitReportBtn.setAlpha(1f);
        submitReportBtn.setText("Submit Report");
    }

    private void navigateTo(Class<?> destination) {
        startActivity(new Intent(this, destination));
    }
}