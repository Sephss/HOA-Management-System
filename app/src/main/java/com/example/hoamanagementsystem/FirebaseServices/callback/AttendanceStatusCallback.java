package com.example.hoamanagementsystem.FirebaseServices.callback;


public interface AttendanceStatusCallback {
    void onResult(String currentUserStatus, long attendingCount, long notAttendingCount);
    void onFailure(String message);
}