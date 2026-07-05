package com.example.hoamanagementsystem.Session;

import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;

public class UserSession {
    private static UserSession instance;
    private HomeOwnerRentersModel currentUser;

    private UserSession() {}

    public static UserSession getInstance() {
        if(instance == null) {
            instance = new UserSession();
        }
        return instance;
    }

    public void setCurrentUser(HomeOwnerRentersModel user) {
        this.currentUser = user;
    }

    public HomeOwnerRentersModel getCurrentUser() {
        return currentUser;
    }

    public void clearSession() {
        currentUser = null;
    }
}