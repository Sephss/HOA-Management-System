package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateAnnouncementCallback;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseAnnouncementManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("announcements");
    }

    public static void createAnnouncement(AnnouncementModel announcementModel, CreateAnnouncementCallback callback) {
        String announcementId = getDatabase().push().getKey();

        if(announcementId == null) {
            callback.onFailure("Failed to generate announcement ID");
            return;
        }
        announcementModel.setAnnouncementId(announcementId);
        getDatabase().child(announcementId).setValue(announcementModel).addOnSuccessListener(task -> {
            callback.onSuccess("Announcement created successfully");
        }).addOnFailureListener(g -> {
            callback.onFailure("Failed to create announcement");
        });
    }
}
