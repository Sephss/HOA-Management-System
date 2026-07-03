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

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseDocumentsManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetDocumentRequestsCallback;
import com.example.hoamanagementsystem.Model.DocumentRequestModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.adapters.DocumentRequestAdapter;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;

public class DocumentsPage extends AppCompatActivity {
    private RecyclerView documentRequestsRV;
    private Button newRequestBtn;

    private DocumentRequestAdapter adapter;

    private String theRole, theUid, theFullName, theEmail, theBlock, theLot, theStreet, theLavanyaPhaseType, theImage;

    private List<DocumentRequestModel> requestList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_documents_page);

        newRequestBtn = findViewById(R.id.newRequestBtn);

        documentRequestsRV =
                findViewById(R.id.documentRequestsRV);

        requestList = new ArrayList<>();

        adapter = new DocumentRequestAdapter(
                this,
                requestList
        );

        documentRequestsRV.setLayoutManager(
                new LinearLayoutManager(this)
        );

        documentRequestsRV.setAdapter(adapter);

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
        loadRequests();

        newRequestBtn.setOnClickListener(g -> {
            navigateToCreateDocumentRequestPage();
        });
    }
    private void loadRequests() {

        String currentUserId = FirebaseAuthManager.getCurrentUserUid();

        FirebaseDocumentsManager.getUserDocumentRequests(
                currentUserId,
                new GetDocumentRequestsCallback() {
                    @Override
                    public void onSuccess(
                            List<DocumentRequestModel> requests
                    ) {

                        requestList.clear();

                        requestList.addAll(requests);

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {

                    }
                }
        );
    }
    private void navigateToCreateDocumentRequestPage() {
        Intent intent = new Intent(
                DocumentsPage.this,
                RequestDocuments.class
        );
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
}