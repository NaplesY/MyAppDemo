package com.example.myappdemo.utils;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.myappdemo.feed.FeedDataManager;
import com.example.myappdemo.feed.adapter.viewholder.FeedViewHolder;
import com.example.myappdemo.feed.card.FeedCard;
import com.example.myappdemo.feed.card.FeedCardRegistry;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewHolderPreCacheUtils {
    // 缓存池：key=viewType，value=该类型的ViewHolder缓存列表
    private final Map<Integer, List<FeedViewHolder>> viewHolderCachePool = new HashMap<>();
    private static final int MAX_CACHE_NUMBER = 3;// 最大缓存数量
    private final LayoutInflater inflater;
    private final ViewGroup parent;

    // 构造方法
    public ViewHolderPreCacheUtils(ViewGroup parent) {
        this.parent = parent;
        this.inflater = LayoutInflater.from(parent.getContext());
    }


    public void preCacheViewHolders() {
        // 获取viewtypes
        List<FeedCard> allRegisteredCards = FeedCardRegistry.getInstance().cardList;
        if (allRegisteredCards.isEmpty()) return;

        // 预渲染所有卡片类型
        for (FeedCard feedCard : allRegisteredCards) {
            int viewType = feedCard.getViewType();
            // 预渲染指定数量的ViewHolder
            for (int i = 0; i < MAX_CACHE_NUMBER; i++) {
                View itemView = inflater.inflate(feedCard.getLayoutResId(), parent, false);
                FeedViewHolder holder = feedCard.createViewHolder(itemView);
                addToCachePool(viewType, holder);
            }
        }
    }


    // 从缓存池获取ViewHolder
    public FeedViewHolder getCachedViewHolder(int viewType) {
        if (!viewHolderCachePool.containsKey(viewType)) return null;
        List<FeedViewHolder> cacheList = viewHolderCachePool.get(viewType);
        if (cacheList.isEmpty()) return null;
        // LIFO复用
        return cacheList.remove(cacheList.size() - 1);
    }

    // 回收ViewHolder
    public void recycleViewHolder(int viewType, FeedViewHolder holder) {
        addToCachePool(viewType, holder);
    }

    // 缓存池添加逻辑（控制最大容量）
    private void addToCachePool(int viewType, FeedViewHolder holder) {
        viewHolderCachePool.putIfAbsent(viewType, new ArrayList<>());
        List<FeedViewHolder> cacheList = viewHolderCachePool.get(viewType);
        if (cacheList.size() < MAX_CACHE_NUMBER) {
            cacheList.add(holder);
        }
    }

    // 清空缓存池
    public void clearCache() {
        viewHolderCachePool.clear();
    }
}
