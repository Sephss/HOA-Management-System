package com.example.hoamanagementsystem.FirebaseServices;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.RegisterHomeownerRenterCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UserDatasCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class FirebaseDatabaseManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("users");
    }

    public static void saveUser(HomeOwnerRentersModel homeOwnerRentersModel, RegisterHomeownerRenterCallback callback) {

        getDatabase().child(homeOwnerRentersModel.getUid()).setValue(homeOwnerRentersModel).addOnSuccessListener(task -> {
            Log.d(TAG, "User saved successfully");
            callback.onSuccess("User saved successfully");
        }).addOnFailureListener(g -> {
            Log.d(TAG, "User failed to save");
            callback.onFailure("User failed to save");
        });
    }
    public static void getUserDatas(String uid, UserDatasCallback callback) {
        DatabaseReference ref = FirebaseDatabase.getInstance()
                .getReference("users")
                .child(uid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {

                    HomeOwnerRentersModel user =
                            snapshot.getValue(HomeOwnerRentersModel.class);

                    if (user != null) {
                        callback.onSuccess(user);
                    } else {
                        callback.onFailure("Failed to parse user data");
                    }

                } else {
                    callback.onFailure("User not found");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
}
