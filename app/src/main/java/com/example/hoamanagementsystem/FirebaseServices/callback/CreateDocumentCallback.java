package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.google.firebase.auth.FirebaseUser;

public interface CreateDocumentCallback {
    void onSuccess(String success);
    void onFailure(String failed);
}
