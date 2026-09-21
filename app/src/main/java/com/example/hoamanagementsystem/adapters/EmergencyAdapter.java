package com.example.hoamanagementsystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.EmergencyEntry;
import com.example.hoamanagementsystem.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class EmergencyAdapter extends RecyclerView.Adapter<EmergencyAdapter.EmergencyViewHolder> {

    private final Context context;
    private final List<EmergencyEntry> emergencyList;

    public EmergencyAdapter(Context context, List<EmergencyEntry> emergencyList) {
        this.context = context;
        this.emergencyList = emergencyList;
    }

    @NonNull
    @Override
    public EmergencyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_emergency, parent, false);
        return new EmergencyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmergencyViewHolder holder, int position) {
        EmergencyEntry entry = emergencyList.get(position);

        holder.title.setText(entry.getTitle());
        holder.description.setText(entry.getDescription());
        holder.category.setText(entry.getCategory());

        // Posted by info
        String postedBy = entry.getPostedByName();
        String role = entry.getPostedByRole();
        if (!TextUtils.isEmpty(postedBy)) {
            String postedText = postedBy;
            if (!TextUtils.isEmpty(role)) {
                postedText += " • " + role;
            }
            holder.postedBy.setText(postedText);
            holder.postedBy.setVisibility(View.VISIBLE);
        } else {
            holder.postedBy.setVisibility(View.GONE);
        }

        // Date and time
        String dateTime = "";
        if (!TextUtils.isEmpty(entry.getDateCreated())) {
            dateTime = entry.getDateCreated();
        }
        if (!TextUtils.isEmpty(entry.getTimeCreated())) {
            dateTime += (dateTime.isEmpty() ? "" : " • ") + entry.getTimeCreated();
        }
        holder.dateTime.setText(dateTime);

        // Image handling — hide if no image
        String imageUrl = entry.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_arrow_back_24) // replace with a real placeholder drawable
                    .error(R.drawable.baseline_arrow_back_24)       // replace with a real error drawable
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        // Link handling — hide if no link
        String link = entry.getLink();
        if (!TextUtils.isEmpty(link)) {
            holder.linkButton.setVisibility(View.VISIBLE);
            holder.linkButton.setOnClickListener(v -> {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                    context.startActivity(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else {
            holder.linkButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return emergencyList.size();
    }

    public static class EmergencyViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView category, title, description, postedBy, dateTime, linkButton;

        public EmergencyViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.entryImage);
            category = itemView.findViewById(R.id.entryCategory);
            title = itemView.findViewById(R.id.entryTitle);
            description = itemView.findViewById(R.id.entryDescription);
            postedBy = itemView.findViewById(R.id.entryPostedBy);
            dateTime = itemView.findViewById(R.id.entryDateTime);
            linkButton = itemView.findViewById(R.id.entryLink);
        }
    }
}