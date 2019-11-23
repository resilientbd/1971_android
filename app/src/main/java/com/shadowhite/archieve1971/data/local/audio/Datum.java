
package com.shadowhite.archieve1971.data.local.audio;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("audio_title")
    @Expose
    private String audioTitle;
    @SerializedName("audio_url")
    @Expose
    private String audioUrl;
    @SerializedName("audio_cat_id")
    @Expose
    private String audioCatId;
    @SerializedName("audio_img")
    @Expose
    private String audioImg;
    @SerializedName("audio_description")
    @Expose
    private String audioDescription;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAudioTitle() {
        return audioTitle;
    }

    public void setAudioTitle(String audioTitle) {
        this.audioTitle = audioTitle;
    }

    public String getAudioUrl() {
        return audioUrl;
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
    }

    public String getAudioCatId() {
        return audioCatId;
    }

    public void setAudioCatId(String audioCatId) {
        this.audioCatId = audioCatId;
    }

    public String getAudioImg() {
        return audioImg;
    }

    public void setAudioImg(String audioImg) {
        this.audioImg = audioImg;
    }

    public String getAudioDescription() {
        return audioDescription;
    }

    public void setAudioDescription(String audioDescription) {
        this.audioDescription = audioDescription;
    }

}
