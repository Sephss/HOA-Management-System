package com.example.hoamanagementsystem.FirebaseServices.callback;

public interface CreateBookingsCallback {
    void onSuccess(String bookingID,String message);
    void onFailure(String error);
}
