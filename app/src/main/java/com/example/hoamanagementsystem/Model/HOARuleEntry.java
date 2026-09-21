package com.example.hoamanagementsystem.Model;

public class HOARuleEntry {
    private String ruleId;
    private String category;
    private String title;
    private String description;
    private String imageUrl;
    private String link;
    private String postedById;
    private String postedByName;
    private String postedByRole;
    private String dateCreated;
    private String timeCreated;
    private long timestamp;

    // Required empty constructor for Firebase
    public HOARuleEntry() {
    }

    public HOARuleEntry(String ruleId, String category, String title, String description,
                        String imageUrl, String link, String postedById, String postedByName,
                        String postedByRole, String dateCreated, String timeCreated, long timestamp) {
        this.ruleId = ruleId;
        this.category = category;
        this.title = title;
        this.description = description;
        this.imageUrl = imageUrl;
        this.link = link;
        this.postedById = postedById;
        this.postedByName = postedByName;
        this.postedByRole = postedByRole;
        this.dateCreated = dateCreated;
        this.timeCreated = timeCreated;
        this.timestamp = timestamp;
    }

    public String getRuleId() { return ruleId; }
    public void setRuleId(String ruleId) { this.ruleId = ruleId; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getLink() { return link; }
    public void setLink(String link) { this.link = link; }

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