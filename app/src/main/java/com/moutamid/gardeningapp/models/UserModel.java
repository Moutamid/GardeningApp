package com.moutamid.gardeningapp.models;

import java.util.ArrayList;

public class UserModel {
    String id, name, address, email, password;
    double latitude, longitude;
    boolean isGardener;
    String image;

    ArrayList<FeedbackModel> list;

    public UserModel() {
    }

    public UserModel(String id, String name, String address, String email, String password, double latitude, double longitude, boolean isGardener, String image, ArrayList<FeedbackModel> list) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.email = email;
        this.password = password;
        this.latitude = latitude;
        this.longitude = longitude;
        this.isGardener = isGardener;
        this.image = image;
        this.list = list;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public boolean isGardener() {
        return isGardener;
    }

    public void setGardener(boolean gardener) {
        isGardener = gardener;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public ArrayList<FeedbackModel> getList() {
        return list;
    }

    public void setList(ArrayList<FeedbackModel> list) {
        this.list = list;
    }
}
