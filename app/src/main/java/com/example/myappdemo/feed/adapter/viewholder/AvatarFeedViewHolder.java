package com.example.myappdemo.feed.adapter.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myappdemo.R;
import com.example.myappdemo.data.User;

public class AvatarFeedViewHolder extends FeedViewHolder{
    private final TextView textViewName;
    private final TextView textViewAccount;
    private final ImageView imageViewAvatar;

    public AvatarFeedViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.textViewName);
        textViewAccount = itemView.findViewById(R.id.textViewAccount);
        imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
    }

    @Override
    public void bindData(User user, RequestOptions options) {
        textViewName.setText(user.getName());
        textViewAccount.setText(user.getAccount());
        Glide.with(itemView.getContext())
                .load(user.getAvatarPath())
                .apply(options)
                .circleCrop()
                .into(imageViewAvatar);
    }
}
