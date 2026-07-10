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
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseMaintenanceManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseTicketMaintenanceManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.SubmitMaintenanceCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.TicketMaintenanceCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Model.MaintenanceModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.cloudinary.addImage;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class SubmitMaintenanceRequest extends AppCompatActivity {
    private Spinner maintenanceTypeSpinner;
    private LinearLayout routineBtn, moderateBtn, urgentBtn, selectImage;
    private EditText maintenanceTitleET, maintenanceDescriptionET, maintenanceLocationET;
    private ImageView imageDisplay;
    private HomeOwnerRentersModel currentUser;
    private Button submitRequestBtn;

    private String theUrgencyLevel = "none";

    private Uri imageUri;
    private static final int PICK_IMAGE_REQUEST= 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_submit_maintenance_request);

        maintenanceTypeSpinner = findViewById(R.id.maintenanceTypeSpinner);
        routineBtn = findViewById(R.id.routineBtn);
        moderateBtn = findViewById(R.id.moderateBtn);
        urgentBtn = findViewById(R.id.urgentBtn);
        maintenanceTitleET = findViewById(R.id.maintenanceTitleET);
        maintenanceDescriptionET = findViewById(R.id.maintenanceDescriptionET);
        maintenanceLocationET = findViewById(R.id.maintenanceLocationET);
        selectImage = findViewById(R.id.selectImage);
        imageDisplay = findViewById(R.id.imageDisplay);
        submitRequestBtn = findViewById(R.id.submitReportBtn);

        currentUser = UserSession.getInstance().getCurrentUser();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        setupSpinner();

        submitRequestBtn.setOnClickListener(s -> {
            submitMaintenanceRequest();
        });
        selectImage.setOnClickListener(f -> {
            choosePhotoFromGallery();
        });

        imageDisplay.setOnClickListener(g -> {
            choosePhotoFromGallery();
        });

        routineBtn.setOnClickListener(d -> {
            theUrgencyLevel = "routine";
        });

        moderateBtn.setOnClickListener(d -> {
            theUrgencyLevel = "moderate";
        });

        urgentBtn.setOnClickListener(d -> {
            theUrgencyLevel = "urgent";
        });

    }
    private void setupSpinner() {

        List<String> maintenanceTypes = Arrays.asList(
                "Select Maintenance Type",
                "Road Repair",
                "Streetlight Repair",
                "Drainage Maintenance",
                "Water Supply Issue",
                "Sewer/Plumbing Issue",
                "Electrical Issue",
                "Landscaping/Gardening",
                "Tree Trimming",
                "Garbage Collection Area",
                "Common Area Cleaning",
                "Fence/Wall Repair",
                "Gate Barrier Repair",
                "Security Equipment Maintenance",
                "Playground Maintenance",
                "Clubhouse Maintenance",
                "Swimming Pool Maintenance",
                "Park Maintenance",
                "Signage Repair",
                "Pest Control",
                "Others"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this, R.layout.spinner_item,
                maintenanceTypes
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        maintenanceTypeSpinner.setAdapter(adapter);
    }
    private void submitMaintenanceRequest() {
        String maintenanceType = maintenanceTypeSpinner.getSelectedItem().toString();
        String maintenanceTitle = maintenanceTitleET.getText().toString();
        String maintenanceDescription = maintenanceDescriptionET.getText().toString();
        String maintenanceLocation = maintenanceLocationET.getText().toString();

        if(maintenanceType.equals("Select Maintenance Type")) {
            Toast.makeText(this, "Select maintenance type", Toast.LENGTH_SHORT).show();
            return;
        }
        if(theUrgencyLevel.equals("none")) {
            Toast.makeText(this, "Select urgency level", Toast.LENGTH_SHORT).show();
            return;
        }
        if(maintenanceTitle.isEmpty()) {
            maintenanceTitleET.setError("Title is required");
            return;
        }
        if(maintenanceDescription.isEmpty()) {
            maintenanceDescriptionET.setError("Description is required");
            return;
        }
        if(maintenanceLocation.isEmpty()) {
            maintenanceLocationET.setError("Location is required");
            return;
        }
        if(imageUri == null) {
            Toast.makeText(this, "Please upload proof of evidence", Toast.LENGTH_SHORT).show();
            return;
        }

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

        addImage.uploadImage(this, imageUri, new addImage.UploadCallback() {
            @Override
            public void onSuccess(String imageUrl) {
                FirebaseTicketMaintenanceManager.generateTicketNumber(new TicketMaintenanceCallback() {
                    @Override
                    public void onSuccess(String ticketNumber) {
                        MaintenanceModel data = new MaintenanceModel(maintenanceType, maintenanceTitle, theUrgencyLevel, maintenanceDescription, maintenanceLocation, imageUrl, currentUser.getFirstName() + " " + currentUser.getLastName(), currentUser.getUid(), "pending", "", ticketNumber, currentDate, currentTime, "", "", "");

                        FirebaseMaintenanceManager.submitMaintenanceRequest(data, new SubmitMaintenanceCallback() {
                            @Override
                            public void onSuccess(String Success) {
                                Toast.makeText(SubmitMaintenanceRequest.this, Success, Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onFailure(String error) {
                                Toast.makeText(SubmitMaintenanceRequest.this, error, Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String error) {
                        Toast.makeText(SubmitMaintenanceRequest.this, error, Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Exception e) {
                Toast.makeText(SubmitMaintenanceRequest.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

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
}