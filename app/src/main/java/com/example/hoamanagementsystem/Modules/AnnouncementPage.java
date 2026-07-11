package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAnnouncementManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchAnnouncementsCallback;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.example.hoamanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementPage extends AppCompatActivity {
    private Button newAnnouncementBtn;
    private RecyclerView announcementsRV;
    private ArrayList<AnnouncementModel> announcementList;
    private com.example.hoamanagementsystem.Adapter.AnnouncementAdapter announcementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announcement_page);

        newAnnouncementBtn = findViewById(R.id.newAnnouncementBtn);
        announcementsRV = findViewById(R.id.announcementsRV);
        announcementList = new ArrayList<>();

        announcementAdapter = new com.example.hoamanagementsystem.Adapter.AnnouncementAdapter(this, announcementList);
        announcementsRV.setLayoutManager(new LinearLayoutManager(this));
        announcementsRV.setAdapter(announcementAdapter);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchAnnouncements();

        newAnnouncementBtn.setOnClickListener(s -> {
            navigateTo(CreateAnnouncementPage.class);
        });
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
    private void fetchAnnouncements() {

        FirebaseAnnouncementManager.fetchAnnouncements(new FetchAnnouncementsCallback() {
            @Override
            public void onSuccess(List<AnnouncementModel> announcements) {

                announcementList.clear();
                announcementList.addAll(announcements);

                announcementAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(String message) {

            }
        });
    }
}