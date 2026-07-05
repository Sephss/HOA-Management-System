package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseGrievanceManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseTicketCounterManager;
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
    private String currentUserID;

    private Button submitReportBtn;

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST= 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_grievance_report);

        incidentTitleET = findViewById(R.id.incidentTitleET);
        incidentDescriptionET = findViewById(R.id.incidentDescriptionET);
        incidentLocationET = findViewById(R.id.incidentLocationET);

        incidentTypeSpinner = findViewById(R.id.incidentTypeSpinner);
        selectImage = findViewById(R.id.selectImage);

        imageDisplay = findViewById(R.id.imageDisplay);

        submitReportBtn = findViewById(R.id.submitReportBtn);

        currentUserID = FirebaseAuthManager.getCurrentUserUid();

        HomeOwnerRentersModel currentUser = UserSession.getInstance().getCurrentUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupSpinner();

        Toast.makeText(this, currentUser.getFirstName() + " " + currentUser.getLastName(), Toast.LENGTH_SHORT).show();

        selectImage.setOnClickListener(f -> {
            choosePhotoFromGallery();
        });

        imageDisplay.setOnClickListener(g -> {
            choosePhotoFromGallery();
        });

        submitReportBtn.setOnClickListener(l -> {
            submitReport();
        });
    }
    private void submitReport() {
        String title = incidentTitleET.getText().toString();
        String description = incidentDescriptionET.getText().toString();
        String location = incidentLocationET.getText().toString();
        String incidentType = incidentTypeSpinner.getSelectedItem().toString();

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

        if(title.isEmpty()) {
            incidentTitleET.setError("Title is reqired");
            incidentTitleET.requestFocus();
            return;
        }

        if(description.isEmpty()) {
            incidentDescriptionET.setError("Description is reqired");
            incidentDescriptionET.requestFocus();
            return;
        }

        if(location.isEmpty()) {
            incidentLocationET.setError("Location is reqired");
            incidentLocationET.requestFocus();
            return;
        }

        if(incidentType.equals("Select Incident Type")) {
            Toast.makeText(this, "Please Select Incident Type", Toast.LENGTH_SHORT).show();
            return;
        }

        if(imageUri == null) {
            Toast.makeText(this, "Please upload proof of evidence", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoadingState();

        addImage.uploadImage(this, imageUri, new addImage.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {

                FirebaseTicketCounterManager.generateTicketNumber(new TicketCallback() {
                    @Override
                    public void onSuccess(String ticketNumber) {
                        GrievanceModel grievanceModel = new GrievanceModel(incidentType, title, description, location, imageUrl, "", currentUserID, "pending", ticketNumber,currentDate, currentTime, "", "", "");

                        FirebaseGrievanceManager.createReport(grievanceModel, new SubmitGrievanceCallback() {
                            @Override
                            public void onSuccess(String Success) {
                                Toast.makeText(SubmitGrievanceReport.this, Success, Toast.LENGTH_SHORT).show();
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
                Toast.makeText(SubmitGrievanceReport.this, "Failed to upload image " + e.getMessage(), Toast.LENGTH_SHORT).show();
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

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                incidentTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        incidentTypeSpinner.setAdapter(adapter);
    }
    // method for choosing photo from your gallery
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
            imageDisplay.setImageURI(imageUri);  // Load the image into the ImageView
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
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
}