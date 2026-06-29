package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateAnnouncementCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchAnnouncementsCallback;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

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
    public static void fetchAnnouncements(FetchAnnouncementsCallback callback) {

        getDatabase().get()
                .addOnSuccessListener(snapshot -> {

                    List<AnnouncementModel> announcements = new ArrayList<>();

                    for (DataSnapshot ds : snapshot.getChildren()) {

                        AnnouncementModel announcement =
                                ds.getValue(AnnouncementModel.class);

                        if (announcement != null) {
                            announcements.add(announcement);
                        }
                    }

                    callback.onSuccess(announcements);
                })
                .addOnFailureListener(e ->
                        callback.onFailure(e.getMessage()));
    }
}
