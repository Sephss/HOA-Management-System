package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.DocumentRequestModel;

import java.util.List;

public interface GetDocumentRequestsCallback {
    void onSuccess(List<DocumentRequestModel> requests);
    void onFailure(String error);
}