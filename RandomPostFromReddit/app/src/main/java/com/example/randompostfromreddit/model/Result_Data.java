package com.example.randompostfromreddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Result_Data {
    @SerializedName("modhash")
    @Expose
    private String modhash;
    @SerializedName("dist")
    @Expose
    private Integer dist;
    @SerializedName("children")
    @Expose
    private ArrayList<Child> children = null;

    public String getModhash() {
        return modhash;
    }

    public void setModhash(String modhash) {
        this.modhash = modhash;
    }

    public Integer getDist() {
        return dist;
    }

    public void setDist(Integer dist) {
        this.dist = dist;
    }

    public ArrayList<Child> getChildren() {
        return children;
    }

    public void setChildren(ArrayList<Child> children) {
        this.children = children;
    }
}
