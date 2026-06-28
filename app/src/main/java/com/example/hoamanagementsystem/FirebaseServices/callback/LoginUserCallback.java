package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.google.firebase.auth.FirebaseUser;

public interface LoginUserCallback {
    void onSuccess(FirebaseUser user, String role);
    void onFailure(String failed);
}
