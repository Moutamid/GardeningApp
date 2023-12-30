package com.moutamid.gardeningapp.models;

public class GardenerModel {
    UserModel userModel;
    ServiceModel serviceModel;

    public GardenerModel(UserModel userModel, ServiceModel serviceModel) {
        this.userModel = userModel;
        this.serviceModel = serviceModel;
    }

    public UserModel getUserModel() {
        return userModel;
    }

    public void setUserModel(UserModel userModel) {
        this.userModel = userModel;
    }

    public ServiceModel getServiceModel() {
        return serviceModel;
    }

    public void setServiceModel(ServiceModel serviceModel) {
        this.serviceModel = serviceModel;
    }
}
