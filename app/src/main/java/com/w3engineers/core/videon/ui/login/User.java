package com.w3engineers.core.videon.ui.login;

public class User {

    /**
     * Constants
     */
    public static final int TYPE_EMAIL = 1;
    public static final int TYPE_GOOGLE = 2;
    public static final int TYPE_FACEBOOK = 3;

    /**
     * Fields
     */
    private String mName, mEmail, mPassword, mSocialNetworkId;
    private int mType, mUserId;

    public User() {
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        mName = name;
    }

    public int getUserId() {
        return mUserId;
    }

    public void setUserId(int userId) {
        mUserId = userId;
    }

    public String getEmail() {
        return mEmail;
    }

    public void setEmail(String email) {
        mEmail = email;
    }

    public String getPassword() {
        return mPassword;
    }

    public void setPassword(String password) {
        mPassword = password;
    }

    public int getType() {
        return mType;
    }

    public void setType(int type) {
        mType = type;
    }

    public String getSocialNetworkId() {
        return mSocialNetworkId;
    }

    public void setSocialNetworkId(String socialNetworkId) {
        mSocialNetworkId = socialNetworkId;
    }
}
