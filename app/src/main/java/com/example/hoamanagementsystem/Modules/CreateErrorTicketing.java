package com.example.hoamanagementsystem.Modules;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDatabaseManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserDatasCallback;
import com.example.hoamanagementsystem.Model.ErrorTicketEntry;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.cloudinary.addImage;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class CreateErrorTicketing extends AppCompatActivity {

    private String uid;
    private String theFullName;

    private ImageView backBtn;
    private Spinner categorySpinner;
    private EditText titleInput, descriptionInput;
    private View attachImageBtn;
    private ImageView imagePreview;
    private TextView attachImageText;
    private Button submitBtn;
    private ProgressBar progressBar;

    private Uri selectedImageUri = null;

    private final ActivityResultLauncher<String> imagePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
                if (uri != null) {
                    selectedImageUri = uri;
                    imagePreview.setImageURI(uri);
                    imagePreview.setVisibility(View.VISIBLE);
                    attachImageText.setText("Tap to change image");
                }
            });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_create_error_ticketing);

        uid = FirebaseAuthManager.getCurrentUserUid();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initViews();
        setupCategorySpinner();
        getUserDetails();

        backBtn.setOnClickListener(v -> finish());
        attachImageBtn.setOnClickListener(v -> imagePickerLauncher.launch("image/*"));
        submitBtn.setOnClickListener(v -> submitTicket());
    }

    private void initViews() {
        backBtn = findViewById(R.id.backBtn);
        categorySpinner = findViewById(R.id.categorySpinner);
        titleInput = findViewById(R.id.titleInput);
        descriptionInput = findViewById(R.id.descriptionInput);
        attachImageBtn = findViewById(R.id.attachImageBtn);
        imagePreview = findViewById(R.id.imagePreview);
        attachImageText = findViewById(R.id.attachImageText);
        submitBtn = findViewById(R.id.submitBtn);
        progressBar = findViewById(R.id.progressBar);
    }

    private void setupCategorySpinner() {
        String[] categories = {"Bug / Error", "Suggestion / Feature Request"};
        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this, android.R.layout.simple_spinner_item, categories);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(spinnerAdapter);
    }

    private void getUserDetails() {
        FirebaseDatabaseManager.getUserDatas(uid, new UserDatasCallback() {
            @Override
            public void onSuccess(HomeOwnerRentersModel user) {
                theFullName = user.getFirstName() + " " + user.getLastName();
            }

            @Override
            public void onFailure(String message) {
                theFullName = "Unknown User";
            }
        });
    }

    private void submitTicket() {
        String category = categorySpinner.getSelectedItem() != null
                ? categorySpinner.getSelectedItem().toString() : "";
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();

        if (TextUtils.isEmpty(title)) {
            titleInput.setError("Title is required");
            titleInput.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptionInput.setError("Description is required");
            descriptionInput.requestFocus();
            return;
        }
        if (uid == null) {
            Toast.makeText(this, "You must be logged in to submit a ticket.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        if (selectedImageUri != null) {
            addImage.uploadImage(this, selectedImageUri, new addImage.UploadCallback() {
                @Override
                public void onSuccess(String imageUrl) {
                    saveTicketToFirebase(category, title, description, imageUrl);
                }

                @Override
                public void onFailure(Exception e) {
                    setLoading(false);
                    Toast.makeText(CreateErrorTicketing.this,
                            "Image upload failed. Try again.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            saveTicketToFirebase(category, title, description, "");
        }
    }

    private void saveTicketToFirebase(String category, String title, String description, String imageUrl) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("errorTicketing")
                .child(uid);

        String ticketId = ref.push().getKey();
        if (ticketId == null) {
            setLoading(false);
            Toast.makeText(this, "Failed to create ticket. Try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        long timestamp = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.getDefault());
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.getDefault());
        String dateCreated = dateFormat.format(timestamp);
        String timeCreated = timeFormat.format(timestamp);

        ErrorTicketEntry entry = new ErrorTicketEntry(
                ticketId,
                uid,
                category,
                title,
                description,
                imageUrl,
                "Pending",
                theFullName != null ? theFullName : "Unknown User",
                dateCreated,
                timeCreated,
                timestamp
        );

        ref.child(ticketId).setValue(entry)
                .addOnSuccessListener(unused -> {
                    setLoading(false);
                    Toast.makeText(this, "Ticket submitted successfully.", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    setLoading(false);
                    Toast.makeText(this, "Failed to submit ticket: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setLoading(boolean loading) {
        progressBar.setVisibility(loading ? View.VISIBLE : View.GONE);
        submitBtn.setEnabled(!loading);
        submitBtn.setAlpha(loading ? 0.6f : 1f);
    }
}