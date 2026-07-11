package com.example.hoamanagementsystem.Model;

public class FinancialModel {
    String reportType, reportPurpose, reportAdditionalRemarks, dateRequested, requesterName, requesterID, requestID, underReviewDate, completedDate, rejectedDate, cancelledDate, adminRemarks, adminRemarksLink;

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getReportPurpose() {
        return reportPurpose;
    }

    public void setReportPurpose(String reportPurpose) {
        this.reportPurpose = reportPurpose;
    }

    public String getReportAdditionalRemarks() {
        return reportAdditionalRemarks;
    }

    public void setReportAdditionalRemarks(String reportAdditionalRemarks) {
        this.reportAdditionalRemarks = reportAdditionalRemarks;
    }

    public String getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(String dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getRequesterName() {
        return requesterName;
    }

    public void setRequesterName(String requesterName) {
        this.requesterName = requesterName;
    }

    public String getRequesterID() {
        return requesterID;
    }

    public void setRequesterID(String requesterID) {
        this.requesterID = requesterID;
    }

    public String getRequestID() {
        return requestID;
    }

    public void setRequestID(String requestID) {
        this.requestID = requestID;
    }

    public String getUnderReviewDate() {
        return underReviewDate;
    }

    public void setUnderReviewDate(String underReviewDate) {
        this.underReviewDate = underReviewDate;
    }

    public String getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(String completedDate) {
        this.completedDate = completedDate;
    }

    public String getRejectedDate() {
        return rejectedDate;
    }

    public void setRejectedDate(String rejectedDate) {
        this.rejectedDate = rejectedDate;
    }

    public String getCancelledDate() {
        return cancelledDate;
    }

    public void setCancelledDate(String cancelledDate) {
        this.cancelledDate = cancelledDate;
    }

    public String getAdminRemarks() {
        return adminRemarks;
    }

    public void setAdminRemarks(String adminRemarks) {
        this.adminRemarks = adminRemarks;
    }

    public String getAdminRemarksLink() {
        return adminRemarksLink;
    }

    public void setAdminRemarksLink(String adminRemarksLink) {
        this.adminRemarksLink = adminRemarksLink;
    }

    public FinancialModel(String reportType,
                          String reportPurpose,
                          String reportAdditionalRemarks,
                          String dateRequested,
                          String requesterName,
                          String requesterID,
                          String requestID,
                          String underReviewDate,
                          String completedDate,
                          String rejectedDate,
                          String cancelledDate,
                          String adminRemarks,
                          String adminRemarksLink) {

        this.reportType = reportType;
        this.reportPurpose = reportPurpose;
        this.reportAdditionalRemarks = reportAdditionalRemarks;
        this.dateRequested = dateRequested;
        this.requesterName = requesterName;
        this.requesterID = requesterID;
        this.requestID = requestID;
        this.underReviewDate = underReviewDate;
        this.completedDate = completedDate;
        this.rejectedDate = rejectedDate;
        this.cancelledDate = cancelledDate;
        this.adminRemarks = adminRemarks;
        this.adminRemarksLink = adminRemarksLink;
    }
    public FinancialModel() {

    }
}
