package com.moutamid.gardeningapp.models;

public class FeedbackModel {
    String senderID, receiverID, name, desc;

    public FeedbackModel() {
    }

    public FeedbackModel(String senderID, String receiverID, String name, String desc) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.name = name;
        this.desc = desc;
    }

    public FeedbackModel(String name, String desc) {
        this.name = name;
        this.desc = desc;
    }

    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }
}
