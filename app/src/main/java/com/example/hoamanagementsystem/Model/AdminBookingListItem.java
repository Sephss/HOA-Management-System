package com.example.hoamanagementsystem.Model;

public class AdminBookingListItem {
    public static final int TYPE_HEADER = 0;
    public static final int TYPE_BOOKING = 1;

    private int type;
    private String headerTitle;
    private BookingsModel booking;

    public static AdminBookingListItem header(String title) {
        AdminBookingListItem item = new AdminBookingListItem();
        item.type = TYPE_HEADER;
        item.headerTitle = title;
        return item;
    }

    public static AdminBookingListItem booking(BookingsModel booking) {
        AdminBookingListItem item = new AdminBookingListItem();
        item.type = TYPE_BOOKING;
        item.booking = booking;
        return item;
    }

    public int getType() { return type; }
    public String getHeaderTitle() { return headerTitle; }
    public BookingsModel getBooking() { return booking; }
}