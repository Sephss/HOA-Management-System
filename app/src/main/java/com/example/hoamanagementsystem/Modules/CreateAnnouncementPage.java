package com.example.hoamanagementsystem.Modules;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAnnouncementManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateAnnouncementCallback;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.example.hoamanagementsystem.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class CreateAnnouncementPage extends AppCompatActivity {
    private EditText titleET, descriptionET, linkET, dateET, timeET;
    private Spinner categorySpinner;
    private Button publishAnnouncementBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_create_announcement_page);

        titleET = findViewById(R.id.titleET);
        descriptionET = findViewById(R.id.descriptionET);
        linkET = findViewById(R.id.linkET);
        categorySpinner = findViewById(R.id.categorySpinner);
        dateET = findViewById(R.id.dateET);
        timeET = findViewById(R.id.timeET);

        publishAnnouncementBtn = findViewById(R.id.publishAnnouncementBtn);


        setupSpinner();
        dateET.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(
                    this,
                    (view, year, month, day) -> {
                        String date = (month + 1) + "/" + day + "/" + year;
                        dateET.setText(date);
                    },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            );

            dialog.show();
        });

        timeET.setOnClickListener(v -> {

            Calendar calendar = Calendar.getInstance();

            TimePickerDialog dialog = new TimePickerDialog(
                    this,
                    (view, hour, minute) -> {

                        Calendar selectedTime = Calendar.getInstance();
                        selectedTime.set(Calendar.HOUR_OF_DAY, hour);
                        selectedTime.set(Calendar.MINUTE, minute);

                        SimpleDateFormat sdf = new SimpleDateFormat(
                                "h:mm a",
                                Locale.ENGLISH
                        );

                        String formattedTime =
                                sdf.format(selectedTime.getTime());

                        timeET.setText(formattedTime);
                    },
                    calendar.get(Calendar.HOUR_OF_DAY),
                    calendar.get(Calendar.MINUTE),
                    false
            );

            dialog.show();
        });

        publishAnnouncementBtn.setOnClickListener(s -> {
            createAnnouncement();
        });
    }
    private void createAnnouncement() {
        String title = titleET.getText().toString();
        String description = descriptionET.getText().toString();
        String category = categorySpinner.getSelectedItem().toString();
        String date = dateET.getText().toString();
        String time = timeET.getText().toString();
        String link = linkET.getText().toString();

        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "MMMM dd, yyyy",
                Locale.ENGLISH
        );

        dateFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        String currentDate = dateFormat.format(new Date());

        SimpleDateFormat timeFormat = new SimpleDateFormat(
                "hh:mm a",
                Locale.ENGLISH
        );

        timeFormat.setTimeZone(TimeZone.getTimeZone("Asia/Manila"));

        String currentTime = timeFormat.format(new Date());

        long timestamp = System.currentTimeMillis();

        if(title.isEmpty()) {
            titleET.setError("Title is required");
            titleET.requestFocus();
            return;
        }
        if(description.isEmpty()) {
            descriptionET.setError("Description is required");
            descriptionET.requestFocus();
            return;
        }
        if(category.equals("Select")) {
            Toast.makeText(this, "Category is required", Toast.LENGTH_SHORT).show();
            categorySpinner.requestFocus();
            return;
        }
        if(date.isEmpty()) {
            dateET.setError("Date is required");
            dateET.requestFocus();
            return;
        }
        if(time.isEmpty()) {
            timeET.setError("Time is required");
            timeET.requestFocus();
            return;
        }

        if (!link.isEmpty()) {

            Uri uri = Uri.parse(link);

            boolean isValid =
                    (uri.getScheme() != null) &&
                            (uri.getHost() != null) &&
                            (uri.getScheme().equals("http") || uri.getScheme().equals("https"));

            if (!isValid) {
                linkET.setError("Enter a valid URL starting with http:// or https://");
                linkET.requestFocus();
                return;
            }
        }


        setLoadingState();

        AnnouncementModel details = new AnnouncementModel(title, description, category, date, time, link, "", "", "", "", currentDate, currentTime, timestamp);

        FirebaseAnnouncementManager.createAnnouncement(details, new CreateAnnouncementCallback() {
            @Override
            public void onSuccess(String success) {
                Toast.makeText(CreateAnnouncementPage.this, success, Toast.LENGTH_SHORT).show();
                setNormalState();
                finish();
            }

            @Override
            public void onFailure(String failed) {
                Toast.makeText(CreateAnnouncementPage.this, failed, Toast.LENGTH_SHORT).show();
                setNormalState();
            }
        });

    }
    private void setupSpinner() {
        String [] announcementType = {
                "Select",
                "General",
                "Meeting",
                "Maintenance",
                "Grievance"
        };

        ArrayAdapter<String> announcementTypeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                announcementType
        );
        announcementTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(announcementTypeAdapter);
    }
    private void setLoadingState() {
        publishAnnouncementBtn.setEnabled(false);
        publishAnnouncementBtn.setAlpha(0.5f);
        publishAnnouncementBtn.setText("Announcing...");
    }

    private void setNormalState() {
        publishAnnouncementBtn.setEnabled(true);
        publishAnnouncementBtn.setAlpha(1f);
        publishAnnouncementBtn.setText("Publish announcement");
    }
}