package com.example.randompostfromreddit;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.randompostfromreddit.model.Child;
import com.example.randompostfromreddit.model.Result;
import com.example.randompostfromreddit.network.RedditClient;
import com.example.randompostfromreddit.network.RedditService;
import com.example.randompostfromreddit.ui.MainActivity;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import retrofit2.Response;

public class CachePostWorker extends Worker {
    SharedPreferences pref;

    public CachePostWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        String permalink = "";
        Context applicationContext = MainActivity.getContextOfApplication();
        pref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.app_pref), Context.MODE_PRIVATE);
        try {
            permalink = getPermaLink();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            cachePostData(permalink);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(applicationContext.getString(R.string.data_cached),true);
        edit.commit();
        return Result.success();
    }

    public String getPermaLink() throws IOException {
        String post_url = "";
        Context applicationContext = MainActivity.getContextOfApplication();
        pref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.app_pref), Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(applicationContext.getString(R.string.liked),false);
        edit.commit();
        Set<String> subreddit_set = pref.getStringSet(applicationContext.getString(R.string.subreddit_set),new HashSet<String>());
        int i = 0;
        String chosen = "";
        for(String str : subreddit_set)
        {
            if (i == new Random().nextInt(subreddit_set.size()))
                chosen = str;
            i++;
        }
        RedditService service = RedditClient.getRedditService();
        retrofit2.Call<com.example.randompostfromreddit.model.Result> call = service.subredditPosts(chosen);
        Response<com.example.randompostfromreddit.model.Result> response = call.execute();
        if (response.body()!=null){
            ArrayList<Child> candidates = response.body().getResultData().getChildren();
            Child chosen_post = candidates.get(new Random().nextInt(candidates.size()));
            post_url = chosen_post.getData().getPermalink();
        }
        return post_url;
    }

    public void cachePostData(String permalink) throws IOException {
        Context applicationContext = MainActivity.getContextOfApplication();
        ArrayList<Child> comments = new ArrayList<>();
        pref = applicationContext.getSharedPreferences(applicationContext.getString(R.string.app_pref), Context.MODE_PRIVATE);
        RedditService service = RedditClient.getRedditService();
        retrofit2.Call<ArrayList<com.example.randompostfromreddit.model.Result>> call = service.random(permalink);
        Response<ArrayList<com.example.randompostfromreddit.model.Result>> response = call.execute();
        if(response.body() != null) {
            Child article = response.body().get(0).getResultData().getChildren().get(0);
            comments = response.body().get(1).getResultData().getChildren();
            SharedPreferences.Editor editor = pref.edit();
            Gson gson = new Gson();
            String post = gson.toJson(article.getData());
            String comment = gson.toJson(comments);
            editor.putString(applicationContext.getString(R.string.current_post), post);
            editor.putString(applicationContext.getString(R.string.comments),comment);
            editor.commit();
        }

    }

}
