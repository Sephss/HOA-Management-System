package com.example.hoamanagementsystem.Model;

public class BookingSlotModel {
    private String bookingID, bookerName, bookerSport, slotDate, slot, timeIn, timeOut;

    public BookingSlotModel() {
        // required empty constructor for Firebase deserialization
    }

    public BookingSlotModel(String bookingID, String bookerName, String bookerSport, String slotDate, String slot, String timeIn, String timeOut) {
        this.bookingID = bookingID;
        this.bookerName = bookerName;
        this.bookerSport = bookerSport;
        this.slotDate = slotDate;
        this.slot = slot;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getBookerName() {
        return bookerName;
    }

    public void setBookerName(String bookerName) {
        this.bookerName = bookerName;
    }

    public String getBookerSport() {
        return bookerSport;
    }

    public void setBookerSport(String bookerSport) {
        this.bookerSport = bookerSport;
    }

    public String getSlotDate() {
        return slotDate;
    }

    public void setSlotDate(String slotDate) {
        this.slotDate = slotDate;
    }

    public String getSlot() {
        return slot;
    }

    public void setSlot(String slot) {
        this.slot = slot;
    }

    public String getTimeIn() {
        return timeIn;
    }

    public void setTimeIn(String timeIn) {
        this.timeIn = timeIn;
    }

    public String getTimeOut() {
        return timeOut;
    }

    public void setTimeOut(String timeOut) {
        this.timeOut = timeOut;
    }

}
