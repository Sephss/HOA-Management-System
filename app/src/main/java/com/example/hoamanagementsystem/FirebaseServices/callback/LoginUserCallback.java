package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.google.firebase.auth.FirebaseUser;

public interface LoginUserCallback {
    void onSuccess(FirebaseUser firebaseUser,
                   HomeOwnerRentersModel user);

    void onFailure(String failed);
}