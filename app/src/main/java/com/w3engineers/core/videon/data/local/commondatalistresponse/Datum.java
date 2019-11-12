package com.w3engineers.core.videon.data.local.commondatalistresponse;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("category")
    @Expose
    private String category;
    @SerializedName("admin_id")
    @Expose
    private String adminId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("image_name")
    @Expose
    private String imageName;
    @SerializedName("link")
    @Expose
    private String link;
    @SerializedName("image_resolution")
    @Expose
    private String imageResolution;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("type")
    @Expose
    private int type;
    @SerializedName("uploaded_video")
    @Expose
    private String uploadedVideo;
    @SerializedName("youtube")
    @Expose
    private String youtube;
    @SerializedName("vimeo")
    @Expose
    private String vimeo;

    @SerializedName("video_link")
    @Expose
    private String videoLink;

    @SerializedName("m3u8")
    @Expose
    private String liveLinkm3u8;
    @SerializedName("rtmp")
    @Expose
    private String liveRtmp;

    @SerializedName("view_count")
    @Expose
    private String viewCount;
    @SerializedName("duration")
    @Expose
    private String duration;

    @SerializedName("created")
    @Expose
    private long created;

    @SerializedName("username")
    private String userName;


    public String getLiveLinkm3u8() {
        return liveLinkm3u8;
    }

    public void setLiveLinkm3u8(String liveLinkm3u8) {
        this.liveLinkm3u8 = liveLinkm3u8;
    }
    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public long getCreated() {
        return created;
    }

    public void setCreated(long created) {
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getImageResolution() {
        return imageResolution;
    }

    public void setImageResolution(String imageResolution) {
        this.imageResolution = imageResolution;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUploadedVideo() {
        return uploadedVideo;
    }

    public void setUploadedVideo(String uploadedVideo) {
        this.uploadedVideo = uploadedVideo;
    }

    public String getYoutube() {
        return youtube;
    }

    public void setYoutube(String youtube) {
        this.youtube = youtube;
    }

    public String getVimeo() {
        return vimeo;
    }

    public void setVimeo(String vimeo) {
        this.vimeo = vimeo;
    }

    public String getViewCount() {
        return viewCount;
    }

    public void setViewCount(String viewCount) {
        this.viewCount = viewCount;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getLiveRtmp() {
        return liveRtmp;
    }

    public void setLiveRtmp(String liveRtmp) {
        this.liveRtmp = liveRtmp;
    }

    @Override
    public String toString() {
        return "Datum{" +
                "id='" + id + '\'' +
                ", category='" + category + '\'' +
                ", adminId='" + adminId + '\'' +
                ", status='" + status + '\'' +
                ", title='" + title + '\'' +
                ", imageName='" + imageName + '\'' +
                ", link='" + link + '\'' +
                ", imageResolution='" + imageResolution + '\'' +
                ", description='" + description + '\'' +
                ", type=" + type +
                ", uploadedVideo='" + uploadedVideo + '\'' +
                ", youtube='" + youtube + '\'' +
                ", vimeo='" + vimeo + '\'' +
                ", videoLink='" + videoLink + '\'' +
                ", liveLinkm3u8='" + liveLinkm3u8 + '\'' +
                ", liveRtmp='" + liveRtmp + '\'' +
                ", viewCount='" + viewCount + '\'' +
                ", duration='" + duration + '\'' +
                ", created=" + created +
                ", userName='" + userName + '\'' +
                '}';
    }
}