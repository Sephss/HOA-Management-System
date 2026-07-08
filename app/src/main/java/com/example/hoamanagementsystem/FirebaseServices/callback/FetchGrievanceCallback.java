package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.GrievanceModel;

import java.util.List;

public interface FetchGrievanceCallback {
    void onSuccess(List<GrievanceModel> grievances);
    void onFailure(String message);
}
