
package com.shadowhite.archieve1971.data.local.userstatus;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

    @SerializedName("review")
    @Expose
    private Integer review;
    @SerializedName("favourite")
    @Expose
    private Integer favourite;
    @SerializedName("playlist")
    @Expose
    private Integer playlist;
    @SerializedName("avg_rating")
    @Expose
    private Double avgRating;
    @SerializedName("total_users")
    @Expose
    private Integer totalUsers;

    public Integer getReview() {
        return review;
    }

    public void setReview(Integer review) {
        this.review = review;
    }

    public Integer getFavourite() {
        return favourite;
    }

    public void setFavourite(Integer favourite) {
        this.favourite = favourite;
    }

    public Integer getPlaylist() {
        return playlist;
    }

    public void setPlaylist(Integer playlist) {
        this.playlist = playlist;
    }

    public Double getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(Double avgRating) {
        this.avgRating = avgRating;
    }

    public Integer getTotalUsers() {
        return totalUsers;
    }

    public void setTotalUsers(Integer totalUsers) {
        this.totalUsers = totalUsers;
    }

}
