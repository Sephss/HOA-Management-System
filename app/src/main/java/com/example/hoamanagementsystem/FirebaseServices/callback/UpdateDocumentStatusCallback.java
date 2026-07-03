package com.example.hoamanagementsystem.FirebaseServices.callback;

public interface UpdateDocumentStatusCallback {
    void onSuccess(String message);
    void onFailure(String error);
}