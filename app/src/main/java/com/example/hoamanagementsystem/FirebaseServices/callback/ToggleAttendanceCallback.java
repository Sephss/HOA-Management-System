package com.example.hoamanagementsystem.FirebaseServices.callback;
public interface ToggleAttendanceCallback {
    void onSuccess(boolean isNowAttending);
    void onFailure(String error);
}