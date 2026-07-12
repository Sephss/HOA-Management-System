package com.example.hoamanagementsystem.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.NotificationModel;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder> {

    private final Context context;
    private final List<NotificationModel> notificationList;

    public NotificationAdapter(
            Context context,
            List<NotificationModel> notificationList
    ) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.notification_item, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        NotificationModel notification = notificationList.get(position);

        holder.notifTitleTV.setText(notification.getNotificationType());
        holder.notifMessageTV.setText(notification.getNotifMessage());
        holder.notifDateTV.setText(notification.getDate());
        holder.notifTimeTV.setText(notification.getTime());

        if ("no".equalsIgnoreCase(notification.getIsRead())) {
            holder.unreadDot.setVisibility(View.VISIBLE);
        } else {
            holder.unreadDot.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView notifTitleTV;
        TextView notifMessageTV;
        TextView notifDateTV;
        TextView notifTimeTV;
        View unreadDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            notifTitleTV = itemView.findViewById(R.id.notifTitleTV);
            notifMessageTV = itemView.findViewById(R.id.notifMessageTV);
            notifDateTV = itemView.findViewById(R.id.notifDateTV);
            notifTimeTV = itemView.findViewById(R.id.notifTimeTV);

            unreadDot = itemView.findViewById(R.id.unreadDot);
        }
    }
}