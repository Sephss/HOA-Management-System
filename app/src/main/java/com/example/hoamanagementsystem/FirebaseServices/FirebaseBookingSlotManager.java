package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.GetTakenSlotsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.InsertBookingSlotCallback;
import com.example.hoamanagementsystem.Model.BookingSlotModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FirebaseBookingSlotManager {

    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("BookingSlots");
    }

    public static void getTakenSlots(String sportCategory, String date, GetTakenSlotsCallback callback) {

        DatabaseReference dateRef = getDatabase()
                .child(sportCategory)
                .child(date);

        dateRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<String> takenSlots = new ArrayList<>();

                for (DataSnapshot child : snapshot.getChildren()) {
                    takenSlots.add(child.getKey());
                }

                callback.onResult(takenSlots);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }

    public static void insertBookingSlot(String sportCategory, String date, String slot, BookingSlotModel slotData, InsertBookingSlotCallback callback) {

        DatabaseReference slotRef = getDatabase()
                .child(sportCategory)
                .child(date)
                .child(slot);

        slotRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    callback.onFailure("This time slot was just taken. Please choose another.");
                    return;
                }

                slotRef.setValue(slotData)
                        .addOnSuccessListener(unused -> callback.onSuccess())
                        .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onFailure(error.getMessage());
            }
        });
    }
    public static void removeBookingSlot(String sportCategory, String date, String slot) {
        getDatabase()
                .child(sportCategory)
                .child(date)
                .child(slot)
                .removeValue();
    }
}