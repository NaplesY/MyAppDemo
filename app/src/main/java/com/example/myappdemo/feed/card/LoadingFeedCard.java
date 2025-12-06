package com.example.myappdemo.feed.card;

import android.view.View;

import com.example.myappdemo.R;
import com.example.myappdemo.database.User;
import com.example.myappdemo.feed.viewholder.FeedViewHolder;
import com.example.myappdemo.feed.viewholder.LoadingFeedViewHolder;

public class LoadingFeedCard implements FeedCard {
    public static final int VIEW_TYPE = 0;
    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.cell_loading;
    }

    @Override
    public FeedViewHolder createViewHolder(View itemView) {
        return new LoadingFeedViewHolder(itemView);
    }

    @Override
    public boolean useThisCard(User user) {
        return false;
    }
}
