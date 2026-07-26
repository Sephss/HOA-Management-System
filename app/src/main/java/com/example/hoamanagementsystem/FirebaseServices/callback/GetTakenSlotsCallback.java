package com.example.hoamanagementsystem.FirebaseServices.callback;

import java.util.List;

public interface GetTakenSlotsCallback {
    void onResult(List<String> takenSlots);
    void onFailure(String message);
}
