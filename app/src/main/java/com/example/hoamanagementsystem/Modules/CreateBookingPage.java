package com.example.hoamanagementsystem.Modules;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseBookingSlotManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseBookingsManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateBookingsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetTakenSlotsCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.InsertBookingSlotCallback;
import com.example.hoamanagementsystem.Model.BookingSlotModel;
import com.example.hoamanagementsystem.Model.BookingsModel;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;

public class CreateBookingPage extends AppCompatActivity {
    private Spinner sportCategorySpinner;
    private EditText bookingPurpose, bookingRemarks;
    private Button submitBookingRequestButton;
    private HomeOwnerRentersModel currentUser;
    private LinearLayout datePickerField;
    private LinearLayout slotsContainer;
    private TextView selectedDateText;
    private String selectedDate = "";
    private String selectedSlot = "";
    private ImageView backBtn;

    private final List<String> allSlots = Arrays.asList(
            "8:00 AM - 11:00 AM",
            "11:00 AM - 2:00 PM",
            "2:00 PM - 5:00 PM",
            "5:00 PM - 8:00 PM"
    );
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_booking_page);
        sportCategorySpinner = findViewById(R.id.sportCategorySpinner);
        bookingPurpose = findViewById(R.id.bookingPurpose);
        bookingRemarks = findViewById(R.id.bookingRemarks);
        submitBookingRequestButton = findViewById(R.id.submitBookingRequestButton);
        selectedDateText = findViewById(R.id.selectedDateText);
        datePickerField = findViewById(R.id.datePickerField);
        slotsContainer = findViewById(R.id.slotsContainer);
        backBtn = findViewById(R.id.backBtn);
        currentUser = UserSession.getInstance().getCurrentUser();
      
        setupSpinner();
        backBtn.setOnClickListener(d -> {
            finish();
        });
        datePickerField.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog datePickerDialog = new DatePickerDialog(
                    this,
                    (view, year, month, dayOfMonth) -> {
                        Calendar pickedDate = Calendar.getInstance();
                        pickedDate.set(year, month, dayOfMonth);

                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
                        selectedDate = dateFormat.format(pickedDate.getTime());

                        selectedDateText.setText(selectedDate);
                        selectedDateText.setTextColor(ContextCompat.getColor(this, R.color.black));

                        selectedSlot = ""; // reset previous slot pick when date changes
                        tryLoadSlots();
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            datePickerDialog.show();
        });

        submitBookingRequestButton.setOnClickListener(s -> {
            submitRequest();
        });
    }

    private void tryLoadSlots() {
        String sportCategory = sportCategorySpinner.getSelectedItem().toString();

        if (sportCategory.equals("Select Sport") || selectedDate.isEmpty()) {
            return; // wait until both sport and date are chosen
        }

        loadAvailableSlots(sportCategory, selectedDate);
    }

    private void loadAvailableSlots(String sportCategory, String date) {
        slotsContainer.removeAllViews();
        selectedSlot = "";

        FirebaseBookingSlotManager.getTakenSlots(sportCategory, date, new GetTakenSlotsCallback() {
            @Override
            public void onResult(List<String> takenSlots) {
                renderSlotChips(takenSlots);
            }

            @Override
            public void onFailure(String message) {
                Log.e("BookingSlots", message);
                Toast.makeText(CreateBookingPage.this, message, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void renderSlotChips(List<String> takenSlots) {
        slotsContainer.removeAllViews();

        for (String slot : allSlots) {
            boolean isTaken = takenSlots.contains(slot);

            LinearLayout chip = new LinearLayout(this);
            chip.setOrientation(LinearLayout.VERTICAL);
            chip.setGravity(Gravity.CENTER);
            chip.setPadding(30, 30, 30, 30);
            chip.setBackgroundResource(R.drawable.document_request_bg);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            params.setMargins(10, 10, 10, 10);
            chip.setLayoutParams(params);

            TextView label = new TextView(this);
            label.setText(slot);
            label.setTextSize(14);

            if (isTaken) {
                chip.setAlpha(0.4f);
                label.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
                chip.setEnabled(false);
            } else {
                label.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
                chip.setOnClickListener(v -> {
                    selectedSlot = slot;
                    highlightSelectedChip(chip, label);
                });
            }

            chip.addView(label);
            slotsContainer.addView(chip);
        }
    }

    private void highlightSelectedChip(LinearLayout selectedChip, TextView label) {
        for (int i = 0; i < slotsContainer.getChildCount(); i++) {
            View child = slotsContainer.getChildAt(i);
            child.setBackgroundResource(R.drawable.document_request_bg);

            if (child instanceof LinearLayout) {
                LinearLayout chip = (LinearLayout) child;
                if (chip.getChildCount() > 0 && chip.getChildAt(0) instanceof TextView) {
                    TextView chipLabel = (TextView) chip.getChildAt(0);
                    chipLabel.setTextColor(ContextCompat.getColor(this, R.color.darkergrey));
                }
            }
        }

        selectedChip.setBackgroundResource(R.drawable.rounded_background);
        label.setTextColor(ContextCompat.getColor(this, R.color.white));
    }

    private void submitRequest() {
        String sportCategory = sportCategorySpinner.getSelectedItem().toString();
        String purpose = bookingPurpose.getText().toString();
        String remarks = bookingRemarks.getText().toString();

        if (sportCategory.equals("Select Sport")) {
            Toast.makeText(this, "Please select a sport category.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedDate.isEmpty()) {
            Toast.makeText(this, "Please select a date.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (selectedSlot.isEmpty()) {
            Toast.makeText(this, "Please select a time slot.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (purpose.isEmpty()) {
            bookingPurpose.setError("Please enter a purpose.");
            return;
        }



        setLoadingState();

        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy", Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        String currentDate = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a", Locale.ENGLISH);
        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));
        String currentTime = timeFormat.format(new Date());

        long timestamp = System.currentTimeMillis();
        String theTimeStamp = String.valueOf(timestamp);

        String[] slotParts = selectedSlot.split(" - ");
        String timeIn = slotParts[0];
        String timeOut = slotParts[1];

        String bookerName = currentUser.getFirstName() + " " + currentUser.getLastName();

        // Generate the shared bookingID upfront
        String bookingID = FirebaseDatabase.getInstance().getReference("Bookings").push().getKey();

        if (bookingID == null) {
            setNormalState();
            Toast.makeText(this, "Failed to generate booking ID. Please try again.", Toast.LENGTH_SHORT).show();
            return;
        }

        BookingSlotModel slotData = new BookingSlotModel(
                bookingID, bookerName, sportCategory, selectedDate, selectedSlot, timeIn, timeOut
        );

        // Step 1: reserve the slot FIRST (fails fast if someone already took it)
        FirebaseBookingSlotManager.insertBookingSlot(sportCategory, selectedDate, selectedSlot, slotData,
                new InsertBookingSlotCallback() {
                    @Override
                    public void onSuccess() {

                        BookingsModel data = new BookingsModel(
                                "", currentUser.getUid(), "confirmed", purpose, remarks,
                                bookerName, sportCategory, currentDate, currentTime,
                                selectedDate, timeIn, timeOut, theTimeStamp, "", "", "", ""
                        );

                        // Step 2: slot is secured, now create the Bookings record with the same ID
                        FirebaseBookingsManager.createBookingWithId(bookingID, data, new CreateBookingsCallback() {
                            @Override
                            public void onSuccess(String bookingID, String message) {
                                setNormalState();
                                navigateTo(BookingRequestSuccess.class);
                                finish();
                            }

                            @Override
                            public void onFailure(String error) {
                                // Slot was reserved but booking record failed to save
                                // Roll back the slot reservation so it doesn't stay stuck as "taken"
                                FirebaseBookingSlotManager.removeBookingSlot(sportCategory, selectedDate, selectedSlot);

                                setNormalState();
                                Toast.makeText(CreateBookingPage.this,
                                        "Something went wrong saving your booking. Please try again.",
                                        Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                    @Override
                    public void onFailure(String slotError) {
                        setNormalState();
                        Toast.makeText(CreateBookingPage.this, slotError, Toast.LENGTH_SHORT).show();
                        // Refresh slots so the UI reflects the slot is now taken by someone else
                        loadAvailableSlots(sportCategory, selectedDate);
                    }
                });
    }

    private void setupSpinner() {

        List<String> sportsList = Arrays.asList(
                "Select Sport",
                "Basketball",
                "Volleyball",
                "Chess",
                "Zumba",
                "Puregold Bazaar",
                "Community Event",
                "Emergency / Repair",
                "Holy Mass",
                "General Assembly"
        );

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,   android.R.layout.simple_spinner_item,
                sportsList
        );

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sportCategorySpinner.setAdapter(adapter);

        sportCategorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedSlot = "";
                tryLoadSlots();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void setLoadingState() {
        submitBookingRequestButton.setEnabled(false);
        submitBookingRequestButton.setAlpha(0.5f);
        submitBookingRequestButton.setText("SUBMITTING...");
    }

    private void setNormalState() {
        submitBookingRequestButton.setEnabled(true);
        submitBookingRequestButton.setAlpha(1f);
        submitBookingRequestButton.setText("SUBMIT BOOKING REQUEST");
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
}