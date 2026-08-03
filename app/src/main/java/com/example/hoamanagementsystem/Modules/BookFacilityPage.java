package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseBookingsManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetBookingsCallback;
import com.example.hoamanagementsystem.Model.BookingsModel;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.adapters.BookingsAdapter;

import java.util.ArrayList;
import java.util.List;

public class BookFacilityPage extends AppCompatActivity {
    private Button newBookingBtn;
    private HomeOwnerRentersModel currentUser;
    private RecyclerView reservationsRV;
    private BookingsAdapter bookingsAdapter;
    private List<BookingsModel> bookingsList = new ArrayList<>();
    private ImageView backBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_book_facility_page);
        reservationsRV = findViewById(R.id.reservationsRV);
        backBtn = findViewById(R.id.backBtn);
        newBookingBtn = findViewById(R.id.newBookingBtn);
        currentUser = UserSession.getInstance().getCurrentUser();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        reservationsRV.setLayoutManager(new LinearLayoutManager(this));
        bookingsAdapter = new BookingsAdapter(this, bookingsList);
        reservationsRV.setAdapter(bookingsAdapter);

        if(currentUser.getRole().equals("Home Owners") || currentUser.getRole().equals("Renters")) {
            newBookingBtn.setVisibility(View.VISIBLE);
        }

        newBookingBtn.setOnClickListener(s -> {
            Intent intent = new Intent(BookFacilityPage.this, CreateBookingPage.class);
            startActivity(intent);
        });

        backBtn.setOnClickListener( d -> {
            finish();
        });

        loadMyBookings();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMyBookings(); // refresh in case user just came back from creating a booking
    }

    private void loadMyBookings() {
        FirebaseBookingsManager.getMyBookings(currentUser.getUid(), new GetBookingsCallback() {
            @Override
            public void onSuccess(List<BookingsModel> bookings) {
                bookingsList.clear();
                bookingsList.addAll(bookings);
                bookingsAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(BookFacilityPage.this, "Failed to load reservations", Toast.LENGTH_SHORT).show();
            }
        });
    }
}