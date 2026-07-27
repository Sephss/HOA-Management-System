package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateBookingsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetBookingsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.UpdateBookingStatusCallback;
import com.example.hoamanagementsystem.Model.BookingsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirebaseBookingsManager {
    private static DatabaseReference getDatabase() {
        return FirebaseDatabase.getInstance().getReference("Bookings");
    }
    public static void createBooking(BookingsModel data, CreateBookingsCallback callback) {
        String bookingID = getDatabase().push().getKey();

        if (bookingID == null) {
            callback.onFailure("Failed to generate bookingID");
            return;
        }

        data.setBookingID(bookingID);
        getDatabase().child(bookingID).setValue(data).addOnCompleteListener(task -> {
            callback.onSuccess(bookingID,"Booking request submitted successfully!");
        }).addOnFailureListener(failed -> {
            callback.onFailure(failed.getMessage());
        });
    }
    public static void createBookingWithId(String bookingID, BookingsModel data, CreateBookingsCallback callback) {
        data.setBookingID(bookingID);
        getDatabase().child(bookingID).setValue(data).addOnCompleteListener(task -> {
            callback.onSuccess(bookingID, "Booking request submitted successfully!");
        }).addOnFailureListener(failed -> {
            callback.onFailure(failed.getMessage());
        });
    }
    public static void getMyBookings(String bookerID, GetBookingsCallback callback) {

        getDatabase().orderByChild("bookerID").equalTo(bookerID)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<BookingsModel> bookings = new ArrayList<>();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            BookingsModel booking = child.getValue(BookingsModel.class);
                            if (booking != null) {
                                bookings.add(booking);
                            }
                        }

                        // newest first
                        Collections.sort(bookings, (a, b) ->
                                Long.compare(Long.parseLong(b.getTimestamp()), Long.parseLong(a.getTimestamp())));

                        callback.onSuccess(bookings);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
    public static void cancelBooking(String bookingID, UpdateBookingStatusCallback callback) {
        getDatabase().child(bookingID).child("bookingStatus").setValue("cancelled")
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
    public static void cancelBookingAdmin(String bookingID, String adminRemarks, UpdateBookingStatusCallback callback) {
        Map<String, Object> updates = new HashMap<>();
        updates.put("bookingStatus", "cancelled");
        updates.put("adminRemarks", adminRemarks);

        getDatabase().child(bookingID).updateChildren(updates)
                .addOnSuccessListener(v -> callback.onSuccess())
                .addOnFailureListener(e -> callback.onFailure(e.getMessage()));
    }
    public static void getBookingsByDate(String date, GetBookingsCallback callback) {

        getDatabase().orderByChild("requestBookingDate").equalTo(date)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<BookingsModel> bookings = new ArrayList<>();

                        for (DataSnapshot child : snapshot.getChildren()) {
                            BookingsModel booking = child.getValue(BookingsModel.class);
                            if (booking != null && !"cancelled".equalsIgnoreCase(booking.getBookingStatus())) {
                                bookings.add(booking);
                            }
                        }

                        // sort by time in (earliest first)
                        Collections.sort(bookings, (a, b) ->
                                a.getRequestBookingTimeIn().compareTo(b.getRequestBookingTimeIn()));

                        callback.onSuccess(bookings);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onFailure(error.getMessage());
                    }
                });
    }
}
