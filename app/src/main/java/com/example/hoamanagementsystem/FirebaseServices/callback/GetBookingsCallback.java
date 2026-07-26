package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.BookingsModel;

import java.util.List;

public interface GetBookingsCallback {
    void onSuccess(List<BookingsModel> bookings);
    void onFailure(String message);
}
