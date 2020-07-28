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

import com.example.randompostfromreddit.ui.MainActivity;
import com.example.randompostfromreddit.R;
import com.example.randompostfromreddit.model.Child;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SubredditAdapter extends RecyclerView.Adapter<SubredditAdapter.MyViewHolder> {
    private ArrayList<Child> children =new ArrayList<>();
    private final LayoutInflater inflater;
    View view;
    MyViewHolder holder;
    private Context context;

    public SubredditAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public SubredditAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.subreddit_item, parent, false);
        holder = new MyViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final SubredditAdapter.MyViewHolder holder, final int position) {
        holder.subreddit_text.setText(children.get(position).getData().getTitle());
        holder.small_subreddit_text.setText(children.get(position).getData().getDescription());
        holder.subscribe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context applicationContext = MainActivity.getContextOfApplication();
                SharedPreferences pref = applicationContext.getSharedPreferences(context.getString(R.string.app_pref), Context.MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                Set<String> set = pref.getStringSet(context.getString(R.string.subreddit_set),new HashSet<String>());
                Set<String> newStrSet = new HashSet<String>();
                newStrSet.add(children.get(position).getData().getUrl());
                newStrSet.addAll(set);
                edit.putStringSet(context.getString(R.string.subreddit_set),newStrSet);
                edit.commit();
                Toast.makeText(context,context.getString(R.string.subsribe)+" "+ children.get(position).getData().getTitle()+context.getString(R.string.exclamation),Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void setChildren(ArrayList<Child> children){
        this.children = children;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if (children != null){
            return children.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView subreddit_text,small_subreddit_text;
        Button subscribe;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            subreddit_text = (TextView) itemView.findViewById(R.id.subreddit_text);
            small_subreddit_text = (TextView) itemView.findViewById(R.id.smallsubreddit_text);
            subscribe = (Button) itemView.findViewById(R.id.subreddit_subscribe);
        }
        @Override
        public void onClick(View v) {

        }
    }
}
