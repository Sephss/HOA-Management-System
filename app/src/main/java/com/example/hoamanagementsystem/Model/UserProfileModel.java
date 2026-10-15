package com.example.hoamanagementsystem.Model;

/**
 * Mirrors users/{uid} field-for-field.
 * Public fields so Firebase maps them directly (no getters/setters needed).
 * All values are Strings, matching how they are stored ("yes"/"no", "2", dates, etc.).
 */
public class UserProfileModel {

    // ---- Account / registration ----
    public String uid;
    public String firstName;
    public String middleName;
    public String lastName;
    public String phoneNumber;
    public String email;
    public String block;
    public String lot;
    public String street;
    public String role;
    public String lavanyaPhaseType;
    public String imageUrl;

    // ---- Admin-controlled flags (read only from the app) ----
    public String isAccountBanned;
    public String isAccountDisabled;
    public String isAccountApprovedByAdmin;
    public String isArchived;
    public String archivedAt;
    public String archivedBy;
    public String archivedReason;

    // ---- Homeowner Information (Primary Member) ----
    public String birthday;
    public String gender;
    public String civilStatus;
    public String preferredCommunication;
    public String messengerName;

    // ---- Property Information ----
    public String dateOccupied;
    public String dateAcquired;
    public String propertyNotes;

    // ---- Emergency contact ----
    public String emergencyContactName;
    public String emergencyContactRelationship;
    public String emergencyContactNumber;

    // ---- Tenant Information ----
    public String tenantName;
    public String tenantContact;
    public String leaseStart;
    public String leaseEnd;
    public String tenantNumberOfOccupants;

    // ---- Documents ----
    public String supportingDocument1Url;
    public String supportingDocument2Url;
    public String validIdUrl;          // exists in DB, not edited here
    public String leaseContractUrl;    // exists in DB, not edited here
    public String authorizationLetterUrl; // exists in DB, not edited here

    // ---- Vehicle Information ----
    public String numberOfVehicles;
    public String vehicleType;
    public String plateNumber;
    public String vehicleMake;
    public String vehicleModel;
    public String vehicleColor;

    // ---- Pet Information ----
    public String numberOfPets;
    public String petType;
    public String petOtherType;
    public String petVaccinated;
    public String petNeutered;
    public String supportsPetWelfare;
    public String supportsStrayProgram;

    public UserProfileModel() {
    }
}