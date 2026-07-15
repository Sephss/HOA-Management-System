package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;

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
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.adapters.AnnouncementAdapter;

import java.util.ArrayList;
import java.util.List;

public class AnnouncementPage extends AppCompatActivity {
    private Button newAnnouncementBtn;
    private RecyclerView announcementsRV;
    private ArrayList<AnnouncementModel> announcementList;
    private AnnouncementAdapter announcementAdapter;
    private LinearLayout homePageLink, profilePage;
    private HomeOwnerRentersModel currentUser;
    private String theRole;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_announcement_page);

        newAnnouncementBtn = findViewById(R.id.newAnnouncementBtn);
        announcementsRV = findViewById(R.id.announcementsRV);
        announcementList = new ArrayList<>();

        announcementAdapter = new AnnouncementAdapter(this, announcementList);
        announcementsRV.setLayoutManager(new LinearLayoutManager(this));
        announcementsRV.setAdapter(announcementAdapter);

        homePageLink = findViewById(R.id.homePageLink);
        profilePage = findViewById(R.id.profilePage);

        currentUser = UserSession.getInstance().getCurrentUser();

        theRole = currentUser.getRole();

        if(theRole.equals("Home Owners") || theRole.equals("Renters")) {
            getWindow().setFlags(
                    WindowManager.LayoutParams.FLAG_SECURE,
                    WindowManager.LayoutParams.FLAG_SECURE
            );
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        fetchAnnouncements();
        setupAnnouncementBtn();
        homePageLink.setOnClickListener(d -> {
            finish();
            overridePendingTransition(0, 0);
        });

        profilePage.setOnClickListener(d -> {
            Intent intent = new Intent(AnnouncementPage.this, ProfilePage.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });

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
    private void setupAnnouncementBtn() {
        String role = currentUser.getRole();

        if(role.equals("Home Owners") || role.equals("Renters")) {
            newAnnouncementBtn.setVisibility(View.GONE);
        } else {
            newAnnouncementBtn.setVisibility(View.VISIBLE);
        }
    }
}