package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAnnouncementManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchAnnouncementsCallback;
import com.example.hoamanagementsystem.MainActivity;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.example.hoamanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private LinearLayout sampleLogout, announcementLink;
    private RecyclerView announcementRV;
    private com.example.hoamanagementsystem.Adapter.AnnouncementAdapter announcementAdapter;
    private ArrayList<AnnouncementModel> announcementList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        sampleLogout= findViewById(R.id.sampleLogout);
        announcementLink = findViewById(R.id.announcementLink);

        announcementRV  = findViewById(R.id.announcementRV);
        announcementList = new ArrayList<>();

        announcementAdapter = new com.example.hoamanagementsystem.Adapter.AnnouncementAdapter(this, announcementList);
        announcementRV.setLayoutManager(new LinearLayoutManager(this));
        announcementRV.setAdapter(announcementAdapter);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        sampleLogout.setOnClickListener(d -> {
            FirebaseAuthManager.logout();
            Intent intent = new Intent(HomePage.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        announcementLink.setOnClickListener(l -> {
            navigateTo(CreateAnnouncementPage.class);
        });
        fetchAnnouncements();
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