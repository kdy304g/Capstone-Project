package com.example.randompostfromreddit.network;

import com.example.randompostfromreddit.model.Result;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Url;

public interface RedditService {

    @GET("subreddits/popular")
    Call<Result> subreddits();

    @GET
    Call<ArrayList<Result>> random(@Url String permalink);

    @GET
    Call<Result> subredditPosts(@Url String subredditLink);
}
