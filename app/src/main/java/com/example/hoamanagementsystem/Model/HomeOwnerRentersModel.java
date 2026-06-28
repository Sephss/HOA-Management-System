package com.example.hoamanagementsystem.Model;

public class HomeOwnerRentersModel {
    String firstName, middleName, lastName, phoneNumber, email, block, lot, street, role, lavanyaPhaseType, uid, imageUrl;
    public HomeOwnerRentersModel() {

    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBlock() {
        return block;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public String getLot() {
        return lot;
    }

    public void setLot(String lot) {
        this.lot = lot;
    }

    public String getStreet() {
        return street;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getLavanyaPhaseType() {
        return lavanyaPhaseType;
    }

    public void setLavanyaPhaseType(String lavanyaPhaseType) {
        this.lavanyaPhaseType = lavanyaPhaseType;
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public HomeOwnerRentersModel(String firstName, String middleName, String lastName, String phoneNumber, String email, String block, String lot, String street, String role, String lavanyaPhaseType, String uid, String imageUrl) {
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.block = block;
        this.lot = lot;
        this.street = street;
        this.role = role;
        this.lavanyaPhaseType = lavanyaPhaseType;
        this.uid = uid;
        this.imageUrl = imageUrl;
    }
}
