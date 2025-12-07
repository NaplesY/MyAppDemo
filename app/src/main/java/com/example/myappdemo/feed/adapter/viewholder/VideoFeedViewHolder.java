package com.example.myappdemo.feed.adapter.viewholder;

import android.annotation.SuppressLint;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.myappdemo.R;
import com.example.myappdemo.data.User;

public class VideoFeedViewHolder extends FeedViewHolder {
    private final TextView textViewName;
    private final TextView textViewAccount;
    private final TextView textViewCountdown;
    private final ImageView imageViewAvatar;
    private final ImageView imageViewCover;
    private final ProgressBar progressBarVideo;
    private final Button buttonReplay;
    private final View viewBackground;
    private int videoDuration;
    private CountDownTimer timer;
    private boolean isPlaying = false;

    public VideoFeedViewHolder(@NonNull View itemView) {
        super(itemView);
        textViewName = itemView.findViewById(R.id.textViewName);
        textViewAccount = itemView.findViewById(R.id.textViewAccount);
        textViewCountdown = itemView.findViewById(R.id.textViewCountdown);
        imageViewAvatar = itemView.findViewById(R.id.imageViewAvatar);
        imageViewCover = itemView.findViewById(R.id.imageViewCover);
        progressBarVideo = itemView.findViewById(R.id.progressBarVideo);
        buttonReplay = itemView.findViewById(R.id.buttonReplay);
        viewBackground = itemView.findViewById(R.id.viewVideoBackground);
        buttonReplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPlay(videoDuration);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void bindData(User user) {
        textViewName.setText(user.getName());
        textViewAccount.setText(user.getAccount());
        Glide.with(itemView.getContext())
                .load(user.getAvatarPath())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal) // 加载中显示的默认图
                .error(android.R.drawable.ic_menu_report_image) // 加载失败显示的图
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存
                .circleCrop()
                .into(imageViewAvatar);
        videoDuration = user.getVideoDuration();
        Glide.with(itemView.getContext())
                .load(user.getVideoCoverPath())
                .placeholder(android.R.drawable.progress_indeterminate_horizontal) // 加载中显示的默认图
                .error(android.R.drawable.ic_menu_report_image) // 加载失败显示的图
                .diskCacheStrategy(DiskCacheStrategy.ALL) // 缓存
                .into(imageViewCover);
        textViewCountdown.setText("视频时长: " + videoDuration + "s");

    }

    // 开始播放
    public void startPlay(int videoDuration){
        stopPlay();
        imageViewCover.setVisibility(View.INVISIBLE); //隐藏封面
        viewBackground.setBackgroundColor(0xFF000000); //背景
        viewBackground.setVisibility(View.VISIBLE);
        progressBarVideo.setVisibility(View.VISIBLE);
        progressBarVideo.setMax((int) videoDuration);

        //创建倒计时
        timer = new CountDownTimer(videoDuration * 1000L, 1000) {

            @SuppressLint("SetTextI18n")
            @Override
            public void onTick(long millisUntilFinished) {
                // 每秒回调一次
                long secondsLeft = millisUntilFinished / 1000;
                long playedSeconds = videoDuration - secondsLeft;

                textViewCountdown.setText("剩余" + secondsLeft + "s");// 更新文字
                progressBarVideo.setProgress((int) playedSeconds);// 更新进度条
            }
            @Override
            public void onFinish() {
                buttonReplay.setVisibility(View.VISIBLE);
                imageViewCover.setVisibility(View.VISIBLE);
                viewBackground.setVisibility(View.INVISIBLE);
                textViewCountdown.setVisibility(View.INVISIBLE);
                progressBarVideo.setProgress((int) videoDuration);
            }
        };

        timer.start(); // 启动倒计时！
    }

    // 停止播放（防止重复播放）
    public void stopPlay() {
        if (timer != null) {
            timer.cancel(); // 防止内存泄漏
            timer = null;
        }
        resetUI();
    }
    // 充值UI
    private void resetUI() {
        textViewCountdown.setVisibility(View.VISIBLE);
        imageViewCover.setVisibility(View.VISIBLE);
        viewBackground.setVisibility(View.INVISIBLE);
        buttonReplay.setVisibility(View.INVISIBLE);
        progressBarVideo.setVisibility(View.INVISIBLE);
        progressBarVideo.setProgress(0);
        textViewCountdown.setText("待播放");
    }
}
