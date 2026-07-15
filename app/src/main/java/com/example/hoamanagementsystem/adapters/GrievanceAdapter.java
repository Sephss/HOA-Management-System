package com.example.hoamanagementsystem.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.GrievanceModel;
import com.example.hoamanagementsystem.Modules.GrievanceClicked;
import com.example.hoamanagementsystem.Modules.RequestDocumensClicked;
import com.example.hoamanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class GrievanceAdapter
        extends RecyclerView.Adapter<GrievanceAdapter.ViewHolder> implements Filterable {

    private final List<GrievanceModel> grievanceList;
    private final List<GrievanceModel> grievanceListFull;


    public GrievanceAdapter(List<GrievanceModel> grievanceList) {
        this.grievanceList = grievanceList;
        this.grievanceListFull = new ArrayList<>(grievanceList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grievance,
                        parent,
                        false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position) {

        GrievanceModel grievance = grievanceList.get(position);

        holder.title.setText(grievance.getIncidentTitle());
        holder.type.setText(grievance.getIncidentType());

        holder.date.setText(grievance.getDateSubmitted());

        holder.viewDetailsTV.setOnClickListener(s -> {
            Intent intent = new Intent(holder.itemView.getContext(), GrievanceClicked.class);
            intent.putExtra("title", grievance.getIncidentTitle());
            intent.putExtra("type", grievance.getIncidentType());
            intent.putExtra("status", grievance.getIncidentStatus());
            intent.putExtra("date", grievance.getDateSubmitted() + ", " + grievance.getTimeSubmitted());
            intent.putExtra("dateUnderInvestigation", grievance.getUnderInvestigationDate());
            intent.putExtra("dateResolved", grievance.getResolvedDate());
            intent.putExtra("description", grievance.getIncidentDescription());
            intent.putExtra("ticket", grievance.getIncidentTicket());
            intent.putExtra("location", grievance.getIncidentExactLocation());
            intent.putExtra("incidentReportID", grievance.getIncidentReportID());

            intent.putExtra("incidentImage", grievance.getIncidentImageUrl());

            intent.putExtra("reporterID", grievance.getIncidentReporterID());

            intent.putExtra("adminRemarks", grievance.getAdminRemarks());

            holder.itemView.getContext().startActivity(intent);
        });

        switch (grievance.getIncidentStatus()) {
            case "pending":
                holder.status.setBackgroundResource(
                        R.drawable.pending_document_request
                );

                holder.status.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.darkyellow)
                );

                holder.status.setText("Pending");
                break;

            case "under_investigation":
                holder.status.setBackgroundResource(
                        R.drawable.under_review_color
                );

                holder.status.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.darkyellow)
                );

                holder.status.setText("Under Investigation");
                break;

            case "resolved":
                holder.status.setBackgroundResource(
                        R.drawable.approved_and_ready_color
                );

                holder.status.setTextColor(
                        ContextCompat.getColor(holder.itemView.getContext(), R.color.green)
                );

                holder.status.setText("Resolved");
                break;
        }
    }

    @Override
    public int getItemCount() {
        return grievanceList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView title, type, status, date, viewDetailsTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            title = itemView.findViewById(R.id.tvTitle);
            type = itemView.findViewById(R.id.tvType);
            status = itemView.findViewById(R.id.tvStatus);
            date = itemView.findViewById(R.id.tvDate);
            viewDetailsTV = itemView.findViewById(R.id.viewDetailsTV);
        }
    }
    public void updateList(List<GrievanceModel> newList) {

        grievanceList.clear();
        grievanceList.addAll(newList);

        grievanceListFull.clear();
        grievanceListFull.addAll(newList);

        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return grievanceFilter;
    }

    private final Filter grievanceFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<GrievanceModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {

                filteredList.addAll(grievanceListFull);

            } else {

                String filterPattern =
                        constraint.toString().toLowerCase().trim();

                for (GrievanceModel grievance : grievanceListFull) {

                    if ((grievance.getIncidentTitle() != null &&
                            grievance.getIncidentTitle().toLowerCase().contains(filterPattern))

                            || (grievance.getIncidentType() != null &&
                            grievance.getIncidentType().toLowerCase().contains(filterPattern))

                            || (grievance.getIncidentStatus() != null &&
                            grievance.getIncidentStatus().toLowerCase().contains(filterPattern))

                            || (grievance.getIncidentTicket() != null &&
                            grievance.getIncidentTicket().toLowerCase().contains(filterPattern))

                            || (grievance.getIncidentDescription() != null &&
                            grievance.getIncidentDescription().toLowerCase().contains(filterPattern))

                            || (grievance.getIncidentExactLocation() != null &&
                            grievance.getIncidentExactLocation().toLowerCase().contains(filterPattern))) {

                        filteredList.add(grievance);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filteredList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint,
                                      FilterResults results) {

            grievanceList.clear();
            grievanceList.addAll((List<GrievanceModel>) results.values);
            notifyDataSetChanged();
        }
    };
}