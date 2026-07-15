package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAnnouncementManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseNotificationManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CheckUnreadNotificationCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchAnnouncementsCallback;
import com.example.hoamanagementsystem.MainActivity;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.adapters.AnnouncementAdapter;

import java.util.ArrayList;
import java.util.List;

public class HomePage extends AppCompatActivity {
    private LinearLayout profilePage, announcementLink, documentsLink, grievanceLink, maintenanceLink;
    private RecyclerView announcementRV;
    private AnnouncementAdapter announcementAdapter;
    private ArrayList<AnnouncementModel> announcementList;
    private TextView fullName, userRole, userLocation;
    private ImageView notificationIcon;
    private View notificationDot;

    @Override
    protected void onResume() {
        super.onResume();
        checkNotificationStatus();
    }

    private String theRole, theUid, theFullName, theEmail, theBlock, theLot, theStreet, theLavanyaPhaseType, theImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Prevent screenshots and screen recordings


        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_home_page);
        profilePage= findViewById(R.id.profilePage);
        announcementLink = findViewById(R.id.announcementLink);
        documentsLink = findViewById(R.id.documentsLink);
        grievanceLink = findViewById(R.id.grievanceLink);
        maintenanceLink = findViewById(R.id.maintenanceLink);

        notificationDot = findViewById(R.id.notificationDot);

        notificationIcon = findViewById(R.id.notificationIcon);

        announcementRV  = findViewById(R.id.announcementRV);
        announcementList = new ArrayList<>();

        announcementAdapter = new AnnouncementAdapter(this, announcementList);
        announcementRV.setLayoutManager(new LinearLayoutManager(this));
        announcementRV.setAdapter(announcementAdapter);

        fullName = findViewById(R.id.fullName);
        userRole = findViewById(R.id.userRole);
        userLocation = findViewById(R.id.userLocation);

        Intent datas = getIntent();
        theRole = datas.getStringExtra("role");
        theUid = datas.getStringExtra("uid");
        theFullName = datas.getStringExtra("name");
        theEmail = datas.getStringExtra("email");
        theBlock = datas.getStringExtra("block");
        theLot = datas.getStringExtra("lot");
        theStreet = datas.getStringExtra("street");
        theLavanyaPhaseType = datas.getStringExtra("lavanyaPhaseType");
        theImage = datas.getStringExtra("image");

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
        checkNotificationStatus();

        notificationIcon.setOnClickListener(dd -> {
            navigateTo(NotificationPage.class);
        });

        setUpData();

        documentsLink.setOnClickListener(f -> {
            navigateToDocumentsPageWithData();
        });

        maintenanceLink.setOnClickListener(s -> {
            Intent intents = new Intent(HomePage.this, MaintenancePage.class);
            startActivity(intents);
        });

        grievanceLink.setOnClickListener(f -> {
            Intent asd = new Intent(HomePage.this, GrievancePage.class);
            startActivity(asd);
        });

        profilePage.setOnClickListener(s -> {
            Intent intent = new Intent(HomePage.this, ProfilePage.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
        });

        announcementLink.setOnClickListener(l -> {
            Intent intent = new Intent(HomePage.this, AnnouncementPage.class);
            startActivity(intent);
            overridePendingTransition(0, 0);
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
    private void setUpData() {
        fullName.setText(theFullName);
        userRole.setText(theRole);
        userLocation.setText(theBlock + " " + theLot + " " + theStreet);
    }
    private void navigateToDocumentsPageWithData() {
        Intent intent = new Intent(HomePage.this, DocumentsPage.class);
        intent.putExtra("role", theRole);
        intent.putExtra("uid", theUid);
        intent.putExtra("name", theFullName);
        intent.putExtra("email", theEmail);
        intent.putExtra("block", theBlock);
        intent.putExtra("lot", theLot);
        intent.putExtra("street", theStreet);
        intent.putExtra("lavanyaPhaseType", theLavanyaPhaseType);
        intent.putExtra("image", theImage);
        startActivity(intent);

    }
    private void checkNotificationStatus() {
        FirebaseNotificationManager.hasUnreadNotifications(
                theUid,
                new CheckUnreadNotificationCallback() {
                    @Override
                    public void onResult(boolean hasUnread) {

                        if (hasUnread) {
                            notificationDot.setVisibility(View.VISIBLE);
                        } else {
                            notificationDot.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onFailure(String error) {
                        notificationDot.setVisibility(View.GONE);
                    }
                });
    }

}