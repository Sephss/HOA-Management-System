package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.MaintenanceModel;

import java.util.List;

public interface CreateNotificationCallback {
    void onSuccess(String success);
    void onFailure(String error);
}
