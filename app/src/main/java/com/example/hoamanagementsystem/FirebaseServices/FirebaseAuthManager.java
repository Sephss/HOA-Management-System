package com.example.hoamanagementsystem.FirebaseServices;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseAuthManager {
    private static FirebaseAuth firebaseAuth;

    public static FirebaseAuth getAuth() {
        if(firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }
}
