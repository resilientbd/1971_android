
package com.shadowhite.archieve1971.data.local.documentcategories;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Datum {

    @SerializedName("doc_id")
    @Expose
    private String docId;
    @SerializedName("doc_cat_title")
    @Expose
    private String docCatTitle;
    @SerializedName("admin_id")
    @Expose
    private String adminId;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getDocCatTitle() {
        return docCatTitle;
    }

    public void setDocCatTitle(String docCatTitle) {
        this.docCatTitle = docCatTitle;
    }

    public String getAdminId() {
        return adminId;
    }

    public void setAdminId(String adminId) {
        this.adminId = adminId;
    }

}
