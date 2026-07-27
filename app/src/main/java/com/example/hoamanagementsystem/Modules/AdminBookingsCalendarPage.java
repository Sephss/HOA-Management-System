package com.example.hoamanagementsystem.Modules;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseBookingsManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetBookingsCallback;
import com.example.hoamanagementsystem.Model.AdminBookingListItem;
import com.example.hoamanagementsystem.Model.BookingsModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.adapters.AdminBookingsAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class AdminBookingsCalendarPage extends AppCompatActivity {

    private View backButton;
    private LinearLayout datePickerField;
    private TextView selectedDateText, resultsCountText;
    private RecyclerView bookingsRV;
    private LinearLayout emptyStateLayout;

    private AdminBookingsAdapter bookingsAdapter;
    private List<AdminBookingListItem> displayItems = new ArrayList<>();
    private String selectedDate = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_bookings_calendar_page);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        bindViews();

        backButton.setOnClickListener(v -> finish());

        bookingsRV.setLayoutManager(new LinearLayoutManager(this));
        bookingsAdapter = new AdminBookingsAdapter(this, displayItems);
        bookingsRV.setAdapter(bookingsAdapter);

        DividerItemDecoration divider = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        bookingsRV.addItemDecoration(divider);

        datePickerField.setOnClickListener(v -> openDatePicker());

        // Default to today
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        selectedDate = dateFormat.format(new Date());
        selectedDateText.setText(selectedDate);

        loadBookingsForDate(selectedDate);
    }

    private void bindViews() {
        backButton = findViewById(R.id.backButton);
        datePickerField = findViewById(R.id.datePickerField);
        selectedDateText = findViewById(R.id.selectedDateText);
        resultsCountText = findViewById(R.id.resultsCountText);
        bookingsRV = findViewById(R.id.bookingsRV);
        emptyStateLayout = findViewById(R.id.emptyStateLayout);
    }

    private void openDatePicker() {
        Calendar calendar = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    Calendar pickedDate = Calendar.getInstance();
                    pickedDate.set(year, month, dayOfMonth);

                    SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
                    selectedDate = dateFormat.format(pickedDate.getTime());
                    selectedDateText.setText(selectedDate);

                    loadBookingsForDate(selectedDate);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private void loadBookingsForDate(String date) {
        FirebaseBookingsManager.getBookingsByDate(date, new GetBookingsCallback() {
            @Override
            public void onSuccess(List<BookingsModel> bookings) {
                displayItems.clear();

                // group bookings by sport, preserving first-seen order
                LinkedHashMap<String, List<BookingsModel>> grouped = new LinkedHashMap<>();
                for (BookingsModel booking : bookings) {
                    String sport = booking.getBookerSport();
                    if (!grouped.containsKey(sport)) {
                        grouped.put(sport, new ArrayList<>());
                    }
                    grouped.get(sport).add(booking);
                }

                for (Map.Entry<String, List<BookingsModel>> entry : grouped.entrySet()) {
                    displayItems.add(AdminBookingListItem.header(entry.getKey()));
                    for (BookingsModel booking : entry.getValue()) {
                        displayItems.add(AdminBookingListItem.booking(booking));
                    }
                }

                bookingsAdapter.notifyDataSetChanged();

                resultsCountText.setText(bookings.size() + (bookings.size() == 1 ? " reservation" : " reservations"));

                if (bookings.isEmpty()) {
                    bookingsRV.setVisibility(View.GONE);
                    emptyStateLayout.setVisibility(View.VISIBLE);
                } else {
                    bookingsRV.setVisibility(View.VISIBLE);
                    emptyStateLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String message) {
                Toast.makeText(AdminBookingsCalendarPage.this, "Failed to load reservations", Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onResume() {
        super.onResume();
        // refresh in case admin cancelled a booking and came back
        if (!selectedDate.isEmpty()) {
            loadBookingsForDate(selectedDate);
        }
    }
}