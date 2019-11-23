
package com.shadowhite.archieve1971.data.local.images;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("img_title")
    @Expose
    private String imgTitle;
    @SerializedName("img_url")
    @Expose
    private String imgUrl;
    @SerializedName("admin_id")
    @Expose
    private String adminId;
    @SerializedName("img_desc")
    @Expose
    private String imgDesc;
    @SerializedName("img_cat_id")
    @Expose
    private String imgCatId;

    private String imageResolution="1200:600";

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImgTitle() {
        return imgTitle;
    }

    public void setImgTitle(String imgTitle) {
        this.imgTitle = imgTitle;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public void setImgUrl(String imgUrl) {
        this.imgUrl = imgUrl;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getImgDesc() {
        return imgDesc;
    }

    public void setImgDesc(String imgDesc) {
        this.imgDesc = imgDesc;
    }

    public String getImgCatId() {
        return imgCatId;
    }

    public void setImgCatId(String imgCatId) {
        this.imgCatId = imgCatId;
    }

    public String getImageResolution() {
        return imageResolution;
    }

    public void setImageResolution(String imageResolution) {
        this.imageResolution = imageResolution;
    }
}
