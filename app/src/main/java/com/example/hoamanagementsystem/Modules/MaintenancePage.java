package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseMaintenanceManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchMaintenanceCallback;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.Model.MaintenanceModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.adapters.MaintenanceAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class MaintenancePage extends AppCompatActivity {
    private Button newRequestBtn;
    private RecyclerView maintenanceRequestRV;
    private ImageView backBtn;
    private EditText searchET;

    private List<MaintenanceModel> maintenanceList;
    private MaintenanceAdapter adapter;

    private HomeOwnerRentersModel currentUser;
    private String userRole;

    private String theUserRole;
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maintenance_page);

        newRequestBtn = findViewById(R.id.newRequestBtn);
        searchET = findViewById(R.id.searchET);
        maintenanceRequestRV = findViewById(R.id.maintenanceRequestRV);
        backBtn = findViewById(R.id.backBtn);
        maintenanceList = new ArrayList<>();

        adapter = new MaintenanceAdapter(this, maintenanceList);

        maintenanceRequestRV.setLayoutManager(new LinearLayoutManager(this));
        maintenanceRequestRV.setAdapter(adapter);

        currentUser = UserSession.getInstance().getCurrentUser();

        userRole = currentUser.getRole();
        if(userRole.equals("Home Owners") || userRole.equals("Renters")) {
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
        displayUIBasedOnRole();
        setUpBtn();
        newRequestBtn.setOnClickListener(s -> {
            navigateTo(SubmitMaintenanceRequest.class);
        });

        backBtn.setOnClickListener(s -> {
            finish();
        });

        searchET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void displayUIBasedOnRole() {
        if(currentUser.getRole().equals("Home Owners") || currentUser.getRole().equals("Renters")) {
            fetchMaintenanceRequests();
        } else {
            fetchAdminMaintenanceRequest();
        }
    }
    private void navigateTo(Class<?> destination) {
        Intent intent = new Intent(this, destination);
        startActivity(intent);
    }
    private void fetchMaintenanceRequests() {

        String uid = FirebaseAuth.getInstance()
                .getCurrentUser()
                .getUid();

        FirebaseMaintenanceManager.getMyMaintenanceRequests(
                uid,
                new FetchMaintenanceCallback() {

                    @Override
                    public void onSuccess(List<MaintenanceModel> requests) {
                        adapter.updateList(requests);
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                });
    }
    private void fetchAdminMaintenanceRequest() {
        FirebaseMaintenanceManager.getAllMaintenanceRequests(
                new FetchMaintenanceCallback() {
                    @Override
                    public void onSuccess(List<MaintenanceModel> requests) {

                        adapter.updateList(requests);
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                }
        );
    }
    private void setUpBtn() {
        String role = currentUser.getRole();

        if(role.equals("Home Owners") || role.equals("Renters")) {
            newRequestBtn.setVisibility(View.VISIBLE);
        } else {
            newRequestBtn.setVisibility(View.GONE);
        }
    }
}