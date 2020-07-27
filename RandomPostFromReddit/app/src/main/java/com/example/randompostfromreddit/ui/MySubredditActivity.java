package com.example.randompostfromreddit.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
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
import com.example.randompostfromreddit.adapter.MySubredditAdapter;

import java.util.ArrayList;
import java.util.HashSet;

public class MySubredditActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    MySubredditAdapter adapter;
    private ArrayList<String> children = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mysub);

        Toolbar mySubToolbar = (Toolbar) findViewById(R.id.mysub_toolbar);
        setSupportActionBar(mySubToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("My Subreddit");
        actionbar.setHomeAsUpIndicator(R.drawable.baseline_keyboard_backspace_white_18dp);
        actionbar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.mysub_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MySubredditAdapter(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        SharedPreferences pref = getSharedPreferences("AppPref", Context.MODE_PRIVATE);
        for (String str: pref.getStringSet("subreddit_set",new HashSet<String>()))
            children.add(str);
        adapter.setMysubreddits(children);
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
