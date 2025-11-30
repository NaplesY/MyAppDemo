package com.example.myappdemo.feed.viewholder;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.myappdemo.database.User;

public abstract class FeedViewHolder extends RecyclerView.ViewHolder {
    public FeedViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    public abstract void bindData(User user);
    public void startPlay() {}
    public void stopPlay() {}
}
