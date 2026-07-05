package com.example.hoamanagementsystem.Model;

public class GrievanceModel {
    String incidentType, incidentTitle, incidentDescription, incidentExactLocation, incidentImageUrl, incidentReportID, incidentReporterID, incidentStatus, incidentTicket, dateSubmitted, timeSubmitted, adminRemarks, underInvestigationDate, resolvedDate;
    public GrievanceModel () {

    }

    public String getIncidentType() {
        return incidentType;
    }

    public void setIncidentType(String incidentType) {
        this.incidentType = incidentType;
    }

    public String getIncidentTitle() {
        return incidentTitle;
    }

    public void setIncidentTitle(String incidentTitle) {
        this.incidentTitle = incidentTitle;
    }

    public String getIncidentDescription() {
        return incidentDescription;
    }

    public void setIncidentDescription(String incidentDescription) {
        this.incidentDescription = incidentDescription;
    }

    public String getIncidentExactLocation() {
        return incidentExactLocation;
    }

    public void setIncidentExactLocation(String incidentExactLocation) {
        this.incidentExactLocation = incidentExactLocation;
    }

    public String getIncidentImageUrl() {
        return incidentImageUrl;
    }

    public void setIncidentImageUrl(String incidentImageUrl) {
        this.incidentImageUrl = incidentImageUrl;
    }

    public String getIncidentReportID() {
        return incidentReportID;
    }

    public void setIncidentReportID(String incidentReportID) {
        this.incidentReportID = incidentReportID;
    }

    public String getIncidentReporterID() {
        return incidentReporterID;
    }

    public void setIncidentReporterID(String incidentReporterID) {
        this.incidentReporterID = incidentReporterID;
    }

    public String getIncidentStatus() {
        return incidentStatus;
    }

    public void setIncidentStatus(String incidentStatus) {
        this.incidentStatus = incidentStatus;
    }

    public String getIncidentTicket() {
        return incidentTicket;
    }

    public void setIncidentTicket(String incidentTicket) {
        this.incidentTicket = incidentTicket;
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

    public GrievanceModel(String incidentType,
                          String incidentTitle,
                          String incidentDescription,
                          String incidentExactLocation,
                          String incidentImageUrl,
                          String incidentReportID,
                          String incidentReporterID,
                          String incidentStatus,
                          String incidentTicket,
                          String dateSubmitted,
                          String timeSubmitted,
                          String adminRemarks,
                          String underInvestigationDate,
                          String resolvedDate) {
        this.incidentType = incidentType;
        this.incidentTitle = incidentTitle;
        this.incidentDescription = incidentDescription;
        this.incidentExactLocation = incidentExactLocation;
        this.incidentImageUrl = incidentImageUrl;
        this.incidentReportID = incidentReportID;
        this.incidentReporterID = incidentReporterID;
        this.incidentStatus = incidentStatus;
        this.incidentTicket = incidentTicket;
        this.dateSubmitted = dateSubmitted;
        this.timeSubmitted = timeSubmitted;
        this.adminRemarks = adminRemarks;
        this.underInvestigationDate = underInvestigationDate;
        this.resolvedDate = resolvedDate;

    }
}
