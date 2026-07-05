package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.SubmitGrievanceCallback;
import com.example.hoamanagementsystem.Model.GrievanceModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseGrievanceManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("Grievance");
    }
    public static void createReport(GrievanceModel data, SubmitGrievanceCallback callback) {
        String reportID = getDatabase().push().getKey();

        if(reportID == null) {
            callback.onFailure("Failed to generate ReportID");
            return;
        }

        data.setIncidentReportID(reportID);
        getDatabase().child(reportID).setValue(data).addOnCompleteListener(task -> {
            callback.onSuccess("Report submitted successfully!");
        }).addOnFailureListener(failed -> {
            callback.onFailure("Failed to submit report");
        });
    }

}
