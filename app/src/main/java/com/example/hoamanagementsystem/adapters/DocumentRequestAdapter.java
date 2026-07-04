package com.example.hoamanagementsystem.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.hoamanagementsystem.Model.DocumentRequestModel;
import com.example.hoamanagementsystem.Modules.RequestDocumensClicked;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class DocumentRequestAdapter
        extends RecyclerView.Adapter<DocumentRequestAdapter.ViewHolder> {

    private final Context context;
    private final List<DocumentRequestModel> requestList;

    public DocumentRequestAdapter(
            Context context,
            List<DocumentRequestModel> requestList
    ) {
        this.context = context;
        this.requestList = requestList;
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


            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return requestList.size();
    }

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
                break;

            case "under review":
                statusText.setBackgroundResource(
                        R.drawable.under_review_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.darkyellow)
                );
                break;

            case "approved":
            case "approved and ready":
                statusText.setBackgroundResource(
                        R.drawable.approved_and_ready_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.green)
                );
                break;

            case "rejected":
                statusText.setBackgroundResource(
                        R.drawable.rejected_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.rejectedtext)
                );
                break;

            case "cancelled":
                statusText.setBackgroundResource(
                        R.drawable.cancelled_color
                );
                statusText.setTextColor(
                        context.getColor(R.color.black)
                );
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