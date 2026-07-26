package com.example.hoamanagementsystem.FirebaseServices.callback;


public interface AttendanceStatusCallback {
    void onResult(boolean isAttending, long attendeeCount);
    void onFailure(String error);
}