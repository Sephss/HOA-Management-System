package com.example.hoamanagementsystem.Modules;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.hoamanagementsystem.R;

public class AccountArchived extends AppCompatActivity {
    private TextView archivedAt, archivedReason;
    private Button iUnderstandBtn;
    String uid, reason, dateArchived;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_account_archived);

        archivedAt = findViewById(R.id.archivedAt);
        archivedReason = findViewById(R.id.archiveReason);
        iUnderstandBtn = findViewById(R.id.iUnderstandBtn);

        Intent intent = getIntent();
        uid = intent.getStringExtra("uid");
        dateArchived = intent.getStringExtra("archivedAt");
        reason = intent.getStringExtra("reason");

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        iUnderstandBtn.setOnClickListener(d -> {
            finishAffinity(); // Closes all activities in the app
            System.exit(0);   //  force the process to exit
        });
        setUpDetails();

    }
    private void setUpDetails() {
        archivedAt.setText(dateArchived);
        archivedReason.setText(reason);
    }

}