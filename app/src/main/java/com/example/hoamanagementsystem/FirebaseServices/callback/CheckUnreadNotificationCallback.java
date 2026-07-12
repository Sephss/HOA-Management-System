package com.example.hoamanagementsystem.FirebaseServices.callback;

public interface CheckUnreadNotificationCallback {
    void onResult(boolean hasUnread);
    void onFailure(String error);
}