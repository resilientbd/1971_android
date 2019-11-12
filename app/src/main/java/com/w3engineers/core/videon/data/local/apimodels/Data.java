
package com.w3engineers.core.videon.data.local.apimodels;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("banner_status")
    @Expose
    private String bannerStatus;
    @SerializedName("banner_id")
    @Expose
    private String bannerId;
    @SerializedName("banner_unit_id")
    @Expose
    private String bannerUnitId;
    @SerializedName("interstitial_status")
    @Expose
    private String interstitialStatus;
    @SerializedName("interstitial_id")
    @Expose
    private String interstitialId;
    @SerializedName("interstitial_unit_id")
    @Expose
    private String interstitialUnitId;
    @SerializedName("video_status")
    @Expose
    private String videoStatus;
    @SerializedName("video_id")
    @Expose
    private String videoId;
    @SerializedName("video_unit_id")
    @Expose
    private String videoUnitId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBannerStatus() {
        return bannerStatus;
    }

    public void setBannerStatus(String bannerStatus) {
        this.bannerStatus = bannerStatus;
    }

    public String getBannerId() {
        return bannerId;
    }

    public void setBannerId(String bannerId) {
        this.bannerId = bannerId;
    }

    public String getBannerUnitId() {
        return bannerUnitId;
    }

    public void setBannerUnitId(String bannerUnitId) {
        this.bannerUnitId = bannerUnitId;
    }

    public String getInterstitialStatus() {
        return interstitialStatus;
    }

    public void setInterstitialStatus(String interstitialStatus) {
        this.interstitialStatus = interstitialStatus;
    }

    public String getInterstitialId() {
        return interstitialId;
    }

    public void setInterstitialId(String interstitialId) {
        this.interstitialId = interstitialId;
    }

    public String getInterstitialUnitId() {
        return interstitialUnitId;
    }

    public void setInterstitialUnitId(String interstitialUnitId) {
        this.interstitialUnitId = interstitialUnitId;
    }

    public String getVideoStatus() {
        return videoStatus;
    }

    public void setVideoStatus(String videoStatus) {
        this.videoStatus = videoStatus;
    }

    public String getVideoId() {
        return videoId;
    }

    public void setVideoId(String videoId) {
        this.videoId = videoId;
    }

    public String getVideoUnitId() {
        return videoUnitId;
    }

    public void setVideoUnitId(String videoUnitId) {
        this.videoUnitId = videoUnitId;
    }

}
