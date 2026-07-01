package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateDocumentCallback;
import com.example.hoamanagementsystem.Model.DocumentRequestModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

}
