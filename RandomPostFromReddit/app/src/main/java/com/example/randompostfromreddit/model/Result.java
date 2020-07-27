package com.example.randompostfromreddit.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("kind")
    @Expose
    private String kind;
    @SerializedName("data")
    @Expose
    private Result_Data resultData;

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }

    public Result_Data getResultData() {
        return resultData;
    }

    public void setResultData(Result_Data resultData) {
        this.resultData = resultData;
    }
}
