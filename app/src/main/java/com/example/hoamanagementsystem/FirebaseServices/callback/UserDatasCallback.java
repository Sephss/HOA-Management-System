package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;

public interface UserDatasCallback {
    void onSuccess(HomeOwnerRentersModel user);
    void onFailure(String message);
}
