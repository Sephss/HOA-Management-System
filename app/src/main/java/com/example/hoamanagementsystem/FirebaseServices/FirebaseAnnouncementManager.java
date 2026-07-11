package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateAnnouncementCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchAnnouncementsCallback;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
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

        getDatabase().addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                List<AnnouncementModel> announcements = new ArrayList<>();

                for (DataSnapshot ds : snapshot.getChildren()) {

                    AnnouncementModel announcement =
                            ds.getValue(AnnouncementModel.class);

                    if (announcement != null) {
                        announcements.add(announcement);
                    }
                }
                // Sort by timestamp (newest first)
                Collections.sort(announcements, (a, b) ->
                        Long.compare(b.getTimestamp(), a.getTimestamp()));

                callback.onSuccess(announcements);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}
