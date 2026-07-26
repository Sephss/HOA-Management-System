package com.example.hoamanagementsystem.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.FirebaseServices.FirebaseAnnouncementManager;
import com.example.hoamanagementsystem.FirebaseServices.callback.AttendanceStatusCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.DeleteAnnouncementCallback;
import com.example.hoamanagementsystem.FirebaseServices.callback.ToggleAttendanceCallback;
import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.example.hoamanagementsystem.Model.HomeOwnerRentersModel;
import com.example.hoamanagementsystem.R;
import com.example.hoamanagementsystem.Session.UserSession;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private Context context;
    private List<AnnouncementModel> announcementList;
    private HomeOwnerRentersModel currentUser;

    public AnnouncementAdapter(Context context, List<AnnouncementModel> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
        this.currentUser = UserSession.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.announcement_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AnnouncementModel announcement = announcementList.get(position);

        if(currentUser.getRole().equals("Home Owners") || currentUser.getRole().equals("Renters")) {
            if (announcement.getCategory().equals("Meeting")) {
                holder.attendanceLayout.setVisibility(View.VISIBLE);
                bindAttendance(holder, announcement.getAnnouncementId());
            } else {
                holder.attendanceLayout.setVisibility(View.GONE);
            }
        }





        if(announcement.getLink().isEmpty() || announcement.getLink() == null || announcement.getLink().equals("")) {
            holder.link.setVisibility(View.GONE);
        } else {
            holder.link.setVisibility(View.VISIBLE);
        }

        String role = currentUser.getRole();

        if(role.equals("Home Owners") || role.equals("Renters")) {
            holder.deleteIcon.setVisibility(View.GONE);
        } else {
            holder.deleteIcon.setVisibility(View.VISIBLE);
        }

        holder.category.setText(announcement.getCategory());
        holder.title.setText(announcement.getTitle());
        holder.description.setText(announcement.getDescription());


        holder.deleteIcon.setOnClickListener(d -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Announcement")
                    .setMessage("Are you sure you want to delete this announcement?")
                    .setPositiveButton("Delete", (dialog, which) -> {

                        FirebaseAnnouncementManager.deleteAnnouncement(
                                announcement.getAnnouncementId(),
                                new DeleteAnnouncementCallback() {

                                    @Override
                                    public void onSuccess(String message) {

                                        Toast.makeText(context,
                                                message,
                                                Toast.LENGTH_SHORT).show();

                                    }

                                    @Override
                                    public void onFailure(String message) {
                                        Toast.makeText(context,
                                                message,
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });

                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        holder.dateAndTime.setText(
                announcement.getDate() + " - " + announcement.getTime()
        );

        String link = announcement.getLink();

        if (link == null || link.trim().isEmpty()) {
            holder.linkLayout.setVisibility(View.GONE);
        } else {

            holder.linkLayout.setVisibility(View.VISIBLE);
            holder.link.setText("View attachment");

            holder.linkLayout.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);
            });
        }
    }

    private void bindAttendance(ViewHolder holder, String announcementId) {

        FirebaseAnnouncementManager.getAttendanceStatus(announcementId, new AttendanceStatusCallback() {
            @Override
            public void onResult(boolean isAttending, long attendeeCount) {
                holder.attendeeCount.setText(attendeeCount + " attending");
                setButtonState(holder.btnConfirmAttendance, isAttending);
            }

            @Override
            public void onFailure(String message) {
                holder.attendeeCount.setText("-- attending");
            }
        });

        holder.btnConfirmAttendance.setOnClickListener(v -> {
            holder.btnConfirmAttendance.setEnabled(false);

            String homeownerName = (currentUser.getFirstName() + " " + currentUser.getLastName());
            String unitNumber = currentUser.getLavanyaPhaseType();

            FirebaseAnnouncementManager.toggleAttendance(announcementId, homeownerName, unitNumber,
                    new ToggleAttendanceCallback() {
                        @Override
                        public void onSuccess(boolean isNowAttending) {
                            holder.btnConfirmAttendance.setEnabled(true);
                            bindAttendance(holder, announcementId);
                        }

                        @Override
                        public void onFailure(String message) {
                            holder.btnConfirmAttendance.setEnabled(true);
                            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    private void setButtonState(Button button, boolean isAttending) {
        if (isAttending) {
            button.setText("Attending ✓");
            button.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.grey)));
        } else {
            button.setText("Confirm Attendance");
            button.setBackgroundTintList(ColorStateList.valueOf(
                    ContextCompat.getColor(context, R.color.green)));
        }
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView category, title, description, dateAndTime, link, attendeeCount;
        LinearLayout linkLayout;
        ImageView deleteIcon;
        LinearLayout attendanceLayout;
        Button btnConfirmAttendance;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            dateAndTime = itemView.findViewById(R.id.dateAndTime);
            link = itemView.findViewById(R.id.link);
            linkLayout = itemView.findViewById(R.id.linkLayout);
            deleteIcon = itemView.findViewById(R.id.deleteIcon);
            attendanceLayout = itemView.findViewById(R.id.attendanceLayout);
            btnConfirmAttendance = itemView.findViewById(R.id.btnConfirmAttendance);
            attendeeCount = itemView.findViewById(R.id.attendeeCount);
        }
    }
}