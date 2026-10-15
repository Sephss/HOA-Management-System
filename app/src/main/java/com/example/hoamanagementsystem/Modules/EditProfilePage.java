package com.example.hoamanagementsystem.Modules;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDatabaseManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.OccupancyListCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateUserCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserProfileCallback;
import com.example.hoamanagementsystem.Model.OccupancyModel;
import com.example.hoamanagementsystem.Model.UserProfileModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.cloudinary.addImage;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class EditProfilePage extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_DOC1_REQUEST = 2;
    private static final int PICK_DOC2_REQUEST = 3;
    private static final int MAX_OCCUPANTS = 10;

    private static final String ROLE_OWNER = "Home Owners";
    private static final String ROLE_RENTER = "Renters";

    private static final String[] PHASES = {"3A", "3B", "3C"};
    private static final String[] ROLES = {ROLE_OWNER, ROLE_RENTER};
    private static final String[] GENDERS = {"Select gender", "Male", "Female", "Other"};
    private static final String[] CIVIL_STATUSES = {"Select civil status", "Single", "Married", "Widow/Widower", "I choose not to disclose"};
    private static final String[] COMMUNICATIONS = {"Select method", "Call", "SMS", "Email", "Messenger"};
    private static final String[] VEHICLE_TYPES = {"Select type of vehicle", "Car", "Motorcycle", "E-bike", "Tricycle"};
    private static final String[] PET_TYPES = {"None", "Dog", "Cat", "Others"};
    private static final String[] OCCUPANT_RELATIONSHIPS = {"Self", "Spouse", "Child", "Parent", "Tenant", "Caretaker"};

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);

    // Existing views
    private ImageView theProfile, backBtn;
    private EditText firstNameET, lastNameET, middleNameET, phoneNumberET, blockET, lotET, streetET;
    private Spinner lavanyaPhaseTypeSpinner;
    private Button saveBtn;

    // Homeowner information
    private Spinner roleSpinner, genderSpinner, civilStatusSpinner, communicationSpinner;
    private EditText birthdayET, messengerNameET;

    // Property information
    private EditText dateOccupiedET, dateAcquiredET, propertyNotesET;

    // Emergency contact
    private EditText emergencyNameET, emergencyRelationshipET, emergencyContactET;

    // Tenant information
    private EditText tenantNameET, tenantContactET, leaseStartET, leaseEndET, tenantOccupantsET;

    // Documents
    private ImageView document1Image, document2Image;

    // Vehicle information
    private EditText numberOfVehiclesET, plateNumberET, vehicleMakeET, vehicleModelET, vehicleColorET;
    private Spinner vehicleTypeSpinner;

    // Pet information
    private EditText numberOfPetsET, petOtherTypeET;
    private Spinner petTypeSpinner;
    private LinearLayout petHealthLayout;
    private RadioGroup petVaccinatedGroup, petNeuteredGroup, supportsPetWelfareGroup, supportsStrayGroup;

    // Occupancy information (Home Owners only)
    private LinearLayout occupancySection, occupancyContainer;
    private Button addOccupancyBtn;

    // State
    private Uri imageUri, doc1Uri, doc2Uri;
    private String doc1Url, doc2Url;
    private String loadedRole;
    private String currentRole = ROLE_OWNER;
    private boolean profileLoaded = false;

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_profile_page);

        initViews();

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

        theProfile.setOnClickListener(d -> {
            choosePhotoFromGallery();
        });

        document1Image.setOnClickListener(d -> pickImage(PICK_DOC1_REQUEST));
        document2Image.setOnClickListener(d -> pickImage(PICK_DOC2_REQUEST));

        saveBtn.setOnClickListener(d -> {
            updateUserDetails();
        });

        backBtn.setOnClickListener(s-> {
            finish();
        });

        addOccupancyBtn.setOnClickListener(v -> addOccupancyRow(null));

        setUpSpinner();
        setUpFormSpinners();
        setUpDatePickers();

        loadProfile();
    }

    private void initViews() {
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
        lavanyaPhaseTypeSpinner = findViewById(R.id.lavanyaPhaseTypeSpinner);

        roleSpinner = findViewById(R.id.roleSpinner);
        genderSpinner = findViewById(R.id.genderSpinner);
        civilStatusSpinner = findViewById(R.id.civilStatusSpinner);
        communicationSpinner = findViewById(R.id.communicationSpinner);
        birthdayET = findViewById(R.id.birthdayET);
        messengerNameET = findViewById(R.id.messengerNameET);

        dateOccupiedET = findViewById(R.id.dateOccupiedET);
        dateAcquiredET = findViewById(R.id.dateAcquiredET);
        propertyNotesET = findViewById(R.id.propertyNotesET);

        emergencyNameET = findViewById(R.id.emergencyNameET);
        emergencyRelationshipET = findViewById(R.id.emergencyRelationshipET);
        emergencyContactET = findViewById(R.id.emergencyContactET);

        tenantNameET = findViewById(R.id.tenantNameET);
        tenantContactET = findViewById(R.id.tenantContactET);
        leaseStartET = findViewById(R.id.leaseStartET);
        leaseEndET = findViewById(R.id.leaseEndET);
        tenantOccupantsET = findViewById(R.id.tenantOccupantsET);

        document1Image = findViewById(R.id.document1Image);
        document2Image = findViewById(R.id.document2Image);

        numberOfVehiclesET = findViewById(R.id.numberOfVehiclesET);
        vehicleTypeSpinner = findViewById(R.id.vehicleTypeSpinner);
        plateNumberET = findViewById(R.id.plateNumberET);
        vehicleMakeET = findViewById(R.id.vehicleMakeET);
        vehicleModelET = findViewById(R.id.vehicleModelET);
        vehicleColorET = findViewById(R.id.vehicleColorET);

        numberOfPetsET = findViewById(R.id.numberOfPetsET);
        petTypeSpinner = findViewById(R.id.petTypeSpinner);
        petOtherTypeET = findViewById(R.id.petOtherTypeET);
        petHealthLayout = findViewById(R.id.petHealthLayout);
        petVaccinatedGroup = findViewById(R.id.petVaccinatedGroup);
        petNeuteredGroup = findViewById(R.id.petNeuteredGroup);
        supportsPetWelfareGroup = findViewById(R.id.supportsPetWelfareGroup);
        supportsStrayGroup = findViewById(R.id.supportsStrayGroup);

        occupancySection = findViewById(R.id.occupancySection);
        occupancyContainer = findViewById(R.id.occupancyContainer);
        addOccupancyBtn = findViewById(R.id.addOccupancyBtn);
    }

    // ------------------------------------------------------------------
    // Spinners / pickers
    // ------------------------------------------------------------------

    private void setUpSpinner() {
        bindSpinner(lavanyaPhaseTypeSpinner, PHASES);
    }

    private void setUpFormSpinners() {
        bindSpinner(roleSpinner, ROLES);
        bindSpinner(genderSpinner, GENDERS);
        bindSpinner(civilStatusSpinner, CIVIL_STATUSES);
        bindSpinner(communicationSpinner, COMMUNICATIONS);
        bindSpinner(vehicleTypeSpinner, VEHICLE_TYPES);
        bindSpinner(petTypeSpinner, PET_TYPES);

        communicationSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updateMessengerVisibility();
            }
        });

        petTypeSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                updatePetVisibility();
            }
        });

        roleSpinner.setOnItemSelectedListener(new SimpleItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                onRoleSelected(ROLES[position]);
            }
        });
    }

    private void bindSpinner(Spinner spinner, String[] items) {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                items
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    private void selectSpinnerValue(Spinner spinner, String value) {
        if (value == null || value.isEmpty()) {
            spinner.setSelection(0);
            return;
        }

        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(value)) {
                spinner.setSelection(i);
                return;
            }
        }
    }

    /** For spinners whose first item is a placeholder ("Select ...", "None"): returns "" when it is selected. */
    private String spinnerValue(Spinner spinner) {
        if (spinner.getSelectedItemPosition() <= 0) {
            return "";
        }
        return spinner.getSelectedItem().toString();
    }

    private void setUpDatePickers() {
        setUpDatePicker(birthdayET);
        setUpDatePicker(dateOccupiedET);
        setUpDatePicker(dateAcquiredET);
        setUpDatePicker(leaseStartET);
        setUpDatePicker(leaseEndET);
    }

    private void setUpDatePicker(EditText editText) {
        editText.setFocusable(false);
        editText.setClickable(true);

        editText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            String current = editText.getText().toString().trim();
            if (!current.isEmpty()) {
                try {
                    calendar.setTime(DATE_FORMAT.parse(current));
                } catch (ParseException ignored) {
                    // keep today's date
                }
            }

            new DatePickerDialog(this, (view, year, month, day) -> {
                Calendar picked = Calendar.getInstance();
                picked.set(year, month, day);
                editText.setText(DATE_FORMAT.format(picked.getTime()));
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });
    }

    private void updateMessengerVisibility() {
        boolean isMessenger = "Messenger".equals(spinnerValue(communicationSpinner));
        messengerNameET.setVisibility(isMessenger ? View.VISIBLE : View.GONE);
    }

    private void updatePetVisibility() {
        String petType = spinnerValue(petTypeSpinner);
        boolean dogOrCat = "Dog".equals(petType) || "Cat".equals(petType);

        petHealthLayout.setVisibility(dogOrCat ? View.VISIBLE : View.GONE);
        petOtherTypeET.setVisibility("Others".equals(petType) ? View.VISIBLE : View.GONE);
    }

    // ------------------------------------------------------------------
    // Role change handling
    // ------------------------------------------------------------------

    private void onRoleSelected(String newRole) {
        if (newRole.equals(currentRole)) {
            return;
        }

        if (ROLE_RENTER.equals(newRole)) {
            new AlertDialog.Builder(this)
                    .setTitle("Change role?")
                    .setMessage("Changing your role to Renters will delete all of your Occupancy Information entries when you save. Do you want to continue?")
                    .setPositiveButton("Yes, change", (dialog, which) -> {
                        currentRole = ROLE_RENTER;
                        occupancyContainer.removeAllViews();
                        occupancySection.setVisibility(View.GONE);
                    })
                    .setNegativeButton("Cancel", (dialog, which) -> roleSpinner.setSelection(0))
                    .setOnCancelListener(dialog -> roleSpinner.setSelection(0))
                    .show();
        } else {
            // Back to Home Owners: show the section again and reload whatever is saved
            currentRole = ROLE_OWNER;
            occupancySection.setVisibility(View.VISIBLE);
            loadOccupancy();
        }
    }

    // ------------------------------------------------------------------
    // Loading (straight from the database, no UserSession)
    // ------------------------------------------------------------------

    private void loadProfile() {
        String uid = FirebaseAuthManager.getCurrentUserUid();

        FirebaseDatabaseManager.getUserProfile(uid, new UserProfileCallback() {
            @Override
            public void onSuccess(UserProfileModel profile) {
                bindProfile(profile);
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EditProfilePage.this, "Failed to load profile: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void bindProfile(UserProfileModel p) {
        setText(firstNameET, p.firstName);
        setText(middleNameET, p.middleName);
        setText(lastNameET, p.lastName);
        setText(phoneNumberET, p.phoneNumber);
        setText(blockET, p.block);
        setText(lotET, p.lot);
        setText(streetET, p.street);

        selectSpinnerValue(lavanyaPhaseTypeSpinner, p.lavanyaPhaseType);

        if (p.imageUrl != null && !p.imageUrl.isEmpty() && !p.imageUrl.equals("none")) {
            Picasso.get()
                    .load(p.imageUrl)
                    .placeholder(R.drawable.profile_placeholder)
                    .into(theProfile);
        }

        // Role (set currentRole first so the spinner listener ignores the programmatic selection)
        loadedRole = p.role;
        currentRole = ROLE_OWNER.equalsIgnoreCase(p.role) ? ROLE_OWNER : ROLE_RENTER;
        roleSpinner.setSelection(ROLE_OWNER.equals(currentRole) ? 0 : 1);
        occupancySection.setVisibility(ROLE_OWNER.equals(currentRole) ? View.VISIBLE : View.GONE);

        // Homeowner information
        setText(birthdayET, p.birthday);
        selectSpinnerValue(genderSpinner, p.gender);
        selectSpinnerValue(civilStatusSpinner, p.civilStatus);
        selectSpinnerValue(communicationSpinner, p.preferredCommunication);
        setText(messengerNameET, p.messengerName);
        updateMessengerVisibility();

        // Property information
        setText(dateOccupiedET, p.dateOccupied);
        setText(dateAcquiredET, p.dateAcquired);
        setText(propertyNotesET, p.propertyNotes);

        // Emergency contact
        setText(emergencyNameET, p.emergencyContactName);
        setText(emergencyRelationshipET, p.emergencyContactRelationship);
        setText(emergencyContactET, p.emergencyContactNumber);

        // Tenant information
        setText(tenantNameET, p.tenantName);
        setText(tenantContactET, p.tenantContact);
        setText(leaseStartET, p.leaseStart);
        setText(leaseEndET, p.leaseEnd);
        setText(tenantOccupantsET, p.tenantNumberOfOccupants);

        // Documents
        doc1Url = p.supportingDocument1Url;
        doc2Url = p.supportingDocument2Url;
        showRemoteImage(doc1Url, document1Image);
        showRemoteImage(doc2Url, document2Image);

        // Vehicle information
        setText(numberOfVehiclesET, p.numberOfVehicles);
        selectSpinnerValue(vehicleTypeSpinner, p.vehicleType);
        setText(plateNumberET, p.plateNumber);
        setText(vehicleMakeET, p.vehicleMake);
        setText(vehicleModelET, p.vehicleModel);
        setText(vehicleColorET, p.vehicleColor);

        // Pet information
        setText(numberOfPetsET, p.numberOfPets);
        selectSpinnerValue(petTypeSpinner, p.petType);
        setText(petOtherTypeET, p.petOtherType);
        setYesNo(petVaccinatedGroup, p.petVaccinated);
        setYesNo(petNeuteredGroup, p.petNeutered);
        setYesNo(supportsPetWelfareGroup, p.supportsPetWelfare);
        setYesNo(supportsStrayGroup, p.supportsStrayProgram);
        updatePetVisibility();

        profileLoaded = true;

        if (ROLE_OWNER.equals(currentRole)) {
            loadOccupancy();
        }
    }

    private void loadOccupancy() {
        String uid = FirebaseAuthManager.getCurrentUserUid();

        FirebaseDatabaseManager.getOccupancyList(uid, new OccupancyListCallback() {
            @Override
            public void onSuccess(List<OccupancyModel> records) {
                // Ignore a late result if the user switched to Renters in the meantime
                if (!ROLE_OWNER.equals(currentRole)) {
                    return;
                }

                occupancyContainer.removeAllViews();

                for (OccupancyModel record : records) {
                    if (occupancyContainer.getChildCount() >= MAX_OCCUPANTS) {
                        break;
                    }
                    addOccupancyRow(record);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(EditProfilePage.this, "Failed to load occupancy information: " + message, Toast.LENGTH_LONG).show();
            }
        });
    }

    // ------------------------------------------------------------------
    // Dynamic rows: occupants
    // ------------------------------------------------------------------

    private void addOccupancyRow(OccupancyModel record) {
        if (occupancyContainer.getChildCount() >= MAX_OCCUPANTS) {
            Toast.makeText(this, "You can add up to " + MAX_OCCUPANTS + " occupants only.", Toast.LENGTH_SHORT).show();
            return;
        }

        View row = LayoutInflater.from(this).inflate(R.layout.item_occupancy, occupancyContainer, false);
        row.setTag(record != null ? record.id : null);

        Spinner relationshipSpinner = row.findViewById(R.id.occRelationshipSpinner);
        bindSpinner(relationshipSpinner, OCCUPANT_RELATIONSHIPS);

        if (record != null) {
            setText((EditText) row.findViewById(R.id.occFullNameET), record.fullName);
            setText((EditText) row.findViewById(R.id.occAgeET), record.age);
            setText((EditText) row.findViewById(R.id.occContactET), record.contactNumber);
            selectSpinnerValue(relationshipSpinner, record.relationshipToOwner);
        }

        row.findViewById(R.id.occRemoveBtn).setOnClickListener(v -> {
            occupancyContainer.removeView(row);
            refreshRowTitles(occupancyContainer, R.id.occTitle, "Occupant ");
        });

        occupancyContainer.addView(row);
        refreshRowTitles(occupancyContainer, R.id.occTitle, "Occupant ");
    }

    private void refreshRowTitles(LinearLayout container, int titleId, String prefix) {
        for (int i = 0; i < container.getChildCount(); i++) {
            TextView title = container.getChildAt(i).findViewById(titleId);
            title.setText(prefix + (i + 1));
        }
    }

    /** Returns null (after showing an error) when a row is invalid. Fully blank rows are skipped. */
    private List<OccupancyModel> collectOccupants() {
        List<OccupancyModel> result = new ArrayList<>();

        for (int i = 0; i < occupancyContainer.getChildCount(); i++) {
            View row = occupancyContainer.getChildAt(i);

            EditText fullNameET = row.findViewById(R.id.occFullNameET);
            EditText ageET = row.findViewById(R.id.occAgeET);
            EditText contactET = row.findViewById(R.id.occContactET);
            Spinner relationshipSpinner = row.findViewById(R.id.occRelationshipSpinner);

            String fullName = text(fullNameET);
            String age = text(ageET);
            String contact = text(contactET);

            if (fullName.isEmpty() && age.isEmpty() && contact.isEmpty()) {
                continue;
            }

            if (fullName.isEmpty()) {
                fullNameET.setError("Full name is required");
                fullNameET.requestFocus();
                return null;
            }

            OccupancyModel model = new OccupancyModel();
            model.id = row.getTag() != null ? row.getTag().toString() : FirebaseDatabaseManager.generateKey();
            model.fullName = fullName;
            model.relationshipToOwner = relationshipSpinner.getSelectedItem().toString();
            model.age = age;
            model.contactNumber = contact;

            row.setTag(model.id);
            result.add(model);
        }

        return result;
    }

    // ------------------------------------------------------------------
    // Saving
    // ------------------------------------------------------------------

    private void updateUserDetails() {
        if (!profileLoaded) {
            Toast.makeText(this, "Profile is still loading. Please wait.", Toast.LENGTH_SHORT).show();
            return;
        }

        final List<OccupancyModel> occupants;
        if (ROLE_OWNER.equals(currentRole)) {
            occupants = collectOccupants();
            if (occupants == null) {
                return;
            }
        } else {
            occupants = new ArrayList<>();
        }

        UserProfileModel profile = buildProfile();

        setLoadingState();

        uploadProfilePhoto(profile, () ->
                uploadDocuments(profile, () ->
                        saveProfile(profile, occupants)));
    }

    private UserProfileModel buildProfile() {
        UserProfileModel p = new UserProfileModel();

        p.uid = FirebaseAuthManager.getCurrentUserUid();

        p.firstName = text(firstNameET);
        p.middleName = text(middleNameET);
        p.lastName = text(lastNameET);
        p.phoneNumber = text(phoneNumberET);
        p.block = text(blockET);
        p.lot = text(lotET);
        p.street = text(streetET);
        p.lavanyaPhaseType = lavanyaPhaseTypeSpinner.getSelectedItem().toString();

        // Keep the stored role string untouched unless the user actually switched owner <-> renter
        boolean roleUnchanged = ROLE_OWNER.equalsIgnoreCase(loadedRole) == ROLE_OWNER.equals(currentRole);
        p.role = roleUnchanged ? loadedRole : currentRole;

        p.birthday = text(birthdayET);
        p.gender = spinnerValue(genderSpinner);
        p.civilStatus = spinnerValue(civilStatusSpinner);
        p.preferredCommunication = spinnerValue(communicationSpinner);
        p.messengerName = "Messenger".equals(p.preferredCommunication) ? text(messengerNameET) : "";

        p.dateOccupied = text(dateOccupiedET);
        p.dateAcquired = text(dateAcquiredET);
        p.propertyNotes = text(propertyNotesET);

        p.emergencyContactName = text(emergencyNameET);
        p.emergencyContactRelationship = text(emergencyRelationshipET);
        p.emergencyContactNumber = text(emergencyContactET);

        p.tenantName = text(tenantNameET);
        p.tenantContact = text(tenantContactET);
        p.leaseStart = text(leaseStartET);
        p.leaseEnd = text(leaseEndET);
        p.tenantNumberOfOccupants = text(tenantOccupantsET);

        p.supportingDocument1Url = doc1Url;
        p.supportingDocument2Url = doc2Url;

        p.numberOfVehicles = text(numberOfVehiclesET);
        p.vehicleType = spinnerValue(vehicleTypeSpinner);
        p.plateNumber = text(plateNumberET);
        p.vehicleMake = text(vehicleMakeET);
        p.vehicleModel = text(vehicleModelET);
        p.vehicleColor = text(vehicleColorET);

        String petType = spinnerValue(petTypeSpinner);
        boolean dogOrCat = "Dog".equals(petType) || "Cat".equals(petType);

        p.numberOfPets = text(numberOfPetsET);
        p.petType = petType;
        p.petOtherType = "Others".equals(petType) ? text(petOtherTypeET) : "";
        p.petVaccinated = dogOrCat ? getYesNo(petVaccinatedGroup) : "";
        p.petNeutered = dogOrCat ? getYesNo(petNeuteredGroup) : "";
        p.supportsPetWelfare = getYesNo(supportsPetWelfareGroup);
        p.supportsStrayProgram = getYesNo(supportsStrayGroup);

        return p;
    }

    private void uploadProfilePhoto(UserProfileModel profile, Runnable next) {
        // No new profile photo selected
        if (imageUri == null) {
            next.run();
            return;
        }

        addImage.uploadImage(this, imageUri, new addImage.UploadCallback() {

            @Override
            public void onSuccess(String imageUrl) {
                runOnUiThread(() -> {
                    profile.imageUrl = imageUrl;
                    next.run();
                });
            }

            @Override
            public void onFailure(Exception e) {
                fail("Failed to upload profile photo.");
            }

        });
    }

    private void uploadDocuments(UserProfileModel profile, Runnable next) {
        // No new documents selected
        if (doc1Uri == null && doc2Uri == null) {
            next.run();
            return;
        }

        addImage.uploadTwoImages(this, doc1Uri, doc2Uri, new addImage.TwoImagesCallback() {

            @Override
            public void onSuccess(String firstUrl, String secondUrl) {
                runOnUiThread(() -> {
                    if (firstUrl != null) {
                        profile.supportingDocument1Url = firstUrl;
                    }
                    if (secondUrl != null) {
                        profile.supportingDocument2Url = secondUrl;
                    }
                    next.run();
                });
            }

            @Override
            public void onFailure(Exception e) {
                fail("Failed to upload documents.");
            }

        });
    }

    private void saveProfile(UserProfileModel profile, List<OccupancyModel> occupants) {
        FirebaseDatabaseManager.updateUserProfile(profile, new UpdateUserCallback() {

            @Override
            public void onSuccess(String message) {
                syncOccupancy(profile, occupants);
            }

            @Override
            public void onFailure(String message) {
                fail("Failed to update profile: " + message);
            }

        });
    }

    private void syncOccupancy(UserProfileModel profile, List<OccupancyModel> occupants) {
        UpdateUserCallback done = new UpdateUserCallback() {

            @Override
            public void onSuccess(String message) {
                onSaveFinished(profile);
            }

            @Override
            public void onFailure(String message) {
                fail("Profile saved, but occupancy information failed: " + message);
            }

        };

        if (ROLE_OWNER.equals(currentRole)) {
            FirebaseDatabaseManager.saveOccupancyList(profile.uid, occupants, done);
        } else {
            // Renters have no occupancy records
            FirebaseDatabaseManager.deleteOccupancyList(profile.uid, done);
        }
    }

    private void onSaveFinished(UserProfileModel profile) {
        runOnUiThread(() -> {
            imageUri = null;
            doc1Uri = null;
            doc2Uri = null;
            doc1Url = profile.supportingDocument1Url;
            doc2Url = profile.supportingDocument2Url;
            loadedRole = profile.role;

            Toast.makeText(EditProfilePage.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
            setNormalState();
        });
    }

    private void fail(String message) {
        runOnUiThread(() -> {
            Toast.makeText(EditProfilePage.this, message, Toast.LENGTH_LONG).show();
            setNormalState();
        });
    }

    // ------------------------------------------------------------------
    // Image picking
    // ------------------------------------------------------------------

    public void choosePhotoFromGallery() {
        pickImage(PICK_IMAGE_REQUEST);
    }

    private void pickImage(int requestCode) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode != RESULT_OK || data == null || data.getData() == null) {
            return;
        }

        Uri uri = data.getData();

        if (requestCode == PICK_IMAGE_REQUEST) {
            imageUri = uri;
            theProfile.setImageURI(uri);  // Load the image into the ImageView
        } else if (requestCode == PICK_DOC1_REQUEST) {
            doc1Uri = uri;
            document1Image.setImageURI(uri);
        } else if (requestCode == PICK_DOC2_REQUEST) {
            doc2Uri = uri;
            document2Image.setImageURI(uri);
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private void showRemoteImage(String url, ImageView target) {
        if (url == null || url.isEmpty() || url.equals("none")) {
            return;
        }

        Picasso.get()
                .load(url)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .fit()
                .centerCrop()
                .into(target);
    }

    private String text(EditText editText) {
        return editText.getText().toString().trim();
    }

    private void setText(EditText editText, String value) {
        editText.setText(value == null ? "" : value);
    }

    /** Yes/No radio groups: first child = Yes, second child = No. Stored as "yes" / "no" / "". */
    private String getYesNo(RadioGroup group) {
        int checkedId = group.getCheckedRadioButtonId();

        if (checkedId == -1) {
            return "";
        }

        RadioButton checked = findViewById(checkedId);
        return checked.getText().toString().equalsIgnoreCase("Yes") ? "yes" : "no";
    }

    private void setYesNo(RadioGroup group, String value) {
        if ("yes".equalsIgnoreCase(value)) {
            ((RadioButton) group.getChildAt(0)).setChecked(true);
        } else if ("no".equalsIgnoreCase(value)) {
            ((RadioButton) group.getChildAt(1)).setChecked(true);
        } else {
            group.clearCheck();
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

    /** Spinner listener that only needs onItemSelected. */
    private abstract static class SimpleItemSelectedListener implements AdapterView.OnItemSelectedListener {
        @Override
        public void onNothingSelected(AdapterView<?> parent) {
        }
    }
}