package com.example.myappdemo.utils;

import android.content.Context;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;

import androidx.asynclayoutinflater.view.AsyncLayoutInflater;

import com.example.myappdemo.feed.card.FeedCard;
import com.example.myappdemo.feed.card.FeedCardRegistry;

import java.util.LinkedList;
import java.util.Queue;

public class FeedViewPrerenderUtils {
    private static FeedViewPrerenderUtils instance;
    // Android自带的map？好像比hashmap好用，Key: ViewType, Value: View队列
    private final SparseArray<Queue<View>> ViewPool = new SparseArray<>();

    private FeedViewPrerenderUtils() {}
    // 单例
    public static FeedViewPrerenderUtils getInstance() {
        if (instance == null) {
            instance = new FeedViewPrerenderUtils();
        }
        return instance;
    }


    public void prerender(Context context, int viewType, int count, ViewGroup parent) {
        FeedCard card = FeedCardRegistry.getInstance().findCardByViewType(viewType);
        if (card == null) return;

        AsyncLayoutInflater asyncLayoutInflater = new AsyncLayoutInflater(context);
        for (int i = 0; i < count; i++) {
            asyncLayoutInflater.inflate(card.getLayoutResId(), parent, (view, resid, p) -> {
                putView(viewType, view);
            });
        }
    }

    private synchronized void putView(int viewType, View view) {
        Queue<View> queue = ViewPool.get(viewType);
        if (queue == null) {
            queue = new LinkedList<>();
            ViewPool.put(viewType, queue);
        }
        queue.offer(view);
    }

    public synchronized View getView(int viewType) {
        Queue<View> queue = ViewPool.get(viewType);
        return (queue != null) ? queue.poll() : null;
    }

    // 清理缓存，防止内存泄漏
    public synchronized void clear() {
        ViewPool.clear();
    }
}
