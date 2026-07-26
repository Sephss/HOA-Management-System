package com.example.hoamanagementsystem.Model;

public class AttendeeModel {
    private String homeownerId;
    private String homeownerName;
    private String unitNumber;
    private long timestamp;

    public AttendeeModel() { } // required for Firebase deserialization

    public AttendeeModel(String homeownerId, String homeownerName, String unitNumber, long timestamp) {
        this.homeownerId = homeownerId;
        this.homeownerName = homeownerName;
        this.unitNumber = unitNumber;
        this.timestamp = timestamp;
    }

    public String getHomeownerId() { return homeownerId; }
    public String getHomeownerName() { return homeownerName; }
    public String getUnitNumber() { return unitNumber; }
    public long getTimestamp() { return timestamp; }

    public void setHomeownerId(String homeownerId) { this.homeownerId = homeownerId; }
    public void setHomeownerName(String homeownerName) { this.homeownerName = homeownerName; }
    public void setUnitNumber(String unitNumber) { this.unitNumber = unitNumber; }
    public void setTimestamp(long timestamp) { this.timestamp = timestamp; }

}
