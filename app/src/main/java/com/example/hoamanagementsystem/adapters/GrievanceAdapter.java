package com.example.hoamanagementsystem.adapters;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.GrievanceModel;
import com.example.hoamanagementsystem.Modules.GrievanceClicked;
import com.example.hoamanagementsystem.Modules.RequestDocumensClicked;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class GrievanceAdapter
        extends RecyclerView.Adapter<GrievanceAdapter.ViewHolder> {

    private final List<GrievanceModel> grievanceList;


    public GrievanceAdapter(List<GrievanceModel> grievanceList) {
        this.grievanceList = grievanceList;
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
        holder.status.setText(grievance.getIncidentStatus());
        holder.date.setText(grievance.getDateSubmitted());

        holder.viewDetailsTV.setOnClickListener(s -> {
            Intent intent = new Intent(holder.itemView.getContext(), GrievanceClicked.class);
            intent.putExtra("title", grievance.getIncidentTitle());
            intent.putExtra("type", grievance.getIncidentType());
            intent.putExtra("status", grievance.getIncidentStatus());
            intent.putExtra("date", grievance.getDateSubmitted());
            intent.putExtra("description", grievance.getIncidentDescription());
            intent.putExtra("ticket", grievance.getIncidentTicket());
            intent.putExtra("location", grievance.getIncidentExactLocation());
            intent.putExtra("incidentReportID", grievance.getIncidentReportID());

            holder.itemView.getContext().startActivity(intent);
        });
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
}