package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.FetchGrievanceCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.SubmitGrievanceCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateGrievanceStatusCallback;
import com.example.hoamanagementsystem.Model.GrievanceModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

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
    public static void getMyReports(String uid, FetchGrievanceCallback callback) {

        Query query = getDatabase()
                .orderByChild("incidentReporterID")
                .equalTo(uid);

        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {

                List<GrievanceModel> reports = new ArrayList<>();

                for(DataSnapshot data : snapshot.getChildren()) {

                    GrievanceModel report =
                            data.getValue(GrievanceModel.class);

                    if(report != null) {
                        reports.add(report);
                    }
                }

                callback.onSuccess(reports);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
    public static void getAllReports(FetchGrievanceCallback callback) {

        getDatabase().addListenerForSingleValueEvent(
                new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        List<GrievanceModel> reports = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            GrievanceModel report =
                                    data.getValue(GrievanceModel.class);

                            if (report != null) {
                                reports.add(report);
                            }
                        }

                        callback.onSuccess(reports);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void updateGrievanceStatus(
            String reportId,
            String selectedStatus,
            String adminRemarks,
            String dateTime,
            UpdateGrievanceStatusCallback callback
    ) {

        java.util.HashMap<String, Object> updates = new java.util.HashMap<>();

        updates.put("incidentStatus", selectedStatus);
        updates.put("adminRemarks",
                adminRemarks == null ? "" : adminRemarks.trim());

        switch (selectedStatus.toLowerCase()) {

            case "under_investigation":
                updates.put("underInvestigationDate", dateTime);
                break;

            case "resolved":
                updates.put("resolvedDate", dateTime);
                break;

            default:
                callback.onFailure("Invalid status selected.");
                return;
        }

        getDatabase()
                .child(reportId)
                .updateChildren(updates)
                .addOnSuccessListener(unused ->
                        callback.onSuccess("Report updated successfully."))
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }
}
