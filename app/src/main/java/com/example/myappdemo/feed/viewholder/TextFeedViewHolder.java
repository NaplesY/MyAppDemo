package com.example.myappdemo.feed.viewholder;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.myappdemo.R;
import com.example.myappdemo.database.User;

public class TextFeedViewHolder extends FeedViewHolder{
    private final TextView textViewName;
    private final TextView textViewAccount;

    public TextFeedViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.textViewName);
        textViewAccount = itemView.findViewById(R.id.textViewAccount);
    }

    @Override
    public void bindData(User user) {
        textViewName.setText(user.getName());
        textViewAccount.setText(user.getAccount());
    }
}
