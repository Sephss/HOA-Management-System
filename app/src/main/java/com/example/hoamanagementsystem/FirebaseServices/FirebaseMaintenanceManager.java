package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.SubmitMaintenanceCallback;
import com.example.hoamanagementsystem.Model.MaintenanceModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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
}
