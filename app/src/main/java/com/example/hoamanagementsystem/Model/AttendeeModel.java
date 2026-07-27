package com.example.hoamanagementsystem.Model;

public class AttendeeModel {
    private String homeownerId, homeownerName, block, lot, street, role, lavanyaPhaseType, status, reason;
    private long timestamp;

    public AttendeeModel() { } // required for Firebase deserialization

    public AttendeeModel(String homeownerId, String homeownerName, String block, String lot, String street,
                         String role, String lavanyaPhaseType, String status, String reason, long timestamp) {
        this.homeownerId = homeownerId;
        this.homeownerName = homeownerName;
        this.block = block;
        this.lot = lot;
        this.street = street;
        this.role = role;
        this.lavanyaPhaseType = lavanyaPhaseType;
        this.status = status;
        this.reason = reason;
        this.timestamp = timestamp;
    }

    public String getHomeownerId() { return homeownerId; }
    public String getHomeownerName() { return homeownerName; }
    public String getBlock() { return block; }
    public String getLot() { return lot; }
    public String getStreet() { return street; }
    public String getRole() { return role; }
    public String getLavanyaPhaseType() { return lavanyaPhaseType; }
    public String getStatus() { return status; }
    public String getReason() { return reason; }
    public long getTimestamp() { return timestamp; }

    public void setHomeownerId(String homeownerId) { this.homeownerId = homeownerId; }
    public void setHomeownerName(String homeownerName) { this.homeownerName = homeownerName; }
    public void setBlock(String block) { this.block = block; }
    public void setLot(String lot) { this.lot = lot; }
    public void setStreet(String street) { this.street = street; }
    public void setRole(String role) { this.role = role; }
    public void setLavanyaPhaseType(String lavanyaPhaseType) { this.lavanyaPhaseType = lavanyaPhaseType; }
    public void setStatus(String status) { this.status = status; }
    public void setReason(String reason) { this.reason = reason; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }
}