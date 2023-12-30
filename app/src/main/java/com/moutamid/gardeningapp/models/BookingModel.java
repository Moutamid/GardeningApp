package com.moutamid.gardeningapp.models;

public class BookingModel {
    String ID, senderID;
    ServiceModel serviceModel;
    long bookingDay, startDate, endDate;
    boolean isAccepted;
    public BookingModel() {
    }

    public BookingModel(String ID, String senderID, ServiceModel serviceModel, long bookingDay, long startDate, long endDate, boolean isAccepted) {
        this.ID = ID;
        this.senderID = senderID;
        this.serviceModel = serviceModel;
        this.bookingDay = bookingDay;
        this.startDate = startDate;
        this.endDate = endDate;
        this.isAccepted = isAccepted;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public ServiceModel getServiceModel() {
        return serviceModel;
    }

    public void setServiceModel(ServiceModel serviceModel) {
        this.serviceModel = serviceModel;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public boolean isAccepted() {
        return isAccepted;
    }

    public void setAccepted(boolean accepted) {
        isAccepted = accepted;
    }

    public long getBookingDay() {
        return bookingDay;
    }

    public void setBookingDay(long bookingDay) {
        this.bookingDay = bookingDay;
    }
}
