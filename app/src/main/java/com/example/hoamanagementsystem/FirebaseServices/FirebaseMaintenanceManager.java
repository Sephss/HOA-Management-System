package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.FetchMaintenanceCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.SubmitMaintenanceCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateMaintenanceStatusCallback;
import com.example.hoamanagementsystem.Model.MaintenanceModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FirebaseMaintenanceManager {
    private static DatabaseReference getDatabase() {
    return FirebaseDatabase.getInstance().getReference("Maintenance");
    }

    public static void submitMaintenanceRequest(MaintenanceModel data, SubmitMaintenanceCallback callback) {
        String maintenanceID = getDatabase().push().getKey();

        if(maintenanceID == null) {
            callback.onFailure("Failed to generate maintenanceID");
            return;
        }

        data.setMaintenanceID(maintenanceID);
        getDatabase().child(maintenanceID).setValue(data).addOnCompleteListener(task -> {
            callback.onSuccess("Maintenance request submitted successfully!");
        }).addOnFailureListener(failed -> {
            callback.onFailure("Failed to submit maintenance request");
        });
    }
    public static void getMyMaintenanceRequests(
            String userId,
            FetchMaintenanceCallback callback
    ) {

        getDatabase()
                .orderByChild("submitterID")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<MaintenanceModel> requests = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            MaintenanceModel model =
                                    data.getValue(MaintenanceModel.class);

                            if (model != null) {
                                requests.add(model);
                            }
                        }
                        Collections.sort(requests, (a, b) ->
                                Long.compare(
                                        Long.parseLong(b.getTimestamp()),
                                        Long.parseLong(a.getTimestamp())
                                ));
                        callback.onSuccess(requests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void getAllMaintenanceRequests(
            FetchMaintenanceCallback callback
    ) {

        getDatabase()
                .addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<MaintenanceModel> requests = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            MaintenanceModel model =
                                    data.getValue(MaintenanceModel.class);

                            if (model != null) {
                                requests.add(model);
                            }
                        }

                        Collections.sort(requests, (a, b) ->
                                Long.compare(
                                        Long.parseLong(b.getTimestamp()),
                                        Long.parseLong(a.getTimestamp())
                                ));

                        callback.onSuccess(requests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void updateMaintenanceStatus(
            String maintenanceId,
            String selectedStatus,
            String adminRemarks,
            String dateTime,
            UpdateMaintenanceStatusCallback callback
    ) {

        HashMap<String, Object> updates = new HashMap<>();

        updates.put("maintenanceStatus", selectedStatus);
        updates.put(
                "adminRemarks",
                adminRemarks == null ? "" : adminRemarks.trim()
        );

        switch (selectedStatus.toLowerCase()) {

            case "repair_in_progress":
                updates.put("underInvestigationDate", dateTime);
                break;

            case "completed":
                updates.put("resolvedDate", dateTime);
                break;

            default:
                callback.onFailure("Invalid status selected.");
                return;
        }

        getDatabase()
                .child(maintenanceId)
                .updateChildren(updates)
                .addOnSuccessListener(unused ->
                        callback.onSuccess("Maintenance request updated successfully."))
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }
}
