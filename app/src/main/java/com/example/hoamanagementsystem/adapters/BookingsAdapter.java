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

import com.example.hoamanagementsystem.Model.BookingsModel;
import com.example.hoamanagementsystem.Modules.BookingsClicked;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class BookingsAdapter extends RecyclerView.Adapter<BookingsAdapter.ViewHolder> {

    private Context context;
    private List<BookingsModel> bookingsList;

    public BookingsAdapter(Context context, List<BookingsModel> bookingsList) {
        this.context = context;
        this.bookingsList = bookingsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.booking_item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BookingsModel booking = bookingsList.get(position);

        holder.sportCategory.setText(booking.getBookerSport());
        holder.bookingDate.setText(booking.getRequestBookingDate());
        holder.bookingTime.setText(booking.getRequestBookingTimeIn() + " - " + booking.getRequestBookingsTimeOut());
        holder.bookingPurpose.setText(booking.getBookerPurpose());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingsClicked.class);
            intent.putExtra("bookingID", booking.getBookingID());
            intent.putExtra("bookingSport", booking.getBookerSport());
            intent.putExtra("bookingStatus", booking.getBookingStatus());

            intent.putExtra("reqeustBookingDate", booking.getRequestBookingDate());
            intent.putExtra("requestBookingTime", booking.getRequestBookingTimeIn() + " " + booking.getRequestBookingsTimeOut());

            intent.putExtra("bookingPurpose", booking.getBookerPurpose());
            intent.putExtra("bookerRemarks", booking.getBookerRemarks());

            intent.putExtra("bookerName", booking.getBookerName());
            intent.putExtra("bookedDate", booking.getDateBooked());

            intent.putExtra("adminRemarks", booking.getAdminRemarks());

            intent.putExtra("requestBookingTime", booking.getRequestBookingTimeIn() + " - " + booking.getRequestBookingsTimeOut());
            context.startActivity(intent);
        });

        String remarks = booking.getBookerRemarks();
        if (remarks == null || remarks.trim().isEmpty()) {
            holder.bookingRemarks.setVisibility(View.GONE);
        } else {
            holder.bookingRemarks.setVisibility(View.VISIBLE);
            holder.bookingRemarks.setText(remarks);
        }

        String status = booking.getBookingStatus();
        holder.bookingStatus.setText(capitalize(status));

        if ("cancelled".equalsIgnoreCase(status)) {
            holder.bookingStatus.setTextColor(ContextCompat.getColor(context, R.color.grey));
        } else {
            holder.bookingStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public int getItemCount() {
        return bookingsList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView sportCategory, bookingStatus, bookingDate, bookingTime, bookingPurpose, bookingRemarks;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sportCategory = itemView.findViewById(R.id.sportCategory);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);
            bookingDate = itemView.findViewById(R.id.bookingDate);
            bookingTime = itemView.findViewById(R.id.bookingTime);
            bookingPurpose = itemView.findViewById(R.id.bookingPurpose);
            bookingRemarks = itemView.findViewById(R.id.bookingRemarks);
        }
    }
}