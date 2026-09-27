package com.example.hoamanagementsystem.Model;

public class ErrorTicketEntry {
    private String ticketId;
    private String userId;
    private String category;      // "Bug / Error" or "Suggestion / Feature"
    private String title;
    private String description;
    private String imageUrl;
    private String status;        // "Pending", "In Progress", "Resolved", "Rejected"
    private String submittedByName;
    private String dateCreated;
    private String timeCreated;
    private long timestamp;

    // Required empty constructor for Firebase
    public ErrorTicketEntry() {
    }

    public ErrorTicketEntry(String ticketId, String userId, String category, String title,
                            String description, String imageUrl, String status,
                            String submittedByName, String dateCreated, String timeCreated, long timestamp) {
        this.ticketId = ticketId;
        this.userId = userId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.status = status;
        this.submittedByName = submittedByName;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.timestamp = timestamp;
    }

    public String getTicketId() { return ticketId; }
    public void setTicketId(String ticketId) { this.ticketId = ticketId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getSubmittedByName() { return submittedByName; }
    public void setSubmittedByName(String submittedByName) { this.submittedByName = submittedByName; }

    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public String getTimeCreated() { return timeCreated; }
    public void setTimeCreated(String timeCreated) { this.timeCreated = timeCreated; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}