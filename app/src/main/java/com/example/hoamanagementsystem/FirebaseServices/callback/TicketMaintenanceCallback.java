package com.example.hoamanagementsystem.FirebaseServices.callback;

public interface TicketMaintenanceCallback {
    void onSuccess(String ticketNumber);
    void onFailure(String error);
}
