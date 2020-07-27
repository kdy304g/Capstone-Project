package com.example.randompostfromreddit.ui;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randompostfromreddit.R;
import com.example.randompostfromreddit.adapter.SubredditAdapter;
import com.example.randompostfromreddit.model.Child;
import com.example.randompostfromreddit.model.Result;
import com.example.randompostfromreddit.network.RedditClient;
import com.example.randompostfromreddit.network.RedditService;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SubredditActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    SubredditAdapter adapter;
    private ArrayList<Child> children = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);

        Toolbar subToolbar = (Toolbar) findViewById(R.id.sub_toolbar);
        setSupportActionBar(subToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.popular_activity_title);
        actionbar.setHomeAsUpIndicator(R.drawable.baseline_keyboard_backspace_white_18dp);
        actionbar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.subreddit_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new SubredditAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        RedditService service = RedditClient.getRedditService();

        Call<Result> call = service.subreddits();
        call.enqueue(new Callback<Result>() {
            @Override
            public void onResponse(Call<Result> call, Response<Result> response) {
                children = response.body().getResultData().getChildren();
                adapter.setChildren(children);
            }
            @Override
            public void onFailure(Call<Result> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            finish();
            return true;
        }
        return true;
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.left_slide_in, R.anim.right_slide_out);
    }
}
