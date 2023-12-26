package com.moutamid.gardeningapp.models;

public class ServiceModel {
    String id, userID, name;
    double price;
    public ServiceModel() {
    }

    public ServiceModel(String id, String userID, String name, double price) {
        this.id = id;
        this.userID = userID;
        this.name = name;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}
