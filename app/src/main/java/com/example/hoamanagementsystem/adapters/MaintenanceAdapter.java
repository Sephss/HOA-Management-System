package com.example.hoamanagementsystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.MaintenanceModel;
import com.example.hoamanagementsystem.Modules.MaintenanceRequestClicked;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class MaintenanceAdapter extends RecyclerView.Adapter<MaintenanceAdapter.ViewHolder> {

    private final Context context;
    private final List<MaintenanceModel> list;

    public MaintenanceAdapter(Context context, List<MaintenanceModel> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(context)
                .inflate(R.layout.maintenance_item_layout, parent, false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        MaintenanceModel model = list.get(position);

        holder.tvMaintenanceType.setText(model.getMaintenanceType());
        holder.tvDescription.setText(model.getFullDescription());
        holder.tvLocation.setText(model.getExactLocation());
        holder.tvDate.setText(model.getDateSubmitted());

        holder.viewDetailsTV.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MaintenanceRequestClicked.class);
            intent.putExtra("maintenanceType", model.getMaintenanceType());
            intent.putExtra("maintenanceLocation", model.getExactLocation());
            intent.putExtra("maintenanceTicket", model.getMaintenanceTicket());
            intent.putExtra("maintenanceStatus", model.getMaintenanceStatus());
            intent.putExtra("maintenanceTitle", model.getMaintenanceTitle());
            intent.putExtra("adminRemarksText", model.getAdminRemarks());
            intent.putExtra("maintenanceDescription", model.getFullDescription());
            intent.putExtra("maintenanceDate", model.getDateSubmitted());
            intent.putExtra("maintenancePriority", model.getUrgencyLevel());
            intent.putExtra("dateTime", model.getDateSubmitted() + ", " + model.getTimeSubmitted());
            intent.putExtra("maintenancePriority", model.getUrgencyLevel());
            intent.putExtra("dateTimeRepairInProgress", model.getUnderInvestigationDate());
            intent.putExtra("maintenanceID", model.getMaintenanceID());
            intent.putExtra("dateCompleted", model.getResolvedDate());
            holder.itemView.getContext().startActivity(intent);
        });

        switch (model.getMaintenanceStatus()) {
            case "pending":
                holder.tvStatus.setBackgroundResource(
                        R.drawable.pending_document_request
                );

                holder.tvStatus.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.darkyellow)
                );

                holder.tvStatus.setText("Pending");
                break;

            case "repair_in_progress":
                holder.tvStatus.setBackgroundResource(
                        R.drawable.under_review_color
                );

                holder.tvStatus.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.darkyellow)
                );

                holder.tvStatus.setText("In Progress");
                break;

            case "completed":
                holder.tvStatus.setBackgroundResource(
                        R.drawable.approved_and_ready_color
                );

                holder.tvStatus.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.green)
                );

                holder.tvStatus.setText("Completed");
                break;
        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvMaintenanceType,
                tvStatus,
                tvDescription,
                tvLocation,
                tvDate,
                viewDetailsTV;

        ViewHolder(@NonNull View itemView) {
            super(itemView);

            tvMaintenanceType = itemView.findViewById(R.id.tvMaintenanceType);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvLocation = itemView.findViewById(R.id.tvLocation);
            tvDate = itemView.findViewById(R.id.tvDate);
            viewDetailsTV = itemView.findViewById(R.id.viewDetailsTV);
        }
    }
}