package com.w3engineers.core.videon.data.remote.myprofileedit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MyProfileModel {


    @SerializedName("username")
    private String userName;

    @SerializedName("password")
    @Expose
    private String oldPassword;

    @SerializedName("new_password")
    @Expose
    private String newPassword;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

}
