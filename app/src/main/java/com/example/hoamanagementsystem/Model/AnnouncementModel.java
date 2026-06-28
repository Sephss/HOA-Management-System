package com.example.hoamanagementsystem.Model;

public class AnnouncementModel {
    String title, description, category, date, time, link, announcementId, announcerId, announcerName, announcerRole, dateCreated, timeCreated;
    long timestamp;
    public AnnouncementModel () {

    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getAnnouncerId() {
        return announcerId;
    }

    public void setAnnouncerId(String announcerId) {
        this.announcerId = announcerId;
    }

    public String getAnnouncerName() {
        return announcerName;
    }

    public void setAnnouncerName(String announcerName) {
        this.announcerName = announcerName;
    }

    public String getAnnouncerRole() {
        return announcerRole;
    }

    public void setAnnouncerRole(String announcerRole) {
        this.announcerRole = announcerRole;
    }

    public String getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(String dateCreated) {
        this.dateCreated = dateCreated;
    }

    public String getTimeCreated() {
        return timeCreated;
    }

    public void setTimeCreated(String timeCreated) {
        this.timeCreated = timeCreated;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public AnnouncementModel(String title, String description, String category, String date, String time, String link, String announcementId, String announcerId, String announcerName, String announcerRole, String dateCreated, String timeCreated, long timestamp) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.date = date;
        this.time = time;
        this.link = link;
        this.announcementId = announcementId;
        this.announcerId = announcerId;
        this.announcerName = announcerName;
        this.announcerRole = announcerRole;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.timestamp = timestamp;
    }
}
