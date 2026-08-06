package com.example.hoamanagementsystem.Model;

public class BookingSlotModel {
    private String bookingID, bookerName, bookerSport, slotDate, slot, timeIn, timeOut, bookingStatus, bookingORNumber, bookingAmount, whoUpdatedTheBookingStatus, paymentReceivedBy;

    public BookingSlotModel() {
        // required empty constructor for Firebase deserialization
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getBookingORNumber() {
        return bookingORNumber;
    }

    public void setBookingORNumber(String bookingORNumber) {
        this.bookingORNumber = bookingORNumber;
    }

    public String getBookingAmount() {
        return bookingAmount;
    }

    public void setBookingAmount(String bookingAmount) {
        this.bookingAmount = bookingAmount;
    }

    public String getWhoUpdatedTheBookingStatus() {
        return whoUpdatedTheBookingStatus;
    }

    public void setWhoUpdatedTheBookingStatus(String whoUpdatedTheBookingStatus) {
        this.whoUpdatedTheBookingStatus = whoUpdatedTheBookingStatus;
    }

    public String getPaymentReceivedBy() {
        return paymentReceivedBy;
    }

    public void setPaymentReceivedBy(String paymentReceivedBy) {
        this.paymentReceivedBy = paymentReceivedBy;
    }

    public BookingSlotModel(String bookingID, String bookerName, String bookerSport, String slotDate, String slot, String timeIn, String timeOut, String bookingStatus, String bookingORNumber, String bookingAmount, String whoUpdatedTheBookingStatus, String paymentReceivedBy) {
        this.bookingID = bookingID;
        this.bookerName = bookerName;
        this.bookerSport = bookerSport;
        this.slotDate = slotDate;
        this.slot = slot;
        this.timeIn = timeIn;
        this.timeOut = timeOut;
        this.bookingStatus = bookingStatus;
        this.bookingORNumber = bookingORNumber;
        this.bookingAmount = bookingAmount;
        this.whoUpdatedTheBookingStatus = whoUpdatedTheBookingStatus;
        this.paymentReceivedBy = paymentReceivedBy;
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
