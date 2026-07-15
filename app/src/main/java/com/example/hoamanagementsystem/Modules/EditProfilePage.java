package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDatabaseManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateUserCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.cloudinary.addImage;
import com.squareup.picasso.Picasso;

public class EditProfilePage extends AppCompatActivity {
    private ImageView theProfile, backBtn;
    private EditText firstNameET, lastNameET, middleNameET, phoneNumberET, blockET, lotET, streetET;
    private Uri imageUri;
    private Button saveBtn;
    private static final int PICK_IMAGE_REQUEST= 1;

    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_edit_profile_page);

        theProfile = findViewById(R.id.theProfile);
        firstNameET = findViewById(R.id.firstNameET);
        lastNameET = findViewById(R.id.lastNameET);
        middleNameET = findViewById(R.id.middleNameET);
        phoneNumberET = findViewById(R.id.phoneNumberET);
        blockET = findViewById(R.id.blockET);
        lotET = findViewById(R.id.lotET);
        streetET = findViewById(R.id.streetET);
        backBtn = findViewById(R.id.backBtn);

        saveBtn = findViewById(R.id.saveBtn);


        theProfile.setOnClickListener(d -> {
            choosePhotoFromGallery();
        });

        saveBtn.setOnClickListener(d -> {
            updateUserDetails();
        });

        backBtn.setOnClickListener(s-> {
            finish();
        });

        populateUserDetails();
    }
    private void updateUserDetails() {
        String userID = FirebaseAuthManager.getCurrentUserUid();

        HomeOwnerRentersModel user = new HomeOwnerRentersModel();

        user.setUid(userID);

        user.setFirstName(firstNameET.getText().toString().trim());
        user.setMiddleName(middleNameET.getText().toString().trim());
        user.setLastName(lastNameET.getText().toString().trim());
        user.setPhoneNumber(phoneNumberET.getText().toString().trim());
        user.setBlock(blockET.getText().toString().trim());
        user.setLot(lotET.getText().toString().trim());
        user.setStreet(streetET.getText().toString().trim());

        // User selected a new image
        if (imageUri != null) {

            setLoadingState();

            addImage.uploadImage(this, imageUri, new addImage.UploadCallback() {

                @Override
                public void onSuccess(String imageUrl) {

                    runOnUiThread(() -> {

                        user.setImageUrl(imageUrl);

                        FirebaseDatabaseManager.updateUser(user, new UpdateUserCallback() {

                            @Override
                            public void onSuccess(String message) {
                                Toast.makeText(EditProfilePage.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                setNormalState();
                            }

                            @Override
                            public void onFailure(String message) {
                                setNormalState();
                                // Failed

                            }

                        });

                    });

                }

                @Override
                public void onFailure(Exception e) {

                    runOnUiThread(() -> {
                        setNormalState();
                        // Upload failed

                    });

                }

            });

        }

        // No new image selected
        else {

            FirebaseDatabaseManager.updateUser(user, new UpdateUserCallback() {

                @Override
                public void onSuccess(String message) {
                    Toast.makeText(EditProfilePage.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    setNormalState();
                    // Success

                }

                @Override
                public void onFailure(String message) {
                    setNormalState();
                    // Failed

                }

            });

        }

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
            theProfile.setImageURI(imageUri);  // Load the image into the ImageView
        }
    }
    private void populateUserDetails() {

        HomeOwnerRentersModel user = UserSession.getInstance().getCurrentUser();

        firstNameET.setText(user.getFirstName());
        middleNameET.setText(user.getMiddleName());
        lastNameET.setText(user.getLastName());
        phoneNumberET.setText(user.getPhoneNumber());
        blockET.setText(user.getBlock());
        lotET.setText(user.getLot());
        streetET.setText(user.getStreet());

        if (!user.getImageUrl().equals("none")) {
            Picasso.get()
                    .load(user.getImageUrl())
                    .placeholder(R.drawable.profile_placeholder)
                    .into(theProfile);
        }

    }
    private void setLoadingState() {
        saveBtn.setEnabled(false);
        saveBtn.setAlpha(0.5f);
        saveBtn.setText("SAVING...");
    }
    private void setNormalState() {
        saveBtn.setEnabled(true);
        saveBtn.setAlpha(1f);
        saveBtn.setText("SAVE");
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
}