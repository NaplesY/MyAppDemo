package com.example.myappdemo.feed.card;

import android.view.View;

import com.example.myappdemo.R;
import com.example.myappdemo.data.User;
import com.example.myappdemo.feed.adapter.viewholder.FeedViewHolder;
import com.example.myappdemo.feed.adapter.viewholder.VideoFeedViewHolder;

public class VideoFeedCard implements FeedCard{
    public static final int VIEW_TYPE = 3;

    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    @Override
    public int getLayoutResId() {
        return R.layout.cell_card_video;
    }

    @Override
    public FeedViewHolder createViewHolder(View itemView) {
        return new VideoFeedViewHolder(itemView);
    }

    @Override
    public boolean useThisCard(User user) {
        return  user.isHasAvatar() && user.isHasVideo();
    }
}
