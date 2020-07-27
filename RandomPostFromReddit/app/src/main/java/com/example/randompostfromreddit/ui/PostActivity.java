package com.example.randompostfromreddit.ui;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randompostfromreddit.R;
import com.example.randompostfromreddit.util.RecyclerItemClickListener;
import com.example.randompostfromreddit.adapter.PostAdapter;
import com.example.randompostfromreddit.model.Child_Data;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class PostActivity extends AppCompatActivity {
    private ArrayList<Child_Data> postArray;
    RecyclerView recyclerView;
    PostAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        SharedPreferences pref = getSharedPreferences(getString(R.string.app_pref), Context.MODE_PRIVATE);
        final String code = pref.getString(getString(R.string.code),"");

        Toolbar postToolbar = (Toolbar) findViewById(R.id.post_toolbar);
        setSupportActionBar(postToolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle(R.string.post_activity_title);
        actionbar.setHomeAsUpIndicator(R.drawable.baseline_keyboard_backspace_white_18dp);
        actionbar.setDisplayHomeAsUpEnabled(true);

        recyclerView = (RecyclerView) findViewById(R.id.post_rv);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PostAdapter(this);
        recyclerView.setAdapter(adapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                new LinearLayoutManager(this).getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, recyclerView ,new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(postArray.get(position).getUrl()));
                        startActivity(intent);
                    }
                    @Override public void onLongItemClick(View view, int position) {
                        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                        Query titleQuery = ref.child(getString(R.string.users)).child(code).orderByChild(getString(R.string.title)).equalTo(postArray.get(position).getTitle());
                        titleQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot snap: snapshot.getChildren()) {
                                    snap.getRef().removeValue();
                                }
                            }
                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }
                        });
                        Toast.makeText(PostActivity.this, R.string.delete_post,Toast.LENGTH_SHORT).show();
                    }
                })
        );

        FirebaseDatabase mDatabase = FirebaseDatabase.getInstance();
        DatabaseReference mDatabaseReference = mDatabase.getReference().child(getString(R.string.users)).child(code);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                postArray = new ArrayList<>();
                for (DataSnapshot child: dataSnapshot.getChildren()){
                    Child_Data post = child.getValue(Child_Data.class);
                    if(post != null){
                        postArray.add(post);
                    }
                }
                Set<Child_Data> postSet = new HashSet<>(postArray);
                postArray.clear();
                postArray.addAll(postSet);
                adapter.setPosts(postArray);
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(getString(R.string.post_cancel), getString(R.string.post_cancel_msg), databaseError.toException());
            }
        };
        mDatabaseReference.addValueEventListener(postListener);
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
