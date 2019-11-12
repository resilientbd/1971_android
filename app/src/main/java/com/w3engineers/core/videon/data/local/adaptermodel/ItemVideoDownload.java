package com.w3engineers.core.videon.data.local.adaptermodel;

import at.huber.youtubeExtractor.YtFile;

public class ItemVideoDownload {
    private String title;
   private String format;
    private String url;
    private int type;
    private String vimoTitle;
    private String duration;

    public ItemVideoDownload() {
    }



    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getVimoTitle() {
        return vimoTitle;
    }

    public void setVimoTitle(String vimoTitle) {
        this.vimoTitle = vimoTitle;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    @Override
    public String toString() {
        return "ItemVideoDownload{" +
                "title='" + title + '\'' +
                ", format='" + format + '\'' +
                ", url='" + url + '\'' +
                ", type=" + type +
                '}';
    }
}
