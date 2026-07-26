package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.AttendanceStatusCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateAnnouncementCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.DeleteAnnouncementCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchAnnouncementsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.ToggleAttendanceCallback;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.example.hoamanagementsystem.Model.AttendeeModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
    public static void deleteAnnouncement(String announcementId,
                                          DeleteAnnouncementCallback callback) {

        getDatabase().child(announcementId)
                .removeValue()
                .addOnSuccessListener(unused -> {
                    callback.onSuccess("Announcement deleted successfully");
                })
                .addOnFailureListener(e -> {
                    callback.onFailure(e.getMessage());
                });
    }
    public static void getAttendanceRef(String announcementId) {
        // helper shown inline below, not a standalone method
    }

    public static void toggleAttendance(String announcementId, String homeownerName, String unitNumber, ToggleAttendanceCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not logged in");
            return;
        }
        String uid = currentUser.getUid();

        DatabaseReference myAttendanceRef = getDatabase()
                .child(announcementId)
                .child("attendees")
                .child(uid);

        myAttendanceRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // already confirmed -> un-confirm
                    myAttendanceRef.removeValue()
                            .addOnSuccessListener(v -> callback.onSuccess(false))
                            .addOnFailureListener(e -> callback.onFailure("Failed to remove attendance"));
                } else {
                    // not confirmed yet -> confirm
                    AttendeeModel attendee = new AttendeeModel(
                            uid, homeownerName, unitNumber, System.currentTimeMillis()
                    );
                    myAttendanceRef.setValue(attendee)
                            .addOnSuccessListener(v -> callback.onSuccess(true))
                            .addOnFailureListener(e -> callback.onFailure("Failed to confirm attendance"));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public static void getAttendanceStatus(String announcementId, AttendanceStatusCallback callback) {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser == null) {
            callback.onFailure("User not logged in");
            return;
        }
        String uid = currentUser.getUid();

        DatabaseReference attendeesRef = getDatabase()
                .child(announcementId)
                .child("attendees");

        attendeesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long count = snapshot.getChildrenCount();
                boolean isAttending = snapshot.hasChild(uid);
                callback.onResult(isAttending, count);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}
