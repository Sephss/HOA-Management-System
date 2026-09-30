package com.example.hoamanagementsystem.Model;

public class ContributionEntry {
    private String uid;
    private String contributorName;
    private String controlNumber;
    private String amountSent;
    private String signatureUrl;
    private String date;
    private String time;
    private long timestamp;

    // Required empty constructor for Firebase
    public ContributionEntry() {
    }

    public ContributionEntry(String uid, String contributorName, String controlNumber,
                             String amountSent, String signatureUrl, String date,
                             String time, long timestamp) {
        this.uid = uid;
        this.contributorName = contributorName;
        this.controlNumber = controlNumber;
        this.amountSent = amountSent;
        this.signatureUrl = signatureUrl;
        this.date = date;
        this.time = time;
        this.timestamp = timestamp;
    }

    public String getUid() { return uid; }
    public void setUid(String uid) { this.uid = uid; }

    public String getContributorName() { return contributorName; }
    public void setContributorName(String contributorName) { this.contributorName = contributorName; }

    public String getControlNumber() { return controlNumber; }
    public void setControlNumber(String controlNumber) { this.controlNumber = controlNumber; }

    public String getAmountSent() { return amountSent; }
    public void setAmountSent(String amountSent) { this.amountSent = amountSent; }

    public String getSignatureUrl() { return signatureUrl; }
    public void setSignatureUrl(String signatureUrl) { this.signatureUrl = signatureUrl; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public long getTimestamp() { return timestamp; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}