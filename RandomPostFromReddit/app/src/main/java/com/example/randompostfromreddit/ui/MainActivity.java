package com.example.randompostfromreddit.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.randompostfromreddit.R;
import com.example.randompostfromreddit.RedditAppWidgetProvider;
import com.example.randompostfromreddit.adapter.CommentAdapter;
import com.example.randompostfromreddit.model.Child;
import com.example.randompostfromreddit.model.Child_Data;
import com.example.randompostfromreddit.model.Result;
import com.example.randompostfromreddit.network.RedditClient;
import com.example.randompostfromreddit.network.RedditService;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.initialization.OnInitializationCompleteListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class MainActivity extends AppCompatActivity {

    private static SharedPreferences pref;
    RecyclerView recyclerView;
    CommentAdapter adapter;
    private ArrayList<Child> comments = new ArrayList<>();

    private static String OAUTH_BASE_URL;
    private static String CLIENT_ID;
    private static String RESPONSE_TYPE;
    private static String REDIRECT_URI;
    private static String RANDOM_STRING;
    private static String SCOPE;
    private static String ACCESS_TOKEN_URL;
    private static String DEVICE_ID = UUID.randomUUID().toString();
    public static Context contextOfApplication;

    private DrawerLayout dl;
    private NavigationView nv;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        pref = getSharedPreferences(getString(R.string.app_pref), Context.MODE_PRIVATE);
        contextOfApplication = getApplicationContext();
        OAUTH_BASE_URL  = getString(R.string.oauth_base_url);
        CLIENT_ID = getString(R.string.client_id);
        RESPONSE_TYPE = getString(R.string.code);
        REDIRECT_URI = getString(R.string.redirect_uri);
        RANDOM_STRING = getString(R.string.random_string);
        SCOPE = getString(R.string.scope);
        ACCESS_TOKEN_URL = getString(R.string.access_token_url);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.main_activity_title);
        actionbar.setHomeAsUpIndicator(R.drawable.baseline_menu_white_18dp);
        actionbar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.comment_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CommentAdapter(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

        dl = (DrawerLayout)findViewById(R.id.activity_main_drawer);

        nv = (NavigationView)findViewById(R.id.nv);
        nv.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                switch(id)
                {
                    case R.id.popular_subreddit:
                        Intent i2 = new Intent(MainActivity.this, SubredditActivity.class);
                        MainActivity.this.startActivity(i2);
                        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                        dl.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.my_subreddit:
                        Intent i3 = new Intent(MainActivity.this, MySubredditActivity.class);
                        MainActivity.this.startActivity(i3);
                        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                        dl.closeDrawer(GravityCompat.START);
                        break;
                    case R.id.my_post:
                        Intent i4 = new Intent(MainActivity.this, PostActivity.class);
                        MainActivity.this.startActivity(i4);
                        overridePendingTransition(R.anim.right_slide_in, R.anim.left_slide_out);
                        dl.closeDrawer(GravityCompat.START);
                        break;
                    default:
                        return true;
                }
                return true;
            }
        });

        if(!pref.getBoolean(getString(R.string.logged_in), false)){
            startSignIn();
        }
        String linkFromWidget = getIntent().getStringExtra(getString(R.string.permalink));
        if(linkFromWidget != null){
            populatePost(linkFromWidget);
        } else{
            getNewPost();
        }

        MobileAds.initialize(this, new OnInitializationCompleteListener() {
            @Override
            public void onInitializationComplete(InitializationStatus initializationStatus) {
            }
        });
        mAdView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getIntent()!=null && getIntent().getAction() != null && getIntent().getAction().equals(Intent.ACTION_VIEW) && !pref.getBoolean(getString(R.string.app_start), false)) {
            Uri uri = getIntent().getData();
            if(uri.getQueryParameter(getString(R.string.error)) != null) {
                java.lang.String error = uri.getQueryParameter(getString(R.string.error));
                Log.e("", getString(R.string.err_occurred) + error);
            } else {
                java.lang.String state = uri.getQueryParameter(getString(R.string.state));
                if(state.equals(RANDOM_STRING)) {
                    String code = uri.getQueryParameter(getString(R.string.code));
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(getString(R.string.code),code);
                    edit.apply();
                    getAccessToken(code);
                }
            }
        }
    }

    public static Context getContextOfApplication(){
        return contextOfApplication;
    }

    public void populatePost(String permalink){
        final TextView title = findViewById(R.id.post_title);
        final ImageView post_image = findViewById(R.id.post_image);
        final TextView url = findViewById(R.id.post_url);
        final TextView subreddit_name = findViewById(R.id.post_subreddit);

        RedditService service = RedditClient.getRedditService();
        retrofit2.Call<ArrayList<Result>> call1 = service.random(permalink);
        call1.enqueue(new retrofit2.Callback<ArrayList<Result>>() {
            @Override
            public void onResponse(retrofit2.Call<ArrayList<Result>> call, retrofit2.Response<ArrayList<Result>> response) {
                if(response.body() != null){
                    Child article = response.body().get(0).getResultData().getChildren().get(0);
                    SharedPreferences.Editor editor = pref.edit();
                    Gson gson = new Gson();
                    String post = gson.toJson(article.getData());
                    editor.putString(getString(R.string.current_post),post);
                    editor.commit();
                    title.setText(article.getData().getTitle());
                    url.setText(article.getData().getUrl());
                    subreddit_name.setText(getString(R.string.subreddit)+article.getData().getSubreddit());
                    String image_url = article.getData().getThumbnail();
                    if (image_url.isEmpty() || image_url.equals(getString(R.string.self)) || image_url.equals(getString(R.string.default_)) || image_url.equals(getString(R.string.spoiler))|| image_url.equals(getString(R.string.nsfw))){
                        post_image.setVisibility(View.GONE);
                    }else{
                        post_image.setVisibility((View.VISIBLE));
                        Picasso.get().load(image_url).placeholder(R.drawable.reddit_default).into(post_image);
                    }
                    comments = response.body().get(1).getResultData().getChildren();
                    adapter.setComments(comments);
                }
                updateAllWidgets();
            }
            @Override
            public void onFailure(retrofit2.Call<ArrayList<Result>> call, Throwable t) {
            }
        });
    }

    public void getNewPost(){
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(getString(R.string.liked),false);
        edit.commit();
        Set<String> subreddit_set = pref.getStringSet(getString(R.string.subreddit_set),new HashSet<String>());
        int i = 0;
        String chosen = "";
        for(String str : subreddit_set)
        {
            if (i == new Random().nextInt(subreddit_set.size()))
                chosen = str;
            i++;
        }
        RedditService service1 = RedditClient.getRedditService();
        retrofit2.Call<Result> call2 = service1.subredditPosts(chosen);
        call2.enqueue(new retrofit2.Callback<Result>() {
            @Override
            public void onResponse(retrofit2.Call<Result> call, retrofit2.Response<Result> response) {
                if (response.body()!=null){
                    ArrayList<Child> candidates = response.body().getResultData().getChildren();
                    Child chosen_post = candidates.get(new Random().nextInt(candidates.size()));
                    String post_url = chosen_post.getData().getPermalink();
                    populatePost(post_url);
                }
            }
            @Override
            public void onFailure(retrofit2.Call<Result> call, Throwable t) {
                Log.d(getString(R.string.error),t.getMessage());
            }
        });
    }

    public void writePostFirebase(){
        String post = pref.getString(getString(R.string.current_post),"");
        String code = pref.getString(getString(R.string.code),"");
        Gson gson = new Gson();
        Child_Data data = gson.fromJson(post, Child_Data.class);
        if(!pref.getBoolean(getString(R.string.liked),false)){
            FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
            DatabaseReference mDatabaseReference = mDatabase.getReference().child(getString(R.string.users)).child(code).push();
            mDatabaseReference.setValue(data);
        }
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(getString(R.string.liked),true);
        edit.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();
        switch(itemId) {
            case android.R.id.home:
                dl.openDrawer(GravityCompat.START);
                return true;
            case R.id.get_newpost:
                getNewPost();
                return true;
            case R.id.like_post:
                writePostFirebase();
                Toast.makeText(getApplicationContext(), R.string.post_added, Toast.LENGTH_SHORT).show();
                return true;
        }
        return true;
    }

    public void startSignIn() {
        String url = OAUTH_BASE_URL + getString(R.string.url_1) + CLIENT_ID + getString(R.string.url_2) + RESPONSE_TYPE +
                getString(R.string.url_3) + RANDOM_STRING + getString(R.string.url_4) + REDIRECT_URI + getString(R.string.url_5) + SCOPE;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
        startActivity(intent);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean(getString(R.string.logged_in),true);
        edit.apply();
    }

    public void getAccessToken(String code){
        OkHttpClient client = new OkHttpClient();
        String authString = CLIENT_ID + ":";
        String encodedAuthString = Base64.encodeToString(authString.getBytes(), Base64.NO_WRAP);
        Request request = new Request.Builder()
                .addHeader(getString(R.string.user_agent), getString(R.string.app_name))
                .addHeader(getString(R.string.authorization), getString(R.string.basic) + encodedAuthString)
                .url(ACCESS_TOKEN_URL)
                .post(RequestBody.create(MediaType.parse(getString(R.string.url_encoded)),
                        getString(R.string.rq_body_1) + code + getString(R.string.rq_body_2) + DEVICE_ID +
                                getString(R.string.rq_body_3) + REDIRECT_URI))
                .build();

        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                Log.e("", getString(R.string.error) + e);
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                String json = response.body().string();
                JSONObject data = null;
                try {
                    data = new JSONObject(json);
                    String token = data.optString(getString(R.string.access_token));
                    SharedPreferences.Editor edit = pref.edit();
                    edit.putString(getString(R.string.token), token);
                    edit.putBoolean(getString(R.string.app_start),true);
                    edit.commit();
                    getNewPost();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private void updateAllWidgets(){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getApplicationContext());
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, RedditAppWidgetProvider.class));
        if (appWidgetIds.length > 0) {
            new RedditAppWidgetProvider().onUpdate(this, appWidgetManager, appWidgetIds);
        }
    }
}