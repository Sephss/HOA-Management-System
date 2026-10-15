package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.UserProfileModel;

public interface UserProfileCallback {
    void onSuccess(UserProfileModel profile);

    void onFailure(String message);
}