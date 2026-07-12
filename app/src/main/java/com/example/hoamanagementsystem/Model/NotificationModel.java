package com.example.hoamanagementsystem.Model;

public class NotificationModel {
    String notificationID, receiverID, title, message, notificationType, action, date, time, referenceID, isRead, timestamp, notifMessage;
    public NotificationModel() {

    }

    public String getNotificationID() {
        return notificationID;
    }

    public void setNotificationID(String notificationID) {
        this.notificationID = notificationID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getReferenceID() {
        return referenceID;
    }

    public void setReferenceID(String referenceID) {
        this.referenceID = referenceID;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getNotifMessage() {
        return notifMessage;
    }

    public void setNotifMessage(String notifMessage) {
        this.notifMessage = notifMessage;
    }

    public NotificationModel(String notificationID, String receiverID, String title, String message, String notificationType, String action, String date, String time, String referenceID, String isRead, String timestamp, String notifMessage) {
        this.notificationID = notificationID;
        this.receiverID = receiverID;
        this.title = title;
        this.message = message;
        this.notificationType = notificationType;
        this.action = action;
        this.date = date;
        this.time = time;
        this.referenceID = referenceID;
        this.isRead = isRead;
        this.timestamp = timestamp;
        this.notifMessage = notifMessage;
    }

}
