
package com.w3engineers.core.videon.data.local.document;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("doc_title")
    @Expose
    private String docTitle;
    @SerializedName("doc_cat_id")
    @Expose
    private String docCatId;
    @SerializedName("admin_id")
    @Expose
    private String adminId;
    @SerializedName("doc_img_url")
    @Expose
    private String docImgUrl;
    @SerializedName("doc_file_url")
    @Expose
    private String docFileUrl;
    @SerializedName("doc_author")
    @Expose
    private String docAuthor;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDocTitle() {
        return docTitle;
    }

    public void setDocTitle(String docTitle) {
        this.docTitle = docTitle;
    }

    public String getDocCatId() {
        return docCatId;
    }

    public void setDocCatId(String docCatId) {
        this.docCatId = docCatId;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

    public String getDocImgUrl() {
        return docImgUrl;
    }

    public void setDocImgUrl(String docImgUrl) {
        this.docImgUrl = docImgUrl;
    }

    public String getDocFileUrl() {
        return docFileUrl;
    }

    public void setDocFileUrl(String docFileUrl) {
        this.docFileUrl = docFileUrl;
    }

    public String getDocAuthor() {
        return docAuthor;
    }

    public void setDocAuthor(String docAuthor) {
        this.docAuthor = docAuthor;
    }

}
