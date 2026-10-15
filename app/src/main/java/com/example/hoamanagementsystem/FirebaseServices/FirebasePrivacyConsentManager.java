package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.ApprovedConsentCallback;
import com.example.hoamanagementsystem.Model.ConsentModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebasePrivacyConsentManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("privacyConsents");
    }

    public static void giveConsent(ConsentModel data, ApprovedConsentCallback callback) {
        String consentId = getDatabase().push().getKey();
        if (consentId == null) {
            callback.onFailure("Failed to generate consent ID");
            return;
        }

        data.setConsentId(consentId);

        getDatabase().child(data.getConsentGiverId()).setValue(data).addOnSuccessListener(task -> {
            callback.onSuccess("Consent given successfully");
        }).addOnFailureListener(g -> {
            callback.onFailure("Failed to give consent");
        });
    }
}
