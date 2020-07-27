package com.example.randompostfromreddit.model;

import androidx.annotation.Nullable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child_Data {

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("public_description")
    @Expose
    private String description;

    @SerializedName("body")
    @Expose
    private String body;

    @SerializedName("thumbnail")
    @Expose
    private String thumbnail;

    @SerializedName("url")
    @Expose
    private String url;

    @SerializedName("permalink")
    @Expose
    private String permalink;

    @SerializedName("subreddit")
    @Expose
    private String subreddit;


    public String getTitle() {
        return title;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(){
        this.description = description;
    }

    public String getBody() { return body; }

    public void setBody() { this.body = body; }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getPermalink() {
        return permalink;
    }

    public void setPermalink(String permalink) {
        this.permalink = permalink;
    }


    public String getSubreddit() {
        return subreddit;
    }

    public void setSubreddit(String subreddit) {
        this.subreddit = subreddit;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == this) return true;

        if (!(obj instanceof Child_Data)) {
            return false;
        }

        Child_Data child_data = (Child_Data) obj;

        return child_data.title.equals(title);
    }

    @Override
    public int hashCode() {
        return title.hashCode();
    }
}
