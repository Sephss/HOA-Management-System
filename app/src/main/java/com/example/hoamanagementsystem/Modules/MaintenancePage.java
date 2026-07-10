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

    private List<MaintenanceModel> maintenanceList;
    private MaintenanceAdapter adapter;

    private HomeOwnerRentersModel currentUser;

    private String theUserRole;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_maintenance_page);

        newRequestBtn = findViewById(R.id.newRequestBtn);
        maintenanceRequestRV = findViewById(R.id.maintenanceRequestRV);

        maintenanceList = new ArrayList<>();

        adapter = new MaintenanceAdapter(this, maintenanceList);

        maintenanceRequestRV.setLayoutManager(new LinearLayoutManager(this));
        maintenanceRequestRV.setAdapter(adapter);

        currentUser = UserSession.getInstance().getCurrentUser();


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        displayUIBasedOnRole();
        newRequestBtn.setOnClickListener(s -> {
            navigateTo(SubmitMaintenanceRequest.class);
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

                        maintenanceList.clear();
                        maintenanceList.addAll(requests);

                        adapter.notifyDataSetChanged();
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

                        maintenanceList.clear();
                        maintenanceList.addAll(requests);
                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                }
        );
    }
}