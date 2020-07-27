package com.example.randompostfromreddit.adapter;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randompostfromreddit.R;
import com.example.randompostfromreddit.ui.MainActivity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class MySubredditAdapter extends RecyclerView.Adapter<MySubredditAdapter.SubredditViewHolder> {
    private Context context;
    private final LayoutInflater inflater;
    ArrayList<String> mysubreddits;
    SharedPreferences pref;
    View view;
    SubredditViewHolder holder;

    public MySubredditAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
        Context applicationContext = MainActivity.getContextOfApplication();
        pref = applicationContext.getSharedPreferences(context.getString(R.string.app_pref), Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public SubredditViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.mysubreddit_item, parent, false);
        holder = new SubredditViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull SubredditViewHolder holder, final int position) {
        final String subreddit = mysubreddits.get(position);
        String subredditTrimmed = subreddit.substring(3,subreddit.length()-1);
        final String subredditCapitalized = subredditTrimmed.substring(0, 1).toUpperCase() + subredditTrimmed.substring(1);
        holder.mysubreddit_text.setText(subredditCapitalized);
        holder.unsubscribe.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(context,context.getString(R.string.unsubscribe)+" "+ subredditCapitalized +context.getString(R.string.exclamation),Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor edit1 = pref.edit();
                Set<String> set = pref.getStringSet(context.getString(R.string.subreddit_set),null);
                Set<String> newStrSet = new HashSet<String>();
                newStrSet.addAll(set);
                newStrSet.remove(subreddit);
                edit1.putStringSet(context.getString(R.string.subreddit_set),newStrSet);
                edit1.commit();
                mysubreddits = new ArrayList<String>();
                for (String str : pref.getStringSet(context.getString(R.string.subreddit_set),null))
                    mysubreddits.add(str);
                notifyDataSetChanged();
            }
        });
    }

    @Override
    public int getItemCount() {
        return pref.getStringSet(context.getString(R.string.subreddit_set),new HashSet<String>()).size();
    }

    public void setMysubreddits(ArrayList<String> mysubreddits){
        this.mysubreddits = mysubreddits;
        notifyDataSetChanged();
    }

    class SubredditViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView mysubreddit_text;
        Button unsubscribe;
        public SubredditViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            mysubreddit_text = (TextView) itemView.findViewById(R.id.mysubreddit_text);
            unsubscribe = (Button) itemView.findViewById(R.id.subreddit_unsubscribe);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
