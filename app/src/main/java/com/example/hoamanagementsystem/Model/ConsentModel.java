package com.example.hoamanagementsystem.Model;

public class ConsentModel {
    String consentGivenBy, consentTimestamp, consentGiven, consentPurpose, consentId, consentGiverId;
    public ConsentModel() {

    }

    public String getConsentId() {
        return consentId;
    }

    public void setConsentId(String consentId) {
        this.consentId = consentId;
    }

    public String getConsentGiverId() {
        return consentGiverId;
    }

    public void setConsentGiverId(String consentGiverId) {
        this.consentGiverId = consentGiverId;
    }

    public ConsentModel(String consentGivenBy, String consentTimestamp, String consentGiven, String consentPurpose, String consentId, String consentGiverId) {
        this.consentGivenBy = consentGivenBy;
        this.consentTimestamp = consentTimestamp;
        this.consentGiven = consentGiven;
        this.consentPurpose = consentPurpose;
        this.consentId = consentId;
        this.consentGiverId = consentGiverId;
    }

    public String getConsentGivenBy() {
        return consentGivenBy;
    }

    public void setConsentGivenBy(String consentGivenBy) {
        this.consentGivenBy = consentGivenBy;
    }

    public String getConsentTimestamp() {
        return consentTimestamp;
    }

    public void setConsentTimestamp(String consentTimestamp) {
        this.consentTimestamp = consentTimestamp;
    }

    public String getConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(String consentGiven) {
        this.consentGiven = consentGiven;
    }

    public String getConsentPurpose() {
        return consentPurpose;
    }

    public void setConsentPurpose(String consentPurpose) {
        this.consentPurpose = consentPurpose;
    }
}
