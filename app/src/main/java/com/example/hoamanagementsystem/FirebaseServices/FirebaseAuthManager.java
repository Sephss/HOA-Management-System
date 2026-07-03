package com.example.hoamanagementsystem.FirebaseServices;

import com.example.hoamanagementsystem.FirebaseServices.callback.LoginUserCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.RegisterHomeownerRenterCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserDatasCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class FirebaseAuthManager {
    private static FirebaseAuth firebaseAuth;

    public static FirebaseAuth getAuth() {
        if(firebaseAuth == null) {
            firebaseAuth = FirebaseAuth.getInstance();
        }
        return firebaseAuth;
    }
    public static void loginUser(String email, String password, LoginUserCallback callback) {
        getAuth().signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                String uid = getCurrentUserUid();
                if(uid != null) {
                    FirebaseDatabaseManager.getUserDatas(uid, new UserDatasCallback() {
                        @Override
                        public void onSuccess(HomeOwnerRentersModel user) {
                            callback.onSuccess(getCurrentUser(), user);
                        }

                        @Override
                        public void onFailure(String message) {
                            callback.onFailure(message);
                        }
                    });
                } else {
                    callback.onFailure("Failed to get user UID.");
                }
            } else {
                callback.onFailure("Incorrect email or password");
            }
        }).addOnFailureListener(failedTask -> {
            callback.onFailure(failedTask.getMessage());
        });
    }
    public static void signupUser (String email, String password, HomeOwnerRentersModel homeOwnerRentersModel, RegisterHomeownerRenterCallback callback) {
        getAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()) {
                FirebaseUser firebaseUser = getAuth().getCurrentUser();

                if(firebaseUser != null) {
                    String uid = firebaseUser.getUid();

                    homeOwnerRentersModel.setUid(uid);

                    FirebaseDatabaseManager.saveUser(homeOwnerRentersModel, new RegisterHomeownerRenterCallback() {
                        @Override
                        public void onSuccess(String success) {
                            callback.onSuccess(success);
                        }

                        @Override
                        public void onFailure(String failed) {
                            firebaseUser.delete().addOnCompleteListener(deleteTask -> {
                                callback.onFailure(failed);
                            });

                        }
                    });
                } else {
                    callback.onFailure("Failed to get user UID");
                }
            } else {
                callback.onFailure(
                        task.getException() != null
                                ? task.getException().getMessage()
                                : "Registration failed.");
            }
        }).addOnFailureListener(e -> {
            callback.onFailure(e.getMessage());
        });
    }
    public static FirebaseUser getCurrentUser() {
        return getAuth().getCurrentUser();
    }
    public static String getCurrentUserUid() {
        FirebaseUser user = getCurrentUser();
        return (user != null) ? user.getUid() : null;
    }
    public static void logout() {
        if (firebaseAuth != null) {
            firebaseAuth.signOut();
        }
    }
}
