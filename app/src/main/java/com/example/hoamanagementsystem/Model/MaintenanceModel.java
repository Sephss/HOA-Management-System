package com.example.hoamanagementsystem.Model;

public class MaintenanceModel {

    String maintenanceType, maintenanceTitle, urgencyLevel, fullDescription,
            exactLocation, photoEvidence, submitterName, submitterID,
            maintenanceStatus, maintenanceID, maintenanceTicket,
            dateSubmitted, timeSubmitted, adminRemarks,
            underInvestigationDate, resolvedDate, timestamp;

    public String getMaintenanceType() {
        return maintenanceType;
    }

    public void setMaintenanceType(String maintenanceType) {
        this.maintenanceType = maintenanceType;
    }

    public String getMaintenanceTitle() {
        return maintenanceTitle;
    }

    public void setMaintenanceTitle(String maintenanceTitle) {
        this.maintenanceTitle = maintenanceTitle;
    }

    public String getUrgencyLevel() {
        return urgencyLevel;
    }

    public void setUrgencyLevel(String urgencyLevel) {
        this.urgencyLevel = urgencyLevel;
    }

    public String getFullDescription() {
        return fullDescription;
    }

    public void setFullDescription(String fullDescription) {
        this.fullDescription = fullDescription;
    }

    public String getExactLocation() {
        return exactLocation;
    }

    public void setExactLocation(String exactLocation) {
        this.exactLocation = exactLocation;
    }

    public String getPhotoEvidence() {
        return photoEvidence;
    }

    public void setPhotoEvidence(String photoEvidence) {
        this.photoEvidence = photoEvidence;
    }

    public String getSubmitterName() {
        return submitterName;
    }

    public void setSubmitterName(String submitterName) {
        this.submitterName = submitterName;
    }

    public String getSubmitterID() {
        return submitterID;
    }

    public void setSubmitterID(String submitterID) {
        this.submitterID = submitterID;
    }

    public String getMaintenanceStatus() {
        return maintenanceStatus;
    }

    public void setMaintenanceStatus(String maintenanceStatus) {
        this.maintenanceStatus = maintenanceStatus;
    }

    public String getMaintenanceID() {
        return maintenanceID;
    }

    public void setMaintenanceID(String maintenanceID) {
        this.maintenanceID = maintenanceID;
    }

    public String getMaintenanceTicket() {
        return maintenanceTicket;
    }

    public void setMaintenanceTicket(String maintenanceTicket) {
        this.maintenanceTicket = maintenanceTicket;
    }

    public String getDateSubmitted() {
        return dateSubmitted;
    }

    public void setDateSubmitted(String dateSubmitted) {
        this.dateSubmitted = dateSubmitted;
    }

    public String getTimeSubmitted() {
        return timeSubmitted;
    }

    public void setTimeSubmitted(String timeSubmitted) {
        this.timeSubmitted = timeSubmitted;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public String getUnderInvestigationDate() {
        return underInvestigationDate;
    }

    public void setUnderInvestigationDate(String underInvestigationDate) {
        this.underInvestigationDate = underInvestigationDate;
    }

    public String getResolvedDate() {
        return resolvedDate;
    }

    public void setResolvedDate(String resolvedDate) {
        this.resolvedDate = resolvedDate;
    }

    public MaintenanceModel(
            String maintenanceType,
            String maintenanceTitle,
            String urgencyLevel,
            String fullDescription,
            String exactLocation,
            String photoEvidence,
            String submitterName,
            String submitterID,
            String maintenanceStatus,
            String maintenanceID,
            String maintenanceTicket,
            String dateSubmitted,
            String timeSubmitted,
            String adminRemarks,
            String underInvestigationDate,
            String resolvedDate, String timestamp) {

        this.maintenanceType = maintenanceType;
        this.maintenanceTitle = maintenanceTitle;
        this.urgencyLevel = urgencyLevel;
        this.fullDescription = fullDescription;
        this.exactLocation = exactLocation;
        this.photoEvidence = photoEvidence;
        this.submitterName = submitterName;
        this.submitterID = submitterID;
        this.maintenanceStatus = maintenanceStatus;
        this.maintenanceID = maintenanceID;
        this.maintenanceTicket = maintenanceTicket;
        this.dateSubmitted = dateSubmitted;
        this.timeSubmitted = timeSubmitted;
        this.adminRemarks = adminRemarks;
        this.underInvestigationDate = underInvestigationDate;
        this.resolvedDate = resolvedDate;
        this.timestamp = timestamp;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public MaintenanceModel() {

    }
}