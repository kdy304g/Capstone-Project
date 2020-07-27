package com.example.randompostfromreddit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randompostfromreddit.R;
import com.example.randompostfromreddit.model.Child_Data;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.PostViewHolder> {
    private ArrayList<Child_Data> posts = new ArrayList<>();
    private final LayoutInflater inflater;
    private Context context;
    View view;
    PostViewHolder holder;


    public PostAdapter(Context context) {
        this.context = context;
        this.inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public PostAdapter.PostViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.post_item, parent, false);
        holder = new PostViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull PostAdapter.PostViewHolder holder, int position) {
        holder.post_text.setText(posts.get(position).getTitle());
        String image_url = posts.get(position).getThumbnail();
        if (image_url.isEmpty() || image_url.equals("self") || image_url.equals("default") || image_url.equals("spoiler")|| image_url.equals("nsfw")){
            holder.post_thumbnail.setImageResource(R.drawable.reddit_post_default);
        } else {
            Picasso.get().load(image_url).into((holder.post_thumbnail));
        }
    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public void setPosts(ArrayList<Child_Data> posts){
        this.posts = posts;
        notifyDataSetChanged();
    }

    class PostViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView post_text;
        ImageView post_thumbnail;
        public PostViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            post_text = (TextView) itemView.findViewById(R.id.post_text);
            post_thumbnail = (ImageView) itemView.findViewById(R.id.post_thumbnail);
        }
        @Override
        public void onClick(View v) {

        }
    }
}
