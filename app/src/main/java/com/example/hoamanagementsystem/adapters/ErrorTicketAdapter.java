package com.example.hoamanagementsystem.Modules;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.ErrorTicketEntry;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class ErrorTicketAdapter extends RecyclerView.Adapter<ErrorTicketAdapter.TicketViewHolder> {

    private final Context context;
    private final List<ErrorTicketEntry> ticketList;

    public ErrorTicketAdapter(Context context, List<ErrorTicketEntry> ticketList) {
        this.context = context;
        this.ticketList = ticketList;
    }

    @NonNull
    @Override
    public TicketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_error_ticket, parent, false);
        return new TicketViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TicketViewHolder holder, int position) {
        ErrorTicketEntry entry = ticketList.get(position);

        holder.title.setText(entry.getTitle());
        holder.description.setText(entry.getDescription());

        // Category chip (color-coded)
        String category = entry.getCategory();
        holder.category.setText(category);
        if (!TextUtils.isEmpty(category) && category.toLowerCase().contains("bug")) {
            holder.category.setBackgroundResource(R.drawable.bg_chip_bug);
        } else {
            holder.category.setBackgroundResource(R.drawable.bg_chip_suggestion);
        }

        // Status chip
        String status = entry.getStatus();
        if (TextUtils.isEmpty(status)) status = "Pending";
        holder.status.setText(status);
        switch (status) {
            case "In Progress":
                holder.status.setBackgroundResource(R.drawable.bg_chip_status_progress);
                break;
            case "Resolved":
                holder.status.setBackgroundResource(R.drawable.bg_chip_status_resolved);
                break;
            case "Rejected":
                holder.status.setBackgroundResource(R.drawable.bg_chip_status_rejected);
                break;
            default:
                holder.status.setBackgroundResource(R.drawable.bg_chip_status_pending);
                break;
        }

        // Date
        String dateTime = "";
        if (!TextUtils.isEmpty(entry.getDateCreated())) dateTime = entry.getDateCreated();
        if (!TextUtils.isEmpty(entry.getTimeCreated())) {
            dateTime += (dateTime.isEmpty() ? "" : " • ") + entry.getTimeCreated();
        }
        holder.dateTime.setText(dateTime);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, ErrorTicketDetailsModule.class);
            intent.putExtra("ticketId", entry.getTicketId());
            intent.putExtra("userId", entry.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return ticketList.size();
    }

    public static class TicketViewHolder extends RecyclerView.ViewHolder {
        TextView category, status, title, description, dateTime;

        public TicketViewHolder(@NonNull View itemView) {
            super(itemView);
            category = itemView.findViewById(R.id.ticketCategory);
            status = itemView.findViewById(R.id.ticketStatus);
            title = itemView.findViewById(R.id.ticketTitle);
            description = itemView.findViewById(R.id.ticketDescription);
            dateTime = itemView.findViewById(R.id.ticketDateTime);
        }
    }
}