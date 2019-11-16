
package com.w3engineers.core.videon.data.local.imagecategories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("img_cat_id")
    @Expose
    private String imgCatId;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("admin_id")
    @Expose
    private String adminId;

    public String getImgCatId() {
        return imgCatId;
    }

    public void setImgCatId(String imgCatId) {
        this.imgCatId = imgCatId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

}
