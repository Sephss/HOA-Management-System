package com.example.hoamanagementsystem.FirebaseServices.callback;

public interface DuplicateCheckCallback {
    void onResult(boolean isDuplicate, String existingTicket, String existingStatus,
                  String existingDate, String existingReporterId);

    void onError(String error);
}