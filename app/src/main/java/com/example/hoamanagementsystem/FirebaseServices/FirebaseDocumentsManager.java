package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateDocumentCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetDocumentRequestsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateDocumentStatusCallback;
import com.example.hoamanagementsystem.Model.DocumentRequestModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class FirebaseDocumentsManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("document_requests");
    }

    public static void createDocumentRequest (DocumentRequestModel documentRequestModel, CreateDocumentCallback callback) {
        String documentRequestID = getDatabase().push().getKey();

        if(documentRequestID == null) {
            callback.onFailure("Failed to generate document request ID");
            return;
        }

        documentRequestModel.setRequestID(documentRequestID);
        getDatabase().child(documentRequestID).setValue(documentRequestModel).addOnSuccessListener(task -> {
            callback.onSuccess("Document request created successfully");
        }).addOnFailureListener(g -> {
            callback.onFailure("Failed to create document request");
        });
    }

    public static void getUserDocumentRequests(
            String userId,
            GetDocumentRequestsCallback callback
    ) {

        getDatabase()
                .orderByChild("requesterID")
                .equalTo(userId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<DocumentRequestModel> requests = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            DocumentRequestModel model =
                                    dataSnapshot.getValue(DocumentRequestModel.class);

                            if (model != null) {
                                requests.add(model);
                            }
                        }
                        Collections.sort(requests, (a, b) -> {
                            long timeA = 0;
                            long timeB = 0;

                            try {
                                timeA = Long.parseLong(a.getRequstTimestamp());
                            } catch (Exception ignored) {}

                            try {
                                timeB = Long.parseLong(b.getRequstTimestamp());
                            } catch (Exception ignored) {}

                            return Long.compare(timeB, timeA);
                        });
                        callback.onSuccess(requests);
                        callback.onSuccess(requests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void getAllDocumentRequests(
            GetDocumentRequestsCallback callback
    ) {

        getDatabase()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        List<DocumentRequestModel> requests = new ArrayList<>();

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            DocumentRequestModel model =
                                    dataSnapshot.getValue(DocumentRequestModel.class);

                            if (model != null) {
                                requests.add(model);
                            }
                        }

                        Collections.sort(requests, (a, b) -> {
                            long timeA = 0;
                            long timeB = 0;

                            try {
                                timeA = Long.parseLong(a.getRequstTimestamp());
                            } catch (Exception ignored) {}

                            try {
                                timeB = Long.parseLong(b.getRequstTimestamp());
                            } catch (Exception ignored) {}

                            return Long.compare(timeB, timeA);
                        });
                        callback.onSuccess(requests);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void updateDocumentRequest(
            String requestId,
            String selectedStatus,
            String remarks,
            String link,
            String dateTime,
            UpdateDocumentStatusCallback callback
    ) {

        HashMap<String, Object> updates = new HashMap<>();

        updates.put("requestStatus", selectedStatus);

        updates.put(
                "adminNote",
                remarks == null ? "" : remarks.trim()
        );

        updates.put(
                "adminLinkResponse",
                link == null ? "" : link.trim()
        );

        switch (selectedStatus.toLowerCase()) {

            case "under_review":
                updates.put("underReviewDateTime", dateTime);
                break;

            case "approved":
                updates.put("approvedDateTime", dateTime);
                break;

            case "rejected":
                updates.put("rejectedDateTime", dateTime);
                break;

            case "cancelled":
                updates.put("cancelledDateTime", dateTime);
                break;

            default:
                callback.onFailure("Invalid status selected");
                return;
        }

        getDatabase()
                .child(requestId)
                .updateChildren(updates)
                .addOnSuccessListener(unused ->
                        callback.onSuccess("Request updated successfully"))
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }

}
