package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.OccupancyModel;

import java.util.List;

public interface OccupancyListCallback {
    void onSuccess(List<OccupancyModel> records);

    void onFailure(String message);
}