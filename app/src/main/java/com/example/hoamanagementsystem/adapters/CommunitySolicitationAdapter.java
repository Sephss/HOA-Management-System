package com.example.hoamanagementsystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.CommunitySolicitationEntry;
import com.example.hoamanagementsystem.Modules.SubmitContributionActivity;
import com.example.hoamanagementsystem.R;
import com.squareup.picasso.Picasso;

import java.util.List;
import java.util.Map;

public class CommunitySolicitationAdapter extends RecyclerView.Adapter<CommunitySolicitationAdapter.SolicitViewHolder> {

    private final Context context;
    private final List<CommunitySolicitationEntry> solicitList;
    private final Map<String, Boolean> submittedMap; // solicitId -> user already contributed

    public CommunitySolicitationAdapter(Context context, List<CommunitySolicitationEntry> solicitList,
                                        Map<String, Boolean> submittedMap) {
        this.context = context;
        this.solicitList = solicitList;
        this.submittedMap = submittedMap;
    }

    @NonNull
    @Override
    public SolicitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_community_solicit, parent, false);
        return new SolicitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SolicitViewHolder holder, int position) {
        CommunitySolicitationEntry entry = solicitList.get(position);

        holder.title.setText(entry.getTitle());
        holder.description.setText(entry.getDescription());

        // Image — hide if none
        String imageUrl = entry.getImageUrl();
        if (!TextUtils.isEmpty(imageUrl)) {
            holder.image.setVisibility(View.VISIBLE);
            Picasso.get()
                    .load(imageUrl)
                    .placeholder(R.drawable.baseline_arrow_back_24)
                    .error(R.drawable.baseline_arrow_back_24)
                    .into(holder.image);
        } else {
            holder.image.setVisibility(View.GONE);
        }

        // Status badge
        boolean isActive = !"closed".equalsIgnoreCase(entry.getStatus());
        holder.statusBadge.setText(isActive ? "Active" : "Closed");
        holder.statusBadge.setBackgroundResource(isActive ? R.drawable.bg_chip_status_resolved : R.drawable.bg_chip_status_rejected);

        // Bank info line — hide if none provided
        String bank = entry.getBankName();
        String accNum = entry.getAccountNumber();
        if (!TextUtils.isEmpty(bank) || !TextUtils.isEmpty(accNum)) {
            String bankLine = (TextUtils.isEmpty(bank) ? "" : bank) +
                    (!TextUtils.isEmpty(bank) && !TextUtils.isEmpty(accNum) ? " • " : "") +
                    (TextUtils.isEmpty(accNum) ? "" : "Acct: " + accNum);
            holder.bankInfo.setText(bankLine);
            holder.bankInfo.setVisibility(View.VISIBLE);
        } else {
            holder.bankInfo.setVisibility(View.GONE);
        }

        // Posted by / date footer
        String postedBy = entry.getPostedByName();
        String dateTime = TextUtils.isEmpty(entry.getDateCreated()) ? "" : entry.getDateCreated();
        String footer = (TextUtils.isEmpty(postedBy) ? "" : postedBy) +
                (!TextUtils.isEmpty(postedBy) && !dateTime.isEmpty() ? " • " : "") + dateTime;
        holder.dateTime.setText(footer);

        // Action button
        boolean alreadySubmitted = submittedMap != null &&
                Boolean.TRUE.equals(submittedMap.get(entry.getSolicitId()));

        if (!isActive) {
            holder.actionBtn.setText("Closed");
            holder.actionBtn.setEnabled(false);
            holder.actionBtn.setAlpha(0.5f);
        } else if (alreadySubmitted) {
            holder.actionBtn.setText("Already Submitted");
            holder.actionBtn.setEnabled(false);
            holder.actionBtn.setAlpha(0.5f);
        } else {
            holder.actionBtn.setText("Send Contribution");
            holder.actionBtn.setEnabled(true);
            holder.actionBtn.setAlpha(1f);
            holder.actionBtn.setOnClickListener(v -> {
                Intent intent = new Intent(context, SubmitContributionActivity.class);
                intent.putExtra("solicitId", entry.getSolicitId());
                intent.putExtra("solicitTitle", entry.getTitle());
                intent.putExtra("bankName", entry.getBankName());
                intent.putExtra("accountName", entry.getAccountName());
                intent.putExtra("accountNumber", entry.getAccountNumber());
                context.startActivity(intent);
            });
        }
    }

    @Override
    public int getItemCount() {
        return solicitList.size();
    }

    public static class SolicitViewHolder extends RecyclerView.ViewHolder {
        ImageView image;
        TextView statusBadge, title, description, bankInfo, dateTime, actionBtn;

        public SolicitViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.solicitImage);
            statusBadge = itemView.findViewById(R.id.solicitStatusBadge);
            title = itemView.findViewById(R.id.solicitTitle);
            description = itemView.findViewById(R.id.solicitDescription);
            bankInfo = itemView.findViewById(R.id.solicitBankInfo);
            dateTime = itemView.findViewById(R.id.solicitDateTime);
            actionBtn = itemView.findViewById(R.id.solicitActionBtn);
        }
    }
}