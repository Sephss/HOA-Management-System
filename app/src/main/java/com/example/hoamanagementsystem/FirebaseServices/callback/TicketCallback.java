package com.example.hoamanagementsystem.FirebaseServices.callback;

public interface TicketCallback {
    void onSuccess(String ticketNumber);
    void onFailure(String error);
}
