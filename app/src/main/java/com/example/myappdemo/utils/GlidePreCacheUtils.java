package com.example.myappdemo.utils;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.myappdemo.data.User;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GlidePreCacheUtils {
    // 线程池
    private static final ExecutorService PRE_CACHE_EXECUTOR = Executors.newSingleThreadExecutor();
    private final Context context;

    public GlidePreCacheUtils(Context context) {
        this.context = context.getApplicationContext();
    }


    public void preCacheAllUserImages(List<User> users) {
        if (users == null || users.isEmpty()) {
            return;
        }

        PRE_CACHE_EXECUTOR.execute(() -> {
            RequestOptions cacheOptions = new RequestOptions()
                    .diskCacheStrategy(DiskCacheStrategy.DATA) // 缓存原图+转换后的图
                    .skipMemoryCache(false); // 启用内存缓存

            for (User user : users) {
                try {
                    // 预缓存头像
                    if (user.getAvatarPath() != null && !user.getAvatarPath().isEmpty()) {
                        Glide.with(context)
                                .downloadOnly() // 下载写入缓存
                                .apply(cacheOptions)
                                .load(user.getAvatarPath())
                                .submit();
                    }
                    // 预缓存视频封面
                    if (user.getVideoCoverPath() != null && !user.getVideoCoverPath().isEmpty()) {
                        Glide.with(context)
                                .downloadOnly()
                                .apply(cacheOptions)
                                .load(user.getVideoCoverPath())
                                .submit();
                    }
                } catch (Exception e) {
                    // 预缓存失败
                    Log.e("GlidePreCache", "预加载失败: " , e);
                }
            }
    });
    }

}
