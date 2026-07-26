package com.example.hoamanagementsystem.utls;

import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class AppSettingsManager {
    public interface OperatingHoursCallback {
        void onResult(boolean isOpen, int openHour, int closeHour);
        void onFailure(String error);
    }

    public static void checkOperatingHours(OperatingHoursCallback callback) {
        FirebaseDatabase.getInstance().getReference("appSettings/operatingHours")
                .get()
                .addOnSuccessListener(snapshot -> {
                    Integer openHour = snapshot.child("openHour").getValue(Integer.class);
                    Integer closeHour = snapshot.child("closeHour").getValue(Integer.class);

                    // Fallback defaults if database values are missing/null
                    if (openHour == null) openHour = 6;
                    if (closeHour == null) closeHour = 17;

                    int currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

                    boolean isOpen = currentHour >= openHour && currentHour < closeHour;
                    callback.onResult(isOpen, openHour, closeHour);
                })
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
}

