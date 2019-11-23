
package com.shadowhite.archieve1971.ui.videodetails;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.shadowhite.archieve1971.data.local.commondatalistresponse.Datum;

public class ViewCountResponse {

    @SerializedName("status")
    @Expose
    private Integer status;
    @SerializedName("message")
    @Expose
    private String message;
    @SerializedName("data")
    @Expose
    private Datum data;

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Datum getData() {
        return data;
    }

    public void setData(Datum data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ViewCountResponse{" +
                "status=" + status +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}
