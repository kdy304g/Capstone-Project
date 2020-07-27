package com.example.randompostfromreddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Child {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("data")
    @Expose
    private Child_Data data;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Child_Data getData() {
        return data;
    }

    public void setData(Child_Data data) {
        this.data = data;
    }
}
