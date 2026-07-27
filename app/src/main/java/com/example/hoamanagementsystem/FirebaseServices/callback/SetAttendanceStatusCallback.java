package com.example.hoamanagementsystem.FirebaseServices.callback;

public interface SetAttendanceStatusCallback {
    void onSuccess(String newStatus);
    void onFailure(String message);
}
