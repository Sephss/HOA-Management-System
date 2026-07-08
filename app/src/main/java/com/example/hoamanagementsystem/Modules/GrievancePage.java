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

import com.example.hoamanagementsystem.FirebaseServices.FirebaseGrievanceManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.FetchGrievanceCallback;
import com.example.hoamanagementsystem.Model.GrievanceModel;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.adapters.GrievanceAdapter;

import java.util.List;

public class GrievancePage extends AppCompatActivity {
    private Button newReportBtn;
    private String theRole, theUid, theFullName, theEmail, theBlock, theLot, theStreet, theLavanyaPhaseType, theImage;
    private RecyclerView grievanceRV;
    private HomeOwnerRentersModel currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_grievance_page);

        newReportBtn = findViewById(R.id.newReportBtn);

        grievanceRV = findViewById(R.id.grievanceRV);
        grievanceRV.setLayoutManager(
                new LinearLayoutManager(this));

        currentUser = UserSession.getInstance().getCurrentUser();

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


        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        loadUIBasedOnRole();

        newReportBtn.setOnClickListener(g -> {
            navigateToSubmitReportPage();
        });
    }
    private void navigateToSubmitReportPage() {
        Intent intent = new Intent(GrievancePage.this, SubmitGrievanceReport.class);
        startActivity(intent);

    }
    private void loadUIBasedOnRole() {


        if(currentUser.getRole().equals("Home Owners") || currentUser.getRole().equals("Renters")) {
            loadMyReports();
        } else {
            loadAdminReports();
        }
    }
    private void loadMyReports() {
        String myID = currentUser.getUid();
        FirebaseGrievanceManager.getMyReports(
                myID,
                new FetchGrievanceCallback() {
                    @Override
                    public void onSuccess(
                            List<GrievanceModel> grievances) {

                        GrievanceAdapter adapter =
                                new GrievanceAdapter(grievances);

                        grievanceRV.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });
    }
    private void loadAdminReports() {
        FirebaseGrievanceManager.getAllReports(
                new FetchGrievanceCallback() {
                    @Override
                    public void onSuccess(
                            List<GrievanceModel> grievances) {

                        GrievanceAdapter adapter =
                                new GrievanceAdapter(grievances);

                        grievanceRV.setAdapter(adapter);
                    }

                    @Override
                    public void onFailure(String message) {

                    }
                });
    }
}