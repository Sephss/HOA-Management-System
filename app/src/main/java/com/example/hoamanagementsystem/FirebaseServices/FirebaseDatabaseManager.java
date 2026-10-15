package com.example.hoamanagementsystem.FirebaseServices;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.OccupancyListCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.RegisterHomeownerRenterCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateUserCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserDatasCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserProfileCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Model.OccupancyModel;
import com.example.hoamanagementsystem.Model.UserProfileModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class FirebaseDatabaseManager {
    private static final String OCCUPANCY_NODE = "OccupancyInformation";

    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    private static DatabaseReference getOccupancyRef(String uid) {
        return FirebaseDatabase.getInstance().getReference(OCCUPANCY_NODE).child(uid);
    }

    /** Generates a unique push key (used for occupancy record IDs). */
    public static String generateKey() {
        return FirebaseDatabase.getInstance().getReference().push().getKey();
    }

    public static void saveUser(HomeOwnerRentersModel homeOwnerRentersModel, RegisterHomeownerRenterCallback callback) {

        getDatabase().child(homeOwnerRentersModel.getUid()).setValue(homeOwnerRentersModel).addOnSuccessListener(task -> {
            Log.d(TAG, "User saved successfully");
            callback.onSuccess("User saved successfully");
        }).addOnFailureListener(g -> {
            Log.d(TAG, "User failed to save");
            callback.onFailure("User failed to save");
        });
    }
    public static void getUserDatas(String uid, UserDatasCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    HomeOwnerRentersModel user =
                            snapshot.getValue(HomeOwnerRentersModel.class);

                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("Failed to parse user data");
                    }

                } else {
                    callback.onFailure("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
    public static void updateUser(
            HomeOwnerRentersModel user,
            UpdateUserCallback callback
    ) {

        Map<String, Object> updates = new HashMap<>();

        if (user.getFirstName() != null && !user.getFirstName().trim().isEmpty()) {
            updates.put("firstName", user.getFirstName());
        }

        if (user.getMiddleName() != null && !user.getMiddleName().trim().isEmpty()) {
            updates.put("middleName", user.getMiddleName());
        }

        if (user.getLastName() != null && !user.getLastName().trim().isEmpty()) {
            updates.put("lastName", user.getLastName());
        }

        if (user.getPhoneNumber() != null && !user.getPhoneNumber().trim().isEmpty()) {
            updates.put("phoneNumber", user.getPhoneNumber());
        }

        if (user.getBlock() != null && !user.getBlock().trim().isEmpty()) {
            updates.put("block", user.getBlock());
        }

        if (user.getLot() != null && !user.getLot().trim().isEmpty()) {
            updates.put("lot", user.getLot());
        }

        if (user.getStreet() != null && !user.getStreet().trim().isEmpty()) {
            updates.put("street", user.getStreet());
        }

        if (user.getLavanyaPhaseType() != null && !user.getLavanyaPhaseType().trim().isEmpty()) {
            updates.put("lavanyaPhaseType", user.getLavanyaPhaseType());
        }

        if (user.getImageUrl() != null && !user.getImageUrl().trim().isEmpty()) {
            updates.put("imageUrl", user.getImageUrl());
        }

        if (updates.isEmpty()) {
            callback.onFailure("Nothing to update.");
            return;
        }

        getDatabase()
                .child(user.getUid())
                .updateChildren(updates)
                .addOnSuccessListener(unused ->
                        callback.onSuccess("Profile updated successfully."))
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    // ------------------------------------------------------------------
    // Full profile (users/{uid}) - used by EditProfilePage
    // ------------------------------------------------------------------

    public static void getUserProfile(String uid, UserProfileCallback callback) {
        if (uid == null) {
            callback.onFailure("Not signed in.");
            return;
        }

        getDatabase().child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    callback.onFailure("User not found");
                    return;
                }

                UserProfileModel profile = snapshot.getValue(UserProfileModel.class);

                if (profile != null) {
                    callback.onSuccess(profile);
                } else {
                    callback.onFailure("Failed to parse user data");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    /**
     * Updates only the fields the edit profile page owns. Admin flags (isAccountBanned, etc.),
     * email, uid, validIdUrl and leaseContractUrl are never touched.
     *
     * - Core fields (name, phone, address, phase, role, image/document URLs) are skipped when empty,
     *   so they can never be blanked by accident.
     * - Every other field is written as-is, so the user can clear it.
     */
    public static void updateUserProfile(UserProfileModel p, UpdateUserCallback callback) {
        if (p == null || p.uid == null) {
            callback.onFailure("Not signed in.");
            return;
        }

        Map<String, Object> updates = new HashMap<>();

        // Core fields
        putIfNotEmpty(updates, "firstName", p.firstName);
        putIfNotEmpty(updates, "lastName", p.lastName);
        putIfNotEmpty(updates, "phoneNumber", p.phoneNumber);
        putIfNotEmpty(updates, "block", p.block);
        putIfNotEmpty(updates, "lot", p.lot);
        putIfNotEmpty(updates, "street", p.street);
        putIfNotEmpty(updates, "lavanyaPhaseType", p.lavanyaPhaseType);
        putIfNotEmpty(updates, "role", p.role);
        putIfNotEmpty(updates, "imageUrl", p.imageUrl);
        putIfNotEmpty(updates, "supportingDocument1Url", p.supportingDocument1Url);
        putIfNotEmpty(updates, "supportingDocument2Url", p.supportingDocument2Url);

        // Optional fields
        putValue(updates, "middleName", p.middleName);

        putValue(updates, "birthday", p.birthday);
        putValue(updates, "gender", p.gender);
        putValue(updates, "civilStatus", p.civilStatus);
        putValue(updates, "preferredCommunication", p.preferredCommunication);
        putValue(updates, "messengerName", p.messengerName);

        putValue(updates, "dateOccupied", p.dateOccupied);
        putValue(updates, "dateAcquired", p.dateAcquired);
        putValue(updates, "propertyNotes", p.propertyNotes);

        putValue(updates, "tenantName", p.tenantName);
        putValue(updates, "tenantContact", p.tenantContact);
        putValue(updates, "leaseStart", p.leaseStart);
        putValue(updates, "leaseEnd", p.leaseEnd);
        putValue(updates, "tenantNumberOfOccupants", p.tenantNumberOfOccupants);

        putValue(updates, "numberOfVehicles", p.numberOfVehicles);
        putValue(updates, "vehicleType", p.vehicleType);
        putValue(updates, "plateNumber", p.plateNumber);
        putValue(updates, "vehicleMake", p.vehicleMake);
        putValue(updates, "vehicleModel", p.vehicleModel);
        putValue(updates, "vehicleColor", p.vehicleColor);

        putValue(updates, "numberOfPets", p.numberOfPets);
        putValue(updates, "petType", p.petType);
        putValue(updates, "petOtherType", p.petOtherType);
        putValue(updates, "petVaccinated", p.petVaccinated);
        putValue(updates, "petNeutered", p.petNeutered);
        putValue(updates, "supportsPetWelfare", p.supportsPetWelfare);
        putValue(updates, "supportsStrayProgram", p.supportsStrayProgram);

        putValue(updates, "emergencyContactName", p.emergencyContactName);
        putValue(updates, "emergencyContactRelationship", p.emergencyContactRelationship);
        putValue(updates, "emergencyContactNumber", p.emergencyContactNumber);

        getDatabase()
                .child(p.uid)
                .updateChildren(updates)
                .addOnSuccessListener(unused ->
                        callback.onSuccess("Profile updated successfully."))
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

    private static void putIfNotEmpty(Map<String, Object> map, String key, String value) {
        if (value != null && !value.trim().isEmpty()) {
            map.put(key, value);
        }
    }

    private static void putValue(Map<String, Object> map, String key, String value) {
        map.put(key, value == null ? "" : value);
    }

    // ------------------------------------------------------------------
    // Occupancy: OccupancyInformation/{uid}/{recordId}/{data}
    // ------------------------------------------------------------------

    public static void getOccupancyList(String uid, OccupancyListCallback callback) {
        if (uid == null) {
            callback.onFailure("Not signed in.");
            return;
        }

        getOccupancyRef(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<OccupancyModel> records = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    OccupancyModel record = child.getValue(OccupancyModel.class);

                    if (record != null) {
                        record.id = child.getKey();
                        records.add(record);
                    }
                }

                callback.onSuccess(records);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    /** Replaces the user's whole occupancy node with the given list (empty list removes the node). */
    public static void saveOccupancyList(String uid, List<OccupancyModel> records, UpdateUserCallback callback) {
        if (uid == null) {
            callback.onFailure("Not signed in.");
            return;
        }

        DatabaseReference ref = getOccupancyRef(uid);

        if (records == null || records.isEmpty()) {
            deleteOccupancyList(uid, callback);
            return;
        }

        Map<String, Object> data = new LinkedHashMap<>();

        for (OccupancyModel record : records) {
            String recordId = (record.id == null || record.id.isEmpty()) ? ref.push().getKey() : record.id;

            Map<String, Object> row = new HashMap<>();
            row.put("fullName", record.fullName == null ? "" : record.fullName);
            row.put("relationshipToOwner", record.relationshipToOwner == null ? "" : record.relationshipToOwner);
            row.put("age", record.age == null ? "" : record.age);
            row.put("contactNumber", record.contactNumber == null ? "" : record.contactNumber);

            data.put(recordId, row);
        }

        ref.setValue(data)
                .addOnSuccessListener(unused -> callback.onSuccess("Occupancy information saved."))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }

    public static void deleteOccupancyList(String uid, UpdateUserCallback callback) {
        if (uid == null) {
            callback.onFailure("Not signed in.");
            return;
        }

        getOccupancyRef(uid).removeValue()
                .addOnSuccessListener(unused -> callback.onSuccess("Occupancy information removed."))
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}