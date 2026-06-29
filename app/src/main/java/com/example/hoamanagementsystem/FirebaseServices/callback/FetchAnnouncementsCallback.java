package com.example.hoamanagementsystem.FirebaseServices.callback;

import com.example.hoamanagementsystem.Model.AnnouncementModel;

import java.util.List;

public interface FetchAnnouncementsCallback {
    void onSuccess(List<AnnouncementModel> announcements);
    void onFailure(String message);
}