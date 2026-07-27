package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.AttendanceStatusCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateAnnouncementCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.DeleteAnnouncementCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchAnnouncementsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.SetAttendanceStatusCallback;
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

    public static void setAttendanceStatus(String announcementId, String status, String reason,
                                           String homeownerName, String block, String lot, String street,
                                           String role, String lavanyaPhaseType,
                                           SetAttendanceStatusCallback callback) {
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
                    AttendeeModel existing = snapshot.getValue(AttendeeModel.class);

                    if (existing != null && status.equals(existing.getStatus())) {
                        // tapping the same button again -> undo their response
                        myAttendanceRef.removeValue()
                                .addOnSuccessListener(v -> callback.onSuccess(null))
                                .addOnFailureListener(e -> callback.onFailure("Failed to undo response"));
                        return;
                    }
                }

                // new response, or switching from one status to the other
                AttendeeModel attendee = new AttendeeModel(
                        uid, homeownerName, block, lot, street, role, lavanyaPhaseType,
                        status, reason, System.currentTimeMillis()
                );

                myAttendanceRef.setValue(attendee)
                        .addOnSuccessListener(v -> callback.onSuccess(status))
                        .addOnFailureListener(e -> callback.onFailure("Failed to save response"));
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
                long attendingCount = 0;
                long notAttendingCount = 0;
                String currentUserStatus = null;

                for (DataSnapshot child : snapshot.getChildren()) {
                    AttendeeModel attendee = child.getValue(AttendeeModel.class);
                    if (attendee == null) continue;

                    if ("attending".equals(attendee.getStatus())) {
                        attendingCount++;
                    } else if ("not_attending".equals(attendee.getStatus())) {
                        notAttendingCount++;
                    }

                    if (child.getKey() != null && child.getKey().equals(uid)) {
                        currentUserStatus = attendee.getStatus();
                    }
                }

                callback.onResult(currentUserStatus, attendingCount, notAttendingCount);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}