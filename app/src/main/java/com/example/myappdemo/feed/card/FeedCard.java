package com.example.myappdemo.feed.card;

import android.view.View;

import com.example.myappdemo.database.User;
import com.example.myappdemo.feed.viewholder.FeedViewHolder;

public interface FeedCard {

    //返回卡片类型VIEW_TYPE
    int getViewType();

    //返回卡片布局
    int getLayoutResId();

    //创建ViewHolder
    FeedViewHolder createViewHolder(View itemView);

    //使用该卡片的条件
    boolean useThisCard(User user);
}
