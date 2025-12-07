package com.example.myappdemo.feed.adapter.viewholder;

import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myappdemo.R;
import com.example.myappdemo.data.User;

public class LoadingFeedViewHolder extends FeedViewHolder{
    private final ProgressBar progressBar;
    private final TextView textView;

    public LoadingFeedViewHolder(@NonNull View itemView) {
        super(itemView);
        progressBar = itemView.findViewById(R.id.progressBar);
        textView = itemView.findViewById(R.id.textView3);
    }

    @Override
    public void bindData(User user) {

    }
}
