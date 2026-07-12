package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.NotificationModel;

import java.util.List;

public interface GetNotificationCallback {

    void onSuccess(List<NotificationModel> notifications);

    void onFailure(String error);
}