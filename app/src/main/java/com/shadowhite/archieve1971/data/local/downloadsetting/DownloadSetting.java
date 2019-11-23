package com.shadowhite.archieve1971.data.local.downloadsetting;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DownloadSetting {
    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("download_youtube")
    @Expose
    private String downloadYoutube;

    @SerializedName("download_vimeo")
    @Expose
    private String downloadVimeo;

    @SerializedName("download_uploaded_video")
    @Expose
    private String downloadUploadedVideo;

    @SerializedName("download_youku")
    @Expose
    private String downloadYouku;

    @SerializedName("download_linked_video")
    @Expose
    private String downloadLinkedVideo;

    @SerializedName("social_login_credentials")
    @Expose
    private String  socialLoginCredentialsFaceBook;

    @SerializedName("download_form_link")
    @Expose
    private String  downloadFormLink;

    public String getDownloadFormLink() {
        return downloadFormLink;
    }

    public void setDownloadFormLink(String downloadFormLink) {
        this.downloadFormLink = downloadFormLink;
    }

    public String getSocialLoginCredentialsFaceBook() {
        return socialLoginCredentialsFaceBook;
    }

    public void setSocialLoginCredentialsFaceBook(String socialLoginCredentialsFaceBook) {
        this.socialLoginCredentialsFaceBook = socialLoginCredentialsFaceBook;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDownloadYoutube() {
        return downloadYoutube;
    }

    public void setDownloadYoutube(String downloadYoutube) {
        this.downloadYoutube = downloadYoutube;
    }

    public String getDownloadVimeo() {
        return downloadVimeo;
    }

    public void setDownloadVimeo(String downloadVimeo) {
        this.downloadVimeo = downloadVimeo;
    }

    public String getDownloadUploadedVideo() {
        return downloadUploadedVideo;
    }

    public void setDownloadUploadedVideo(String downloadUploadedVideo) {
        this.downloadUploadedVideo = downloadUploadedVideo;
    }

    public String getDownloadYouku() {
        return downloadYouku;
    }

    public void setDownloadYouku(String downloadYouku) {
        this.downloadYouku = downloadYouku;
    }

    public String getDownloadLinkedVideo() {
        return downloadLinkedVideo;
    }

    public void setDownloadLinkedVideo(String downloadLinkedVideo) {
        this.downloadLinkedVideo = downloadLinkedVideo;
    }
}
