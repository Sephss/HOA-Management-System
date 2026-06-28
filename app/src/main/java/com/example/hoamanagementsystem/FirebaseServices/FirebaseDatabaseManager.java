package com.example.hoamanagementsystem.FirebaseServices;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.RegisterHomeownerRenterCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.RoleCallback;
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
    public static void getUserRole(String uid, RoleCallback roleCallback) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users").child(uid).child("role");

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
              if(snapshot.exists()) {
                  String role = snapshot.getValue(String.class);
                  roleCallback.onSuccess(role);
              } else {
                  roleCallback.onFailure("Role not found");
              }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                roleCallback.onFailure(error.getMessage());
            }
        });
    }
}
