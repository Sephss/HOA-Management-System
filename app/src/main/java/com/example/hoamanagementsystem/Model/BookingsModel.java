package com.example.hoamanagementsystem.Model;

public class BookingsModel {
    private String bookingID, bookerID, bookingStatus, bookerPurpose, bookerRemarks, bookerName, bookerSport, dateBooked, timeBooked, requestBookingDate, requestBookingTimeIn, requestBookingsTimeOut, timestamp, adminRemarks, approvedDate, rejectedDate, cancelledDate;
    public BookingsModel() {

    }

    public String getBookingID() {
        return bookingID;
    }

    public void setBookingID(String bookingID) {
        this.bookingID = bookingID;
    }

    public String getBookerID() {
        return bookerID;
    }

    public void setBookerID(String bookerID) {
        this.bookerID = bookerID;
    }

    public String getBookerPurpose() {
        return bookerPurpose;
    }

    public void setBookerPurpose(String bookerPurpose) {
        this.bookerPurpose = bookerPurpose;
    }

    public String getBookerRemarks() {
        return bookerRemarks;
    }

    public void setBookerRemarks(String bookerRemarks) {
        this.bookerRemarks = bookerRemarks;
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

    public String getDateBooked() {
        return dateBooked;
    }

    public void setDateBooked(String dateBooked) {
        this.dateBooked = dateBooked;
    }

    public String getTimeBooked() {
        return timeBooked;
    }

    public void setTimeBooked(String timeBooked) {
        this.timeBooked = timeBooked;
    }

    public String getRequestBookingDate() {
        return requestBookingDate;
    }

    public void setRequestBookingDate(String requestBookingDate) {
        this.requestBookingDate = requestBookingDate;
    }

    public String getRequestBookingTimeIn() {
        return requestBookingTimeIn;
    }

    public void setRequestBookingTimeIn(String requestBookingTimeIn) {
        this.requestBookingTimeIn = requestBookingTimeIn;
    }

    public String getRequestBookingsTimeOut() {
        return requestBookingsTimeOut;
    }

    public void setRequestBookingsTimeOut(String requestBookingsTimeOut) {
        this.requestBookingsTimeOut = requestBookingsTimeOut;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public String getApprovedDate() {
        return approvedDate;
    }

    public void setApprovedDate(String approvedDate) {
        this.approvedDate = approvedDate;
    }

    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getBookingStatus() {
        return bookingStatus;
    }

    public void setBookingStatus(String bookingStatus) {
        this.bookingStatus = bookingStatus;
    }

    public String getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(String cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public BookingsModel(String bookingID, String bookerID, String bookingStatus, String bookerPurpose, String bookerRemarks, String bookerName, String bookerSport, String dateBooked, String timeBooked, String requestBookingDate, String requestBookingTimeIn, String requestBookingsTimeOut, String timestamp, String adminRemarks, String approvedDate, String rejectedDate, String cancelledDate) {
        this.bookingID = bookingID;
        this.bookerID = bookerID;
        this.bookingStatus = bookingStatus;
        this.bookerPurpose = bookerPurpose;
        this.bookerRemarks = bookerRemarks;
        this.bookerName = bookerName;
        this.bookerSport = bookerSport;
        this.dateBooked = dateBooked;
        this.timeBooked = timeBooked;
        this.requestBookingDate = requestBookingDate;
        this.requestBookingTimeIn = requestBookingTimeIn;
        this.requestBookingsTimeOut = requestBookingsTimeOut;
        this.timestamp = timestamp;
        this.adminRemarks = adminRemarks;
        this.approvedDate = approvedDate;
        this.rejectedDate = rejectedDate;
        this.cancelledDate = cancelledDate;
    }
}
