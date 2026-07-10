package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.MaintenanceModel;

import java.util.List;

public interface FetchMaintenanceCallback {
    void onSuccess(List<MaintenanceModel> maintenanceRequests);
    void onFailure(String error);
}
