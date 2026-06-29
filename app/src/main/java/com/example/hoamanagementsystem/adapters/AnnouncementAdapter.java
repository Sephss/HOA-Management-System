package com.example.hoamanagementsystem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.AnnouncementModel;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private Context context;
    private List<AnnouncementModel> announcementList;

    public AnnouncementAdapter(Context context, List<AnnouncementModel> announcementList) {
        this.context = context;
        this.announcementList = announcementList;
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

        holder.category.setText(announcement.getCategory());
        holder.title.setText(announcement.getTitle());
        holder.description.setText(announcement.getDescription());

        holder.dateAndTime.setText(
                announcement.getDate() + " - " + announcement.getTime()
        );

        String link = announcement.getLink();

        if (link == null || link.trim().isEmpty()) {
            holder.linkLayout.setVisibility(View.GONE);
        } else {

            holder.linkLayout.setVisibility(View.VISIBLE);
            holder.link.setText(link);

            holder.linkLayout.setOnClickListener(v -> {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return announcementList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView category, title, description, dateAndTime, link;
        LinearLayout linkLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            category = itemView.findViewById(R.id.category);
            title = itemView.findViewById(R.id.title);
            description = itemView.findViewById(R.id.description);
            dateAndTime = itemView.findViewById(R.id.dateAndTime);
            link = itemView.findViewById(R.id.link);
            linkLayout = itemView.findViewById(R.id.linkLayout);
        }
    }
}