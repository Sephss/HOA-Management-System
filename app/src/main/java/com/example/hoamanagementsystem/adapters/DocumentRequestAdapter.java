package com.example.hoamanagementsystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.DocumentRequestModel;
import com.example.hoamanagementsystem.Modules.RequestDocumensClicked;
import com.example.hoamanagementsystem.R;

import java.util.ArrayList;
import java.util.List;

public class DocumentRequestAdapter
        extends RecyclerView.Adapter<DocumentRequestAdapter.ViewHolder> implements Filterable {

    private final Context context;
    private final List<DocumentRequestModel> requestList;
    private final List<DocumentRequestModel> requestListFull;

    public DocumentRequestAdapter(
            Context context,
            List<DocumentRequestModel> requestList
    ) {
        this.context = context;
        this.requestList = requestList;
        this.requestListFull = new ArrayList<>(requestList);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(context)
                .inflate(
                        R.layout.document_request_itemlayout,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        DocumentRequestModel request =
                requestList.get(position);

        holder.requestCategory.setText(
                request.getRequestCategory()
        );

        holder.requestType.setText(
                request.getDocumentType()
        );

        holder.requestPurpose.setText(
                request.getPurpose()
        );

        holder.requestDate.setText(
                request.getRequestDate() + " • " +
                        request.getRequestTime()
        );

        holder.requestStatus.setText(
                request.getRequestStatus()
        );

        setStatusDesign(
                holder.requestStatus,
                request.getRequestStatus()
        );

        holder.viewDetailsTV.setOnClickListener(f -> {
            Intent intent = new Intent(context, RequestDocumensClicked.class);
            intent.putExtra("role", request.getRequesterResidentType());
            intent.putExtra("uid", request.getRequesterID());
            intent.putExtra("requestID", request.getRequestID());
            intent.putExtra("name", request.getRequesterName());
            intent.putExtra("email", request.getRequesterEmail());
            intent.putExtra("block", request.getRequesterBlock());
            intent.putExtra("lot", request.getRequesterLot());
            intent.putExtra("street", request.getRequesterStreet());
            intent.putExtra("lavanyaPhaseType", request.getRequesterLavanyaPhaseType());
            intent.putExtra("image", request.getRequesterImage());

            intent.putExtra("documentType", request.getDocumentType());
            intent.putExtra("documentPurpose", request.getPurpose());
            intent.putExtra("documentStatus", request.getRequestStatus());
            intent.putExtra("documentDateSubmitted", request.getRequestDate());
            intent.putExtra("documentCategory", request.getRequestCategory());

            intent.putExtra("documentUnderReviewDate", request.getUnderReviewDateTime());
            intent.putExtra("documentApprovedDate", request.getApprovedDateTime());
            intent.putExtra("documentRejectedDate", request.getRejectedDateTime());
            intent.putExtra("documentPendingDate", request.getRequestDate() + ", " + request.getRequestTime());

            intent.putExtra("adminLink", request.getAdminLinkResponse());
            intent.putExtra("adminRemarks", request.getAdminNote());

            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }
    public void updateList(List<DocumentRequestModel> newList) {
        requestList.clear();
        requestList.addAll(newList);

        requestListFull.clear();
        requestListFull.addAll(newList);

        notifyDataSetChanged();
    }
    @Override
    public Filter getFilter() {
        return requestFilter;
    }

    private final Filter requestFilter = new Filter() {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            List<DocumentRequestModel> filteredList = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {

                filteredList.addAll(requestListFull);

            } else {

                String filterPattern =
                        constraint.toString().toLowerCase().trim();

                for (DocumentRequestModel item : requestListFull) {

                    if (item.getDocumentType().toLowerCase().contains(filterPattern)
                            || item.getRequestCategory().toLowerCase().contains(filterPattern)
                            || item.getPurpose().toLowerCase().contains(filterPattern)
                            || item.getRequestStatus().toLowerCase().contains(filterPattern)
                            || item.getRequesterName().toLowerCase().contains(filterPattern)
                            || item.getRequestTicket().toLowerCase().contains(filterPattern)) {

                        filteredList.add(item);
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

            requestList.clear();
            requestList.addAll((List<DocumentRequestModel>) results.values);
            notifyDataSetChanged();
        }
    };

    private void setStatusDesign(
            TextView statusText,
            String status
    ) {

        if(status == null) return;

        switch (status.toLowerCase()) {

            case "pending":
                statusText.setBackgroundResource(
                        R.drawable.pending_document_request
                );
                statusText.setTextColor(
                        context.getColor(R.color.darkyellow)

                );
                statusText.setText("Pending");
                break;

            case "under review":
                statusText.setBackgroundResource(
                        R.drawable.under_review_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.darkyellow)
                );
                statusText.setText("Under Review");
                break;

            case "approved":
            case "approved and ready":
                statusText.setBackgroundResource(
                        R.drawable.approved_and_ready_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.green)
                );
                statusText.setText("Approved");
                break;

            case "rejected":
                statusText.setBackgroundResource(
                        R.drawable.rejected_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.rejectedtext)
                );
                statusText.setText("Rejected");
                break;

            case "cancelled":
                statusText.setBackgroundResource(
                        R.drawable.cancelled_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.black)
                );
                statusText.setText("Cancelled");
                break;
        }
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView requestCategory;
        TextView requestStatus;
        TextView requestType;
        TextView requestPurpose;
        TextView requestDate;
        TextView viewDetailsTV;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            requestCategory =
                    itemView.findViewById(R.id.requestCategory);

            requestStatus =
                    itemView.findViewById(R.id.requestStatus);

            requestType =
                    itemView.findViewById(R.id.requestType);

            requestPurpose =
                    itemView.findViewById(R.id.requestPurpose);

            requestDate =
                    itemView.findViewById(R.id.requestDate);

            viewDetailsTV = itemView.findViewById(R.id.viewDetailsTV);
        }
    }
}