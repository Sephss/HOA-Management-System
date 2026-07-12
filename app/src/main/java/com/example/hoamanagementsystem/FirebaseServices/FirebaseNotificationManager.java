package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.CheckUnreadNotificationCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateNotificationCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetNotificationCallback;
import com.example.hoamanagementsystem.Model.NotificationModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FirebaseNotificationManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("Notifications");
    }

    public static void createNotification(NotificationModel data, CreateNotificationCallback callback)  {
        String notificationID = getDatabase().push().getKey();
        if(notificationID == null) {
            callback.onFailure("Failed to generate notificationID");
            return;
        }

        data.setNotificationID(notificationID);

        getDatabase().child(notificationID).setValue(data).addOnCompleteListener(s -> {
            callback.onSuccess("");
        }).addOnFailureListener(g -> {
            callback.onFailure("");
        });
    }
    public static void getUserNotifications(
            String userId,
            GetNotificationCallback callback
    ) {

        getDatabase()
                .orderByChild("receiverID")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        List<NotificationModel> notifications = new ArrayList<>();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            NotificationModel model =
                                    data.getValue(NotificationModel.class);

                            if (model != null) {
                                notifications.add(model);
                            }
                        }
                        Collections.sort(notifications, (n1, n2) -> {

                            long t1 = 0;
                            long t2 = 0;

                            try {
                                t1 = Long.parseLong(n1.getTimestamp());
                            } catch (Exception ignored) {
                            }

                            try {
                                t2 = Long.parseLong(n2.getTimestamp());
                            } catch (Exception ignored) {
                            }

                            return Long.compare(t2, t1);
                        });
                        callback.onSuccess(notifications);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void hasUnreadNotifications(
            String userId,
            CheckUnreadNotificationCallback callback
    ) {

        getDatabase()
                .orderByChild("receiverID")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        boolean hasUnread = false;

                        for (DataSnapshot data : snapshot.getChildren()) {

                            NotificationModel model =
                                    data.getValue(NotificationModel.class);

                            if (model != null &&
                                    "no".equalsIgnoreCase(model.getIsRead())) {

                                hasUnread = true;
                                break; // No need to continue searching
                            }
                        }

                        callback.onResult(hasUnread);
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void markAllNotificationsAsRead(
            String userId,
            CreateNotificationCallback callback
    ) {

        getDatabase()
                .orderByChild("receiverID")
                .equalTo(userId)
                .addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {

                        java.util.Map<String, Object> updates = new java.util.HashMap<>();

                        for (DataSnapshot data : snapshot.getChildren()) {

                            NotificationModel model =
                                    data.getValue(NotificationModel.class);

                            if (model != null &&
                                    "no".equalsIgnoreCase(model.getIsRead())) {

                                updates.put(
                                        data.getKey() + "/isRead",
                                        "yes"
                                );
                            }
                        }

                        if (updates.isEmpty()) {
                            callback.onSuccess("No unread notifications.");
                            return;
                        }

                        getDatabase()
                                .updateChildren(updates)
                                .addOnSuccessListener(unused ->
                                        callback.onSuccess("Notifications marked as read."))
                                .addOnFailureListener(e ->
                                        callback.onFailure(e.getMessage()));
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
}
