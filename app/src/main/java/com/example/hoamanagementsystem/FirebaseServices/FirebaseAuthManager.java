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
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = getCurrentUser();

                if (firebaseUser != null) {
                    // Force refresh to get the latest verification status from Firebase servers
                    firebaseUser.reload().addOnCompleteListener(reloadTask -> {
                        if (reloadTask.isSuccessful()) {
                            if (!firebaseUser.isEmailVerified()) {
                                getAuth().signOut(); // don't leave them signed in unverified
                                callback.onFailure("Please verify your email before logging in.");
                                return;
                            }

                            String uid = firebaseUser.getUid();
                            FirebaseDatabaseManager.getUserDatas(uid, new UserDatasCallback() {
                                @Override
                                public void onSuccess(HomeOwnerRentersModel user) {
                                    callback.onSuccess(firebaseUser, user);
                                }

                                @Override
                                public void onFailure(String message) {
                                    callback.onFailure(message);
                                }
                            });
                        } else {
                            callback.onFailure("Failed to refresh user status.");
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
    public static void signupUser(String email, String password, HomeOwnerRentersModel homeOwnerRentersModel, RegisterHomeownerRenterCallback callback) {
        getAuth().createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                FirebaseUser firebaseUser = getAuth().getCurrentUser();

                if (firebaseUser != null) {
                    String uid = firebaseUser.getUid();

                    homeOwnerRentersModel.setUid(uid);

                    FirebaseDatabaseManager.saveUser(homeOwnerRentersModel, new RegisterHomeownerRenterCallback() {
                        @Override
                        public void onSuccess(String success) {
                            // Data saved successfully, now send verification email
                            firebaseUser.sendEmailVerification().addOnCompleteListener(verifyTask -> {
                                if (verifyTask.isSuccessful()) {
                                    callback.onSuccess(success);
                                } else {
                                    // Account + data exist, but verification email failed to send.
                                    // Don't roll back the account for this — just let the caller know.
                                    callback.onFailure("Account created but failed to send verification email: " +
                                            (verifyTask.getException() != null
                                                    ? verifyTask.getException().getMessage()
                                                    : "Unknown error"));
                                }
                            });
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
    public static void resendEmailVerification(RegisterHomeownerRenterCallback callback) {
        FirebaseUser firebaseUser = getAuth().getCurrentUser();

        if (firebaseUser == null) {
            callback.onFailure("No user is currently signed in.");
            return;
        }

        firebaseUser.sendEmailVerification().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                callback.onSuccess("Verification email sent. Please check your inbox.");
            } else {
                callback.onFailure(
                        task.getException() != null
                                ? task.getException().getMessage()
                                : "Failed to resend verification email."
                );
            }
        });
    }
}
