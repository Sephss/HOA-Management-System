package com.example.hoamanagementsystem.Modules;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAuthManager;
import com.example.hoamanagementsystem.FirebaseServices.FirebaseNotificationManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.CreateNotificationCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.GetNotificationCallback;
import com.example.hoamanagementsystem.Model.NotificationModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;
import com.example.hoamanagementsystem.adapters.NotificationAdapter;

import java.util.ArrayList;
import java.util.List;

public class NotificationPage extends AppCompatActivity {
    private RecyclerView notificationsRV;
    private NotificationAdapter adapter;
    private List<NotificationModel> notificationList;
    private String userID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_notification_page);
        userID = FirebaseAuthManager.getCurrentUserUid();

        notificationList = new ArrayList<>();
        notificationsRV = findViewById(R.id.notificationsRV);

        adapter = new NotificationAdapter(
                this,
                notificationList
        );

        notificationsRV.setLayoutManager(
                new LinearLayoutManager(this)
        );

        notificationsRV.setAdapter(adapter);

        loadNotifications();
        readAllNotification();

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
    }
    private void loadNotifications() {

        FirebaseNotificationManager.getUserNotifications(
                userID,
                new GetNotificationCallback() {

                    @Override
                    public void onSuccess(List<NotificationModel> notifications) {

                        notificationList.clear();
                        notificationList.addAll(notifications);

                        adapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String error) {

                        Toast.makeText(
                                NotificationPage.this,
                                error,
                                Toast.LENGTH_SHORT
                        ).show();
                    }
                }
        );
    }
    private void readAllNotification() {
        FirebaseNotificationManager.markAllNotificationsAsRead(userID, new CreateNotificationCallback() {
            @Override
            public void onSuccess(String success) {

            }

            @Override
            public void onFailure(String error) {

            }
        });
    }
}