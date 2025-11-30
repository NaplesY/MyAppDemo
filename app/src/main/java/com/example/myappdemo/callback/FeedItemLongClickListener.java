package com.example.myappdemo.callback;

import com.example.myappdemo.database.User;

public interface FeedItemLongClickListener {
    void onItemLongClick(int position, User user);
}
