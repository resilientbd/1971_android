
package com.shadowhite.archieve1971.data.local.adaptermodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Model {

    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("category_id")
    @Expose
    private Integer categoryId;
    @SerializedName("category_name")
    @Expose
    private String categoryName;
    @SerializedName("video_title")
    @Expose
    private String videoTitle;
    @SerializedName("video_type")
    @Expose
    private Integer videoType;
    @SerializedName("video_link")
    @Expose
    private String videoLink;
    @SerializedName("duration")
    @Expose
    private String duration;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("img")
    @Expose
    private String img;
    @SerializedName("created_at")
    @Expose
    private String createdAt;
    @SerializedName("featured")
    @Expose
    private Integer featured;
    @SerializedName("total_view")
    @Expose
    private Integer totalView;

    @SerializedName("message")
    String message;

    @SerializedName("status")
    int statusCode;

    @SerializedName("res")
    String resoulation;

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getVideoTitle() {
        return videoTitle;
    }

    public void setVideoTitle(String videoTitle) {
        this.videoTitle = videoTitle;
    }

    public Integer getVideoType() {
        return videoType;
    }

    public void setVideoType(Integer videoType) {
        this.videoType = videoType;
    }

    public String getVideoLink() {
        return videoLink;
    }

    public void setVideoLink(String videoLink) {
        this.videoLink = videoLink;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public Integer getFeatured() {
        return featured;
    }

    public void setFeatured(Integer featured) {
        this.featured = featured;
    }

    public Integer getTotalView() {
        return totalView;
    }

    public void setTotalView(Integer totalView) {
        this.totalView = totalView;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public String getResoulation() {
        return resoulation;
    }

    public void setResoulation(String resoulation) {
        this.resoulation = resoulation;
    }

    @Override
    public String toString() {
        return "Model{" +
                "id=" + id +
                ", categoryId=" + categoryId +
                ", categoryName='" + categoryName + '\'' +
                ", videoTitle='" + videoTitle + '\'' +
                ", videoType=" + videoType +
                ", videoLink='" + videoLink + '\'' +
                ", duration='" + duration + '\'' +
                ", description='" + description + '\'' +
                ", img='" + img + '\'' +
                ", createdAt='" + createdAt + '\'' +
                ", featured=" + featured +
                ", totalView=" + totalView +
                ", message='" + message + '\'' +
                ", statusCode=" + statusCode +
                ", resoulation='" + resoulation + '\'' +
                '}';
    }
}
