package com.example.hoamanagementsystem.Model;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class CommunitySolicitationEntry {
    private String solicitId;
    private String title;
    private String description;
    private String imageUrl;
    private String bankName;
    private String accountName;
    private String accountNumber;
    private String status; // "active" or "closed"
    private String postedById;
    private String postedByName;
    private String postedByRole;
    private String dateCreated;
    private String timeCreated;
    private long timestamp;

    // Required empty constructor for Firebase
    public CommunitySolicitationEntry() {
    }

    public String getSolicitId() { return solicitId; }
    public void setSolicitId(String solicitId) { this.solicitId = solicitId; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getBankName() { return bankName; }
    public void setBankName(String bankName) { this.bankName = bankName; }

    public String getAccountName() { return accountName; }
    public void setAccountName(String accountName) { this.accountName = accountName; }

    public String getAccountNumber() { return accountNumber; }
    public void setAccountNumber(String accountNumber) { this.accountNumber = accountNumber; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPostedById() { return postedById; }
    public void setPostedById(String postedById) { this.postedById = postedById; }

    public String getPostedByName() { return postedByName; }
    public void setPostedByName(String postedByName) { this.postedByName = postedByName; }

    public String getPostedByRole() { return postedByRole; }
    public void setPostedByRole(String postedByRole) { this.postedByRole = postedByRole; }

    public String getDateCreated() { return dateCreated; }
    public void setDateCreated(String dateCreated) { this.dateCreated = dateCreated; }

    public String getTimeCreated() { return timeCreated; }
    public void setTimeCreated(String timeCreated) { this.timeCreated = timeCreated; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}