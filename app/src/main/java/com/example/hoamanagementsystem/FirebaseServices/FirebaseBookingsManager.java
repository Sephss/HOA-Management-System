package com.example.hoamanagementsystem.FirebaseServices;

import androidx.annotation.NonNull;

import com.example.hoamanagementsystem.FirebaseServices.callback.CreateBookingsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetBookingsCallback;
import com.example.hoamanagementsystem.Model.BookingsModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
}
