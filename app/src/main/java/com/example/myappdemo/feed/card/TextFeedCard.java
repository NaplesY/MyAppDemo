package com.example.myappdemo.feed.card;

import android.view.View;

import com.example.myappdemo.R;
import com.example.myappdemo.database.User;
import com.example.myappdemo.feed.viewholder.FeedViewHolder;
import com.example.myappdemo.feed.viewholder.TextFeedViewHolder;

public class TextFeedCard implements FeedCard {
    public static final int VIEW_TYPE = 1;

    //返回卡片类型VIEW_TYPE
    @Override
    public int getViewType() {
        return VIEW_TYPE;
    }

    //返回卡片布局
    @Override
    public int getLayoutResId() {
        return R.layout.cell_card_text;
    }

    //创建ViewHolder
    @Override
    public FeedViewHolder createViewHolder(View itemView) {
        return new TextFeedViewHolder(itemView);
    }

    //使用该卡片的条件
    @Override
    public boolean useThisCard(User user) {
        return !user.isHasAvatar();
    }
}
