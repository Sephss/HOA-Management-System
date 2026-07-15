package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.MainActivity;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.squareup.picasso.Picasso;

public class ProfilePage extends AppCompatActivity {
    private TextView logoutBtn, editProfileLink, userName;
    private ImageView theProfile;
    private HomeOwnerRentersModel currentUser;
    private LinearLayout announcementLink, homePage;
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile_page);

        currentUser = UserSession.getInstance().getCurrentUser();
        logoutBtn = findViewById(R.id.logoutBtn);
        editProfileLink = findViewById(R.id.editProfileLink);

        theProfile = findViewById(R.id.theProfile);
        userName = findViewById(R.id.userName);

        homePage = findViewById(R.id.homePage);
        announcementLink = findViewById(R.id.announcementLink);


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        homePage.setOnClickListener(d -> {

            finish();
            overridePendingTransition(0, 0);
        });

        announcementLink.setOnClickListener(s -> {
            Intent intent = new Intent(ProfilePage.this, AnnouncementPage.class);
            startActivity(intent);
            finish();
            overridePendingTransition(0, 0);
        });
        logoutBtn.setOnClickListener(s -> {
            FirebaseAuthManager.logout();
                Intent intent = new Intent(ProfilePage.this, MainActivity.class);
              intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
              startActivity(intent);
        });

        editProfileLink.setOnClickListener(h -> {
            Intent intent = new Intent(ProfilePage.this, EditProfilePage.class);
            startActivity(intent);
        });

        setupData();
    }
    private void setupData() {
        String image = currentUser.getImageUrl();
        String fullname = currentUser.getFirstName() + " " + currentUser.getLastName();

        if(image.equals("none")) {
            theProfile.setImageResource(R.drawable.profile_placeholder);
        } else {
            Picasso.get().load(image).into(theProfile);
        }

        userName.setText(fullname);

    }
}