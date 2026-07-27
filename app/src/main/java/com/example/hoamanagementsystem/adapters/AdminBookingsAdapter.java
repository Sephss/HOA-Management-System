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

import com.example.hoamanagementsystem.Model.AdminBookingListItem;
import com.example.hoamanagementsystem.Model.BookingsModel;
import com.example.hoamanagementsystem.Modules.BookingsClicked;
import com.example.hoamanagementsystem.R;

import java.util.List;

public class AdminBookingsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_HEADER = 0;
    private static final int TYPE_BOOKING = 1;

    private Context context;
    private List<AdminBookingListItem> items;

    public AdminBookingsAdapter(Context context, List<AdminBookingListItem> items) {
        this.context = context;
        this.items = items;
    }

    @Override
    public int getItemViewType(int position) {
        return items.get(position).getType() == AdminBookingListItem.TYPE_HEADER ? TYPE_HEADER : TYPE_BOOKING;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.admin_booking_header_layout, parent, false);
            return new HeaderViewHolder(view);
        } else {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.admin_booking_layout, parent, false);
            return new BookingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AdminBookingListItem item = items.get(position);

        if (holder instanceof HeaderViewHolder) {
            HeaderViewHolder headerHolder = (HeaderViewHolder) holder;
            headerHolder.headerTitle.setText(item.getHeaderTitle());

            // count how many bookings follow this header until the next header
            int count = 0;
            for (int i = position + 1; i < items.size() && items.get(i).getType() == AdminBookingListItem.TYPE_BOOKING; i++) {
                count++;
            }
            headerHolder.headerCount.setText(count + (count == 1 ? " reservation" : " reservations"));

        } else if (holder instanceof BookingViewHolder) {
            bindBooking((BookingViewHolder) holder, item.getBooking());
        }
    }

    private void bindBooking(BookingViewHolder holder, BookingsModel booking) {
        holder.timeIn.setText(booking.getRequestBookingTimeIn());
        holder.timeOut.setText(booking.getRequestBookingsTimeOut());
        holder.sportCategory.setText(booking.getBookerSport());
        holder.bookerName.setText(booking.getBookerName());
        holder.bookingPurpose.setText(booking.getBookerPurpose());

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, BookingsClicked.class);
            intent.putExtra("bookingID", booking.getBookingID());
            intent.putExtra("bookingSport", booking.getBookerSport());
            intent.putExtra("bookingStatus", booking.getBookingStatus());
            intent.putExtra("reqeustBookingDate", booking.getRequestBookingDate());
            intent.putExtra("requestBookingTime", booking.getRequestBookingTimeIn() + " - " + booking.getRequestBookingsTimeOut());
            intent.putExtra("bookingPurpose", booking.getBookerPurpose());
            intent.putExtra("bookerRemarks", booking.getBookerRemarks());
            intent.putExtra("bookerName", booking.getBookerName());
            intent.putExtra("bookedDate", booking.getDateBooked());
            intent.putExtra("adminRemarks", booking.getAdminRemarks());
            context.startActivity(intent);
        });

        String status = booking.getBookingStatus();
        holder.bookingStatus.setText(capitalize(status));

        if ("cancelled".equalsIgnoreCase(status)) {
            holder.bookingStatus.setTextColor(ContextCompat.getColor(context, R.color.grey));
            holder.statusAccent.setBackgroundResource(R.drawable.booking_accent_cancelled);
        } else {
            holder.bookingStatus.setTextColor(ContextCompat.getColor(context, R.color.green));
            holder.statusAccent.setBackgroundResource(R.drawable.booking_accent_confirmed);
        }
    }

    private String capitalize(String text) {
        if (text == null || text.isEmpty()) return "";
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class HeaderViewHolder extends RecyclerView.ViewHolder {
        TextView headerTitle, headerCount;

        HeaderViewHolder(@NonNull View itemView) {
            super(itemView);
            headerTitle = itemView.findViewById(R.id.headerTitle);
            headerCount = itemView.findViewById(R.id.headerCount);
        }
    }

    static class BookingViewHolder extends RecyclerView.ViewHolder {
        TextView timeIn, timeOut, sportCategory, bookingStatus, bookerName, bookingPurpose;
        View statusAccent;

        BookingViewHolder(@NonNull View itemView) {
            super(itemView);
            timeIn = itemView.findViewById(R.id.timeIn);
            timeOut = itemView.findViewById(R.id.timeOut);
            sportCategory = itemView.findViewById(R.id.sportCategory);
            bookingStatus = itemView.findViewById(R.id.bookingStatus);
            bookerName = itemView.findViewById(R.id.bookerName);
            bookingPurpose = itemView.findViewById(R.id.bookingPurpose);
            statusAccent = itemView.findViewById(R.id.statusAccent);
        }
    }
}