package com.example.randompostfromreddit.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.randompostfromreddit.R;
import com.example.randompostfromreddit.model.Child;

import java.util.ArrayList;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.CommentViewHolder> {
    private ArrayList<Child> comments =new ArrayList<>();
    private final LayoutInflater inflater;
    private Context context;
    View view;
    CommentViewHolder holder;

    public CommentAdapter(Context context){
        this.context = context;
        inflater = LayoutInflater.from(context);
    }

    @NonNull
    @Override
    public CommentAdapter.CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        view = inflater.inflate(R.layout.comment_item, parent, false);
        holder = new CommentViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull CommentAdapter.CommentViewHolder holder, int position) {
        Child comment = comments.get(position);
        holder.comment_text.setText(comment.getData().getBody());
    }

    public void setComments(ArrayList<Child> comments){
        this.comments = comments;
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        if(comments != null){
            return comments.size();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        return super.getItemId(position);
    }

    class CommentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView comment_text;
        public CommentViewHolder(@NonNull View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            comment_text = (TextView) itemView.findViewById(R.id.comment_text);
        }

        @Override
        public void onClick(View v) {

        }
    }
}
