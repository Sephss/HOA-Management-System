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

import com.example.hoamanagementsystem.Model.HOARuleEntry;
import com.example.hoamanagementsystem.R;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HOARuleAdapter extends RecyclerView.Adapter<HOARuleAdapter.HOARuleViewHolder> {

    private final Context context;
    private final List<HOARuleEntry> ruleList;

    public HOARuleAdapter(Context context, List<HOARuleEntry> ruleList) {
        this.context = context;
        this.ruleList = ruleList;
    }

    @NonNull
    @Override
    public HOARuleViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_hoarule, parent, false);
        return new HOARuleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HOARuleViewHolder holder, int position) {
        HOARuleEntry entry = ruleList.get(position);

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

        // Image handling — thumbnail style, hidden if none
        String imageUrl = entry.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_arrow_back_24) // swap with a real placeholder
                    .error(R.drawable.baseline_arrow_back_24)       // swap with a real error drawable
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
        return ruleList.size();
    }

    public static class HOARuleViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView category, title, description, postedBy, dateTime, linkButton;

        public HOARuleViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.ruleImage);
            category = itemView.findViewById(R.id.ruleCategory);
            title = itemView.findViewById(R.id.ruleTitle);
            description = itemView.findViewById(R.id.ruleDescription);
            postedBy = itemView.findViewById(R.id.rulePostedBy);
            dateTime = itemView.findViewById(R.id.ruleDateTime);
            linkButton = itemView.findViewById(R.id.ruleLink);
        }
    }
}