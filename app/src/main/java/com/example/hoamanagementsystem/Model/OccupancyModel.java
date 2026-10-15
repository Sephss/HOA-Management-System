package com.example.hoamanagementsystem.Model;

/**
 * OccupancyInformation/{uid}/{recordId}
 * "id" is the recordId (the node key). It is filled in when reading and is never written as a field.
 */
public class OccupancyModel {
    public String id;
    public String fullName;
    public String relationshipToOwner;
    public String age;
    public String contactNumber;

    public OccupancyModel() {
    }
}