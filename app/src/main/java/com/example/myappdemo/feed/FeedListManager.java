package com.example.myappdemo.feed;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class FeedListManager {
    private final RecyclerView recyclerView;
    private final GridLayoutManager gridLayoutManager;
    private final LoadMoreCallback loadMoreCallback;
    private boolean isLoadingMore = false;


    public interface LoadMoreCallback {
        void onLoadMore();
    }

    public FeedListManager(RecyclerView recyclerView, GridLayoutManager gridLayoutManager, LoadMoreCallback loadMoreCallback) {
        this.recyclerView = recyclerView;
        this.gridLayoutManager = gridLayoutManager;
        this.loadMoreCallback = loadMoreCallback;
        initScrollListener();
        initSpanSizeLookup();
    }

    private void initScrollListener() {
        // 加载更多--滚动条件判定
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy <= 0) return;
                LinearLayoutManager lm = (LinearLayoutManager) recyclerView.getLayoutManager();
                if (lm == null) return;
                int lastVisiblePos = lm.findLastVisibleItemPosition();// 最后一个可见项的位置
                int totalItemCount = lm.getItemCount();// 总Item数
                // 条件：滑到最后一项 + 不在加载中 + 有数据
                if (lastVisiblePos == totalItemCount - 1
                        && !isLoadingMore
                        && totalItemCount > 0) {
                    loadMore(); // 加载更多
                }
            }
        });
    }

    private void initSpanSizeLookup() {
        // 特殊卡片保持单列
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (gridLayoutManager.getSpanCount() == 1) return 1;// 单列时都保持一列
                if (recyclerView.getAdapter() instanceof FeedAdapter){
                    int itemType = recyclerView.getAdapter().getItemViewType(position);
                    return itemType == 0 || itemType == 3 ? 2 : 1;
                }
                return 1;
            }
        });
    }

    // 加载更多（追加数据）
    private void loadMore() {
        isLoadingMore = true;
        if (loadMoreCallback != null) {
            loadMoreCallback.onLoadMore();
        }
    }
    // 设置加载状态
    public void setLoadingMore(Boolean isLoading) {
        this.isLoadingMore = isLoading;
    }

    // 切换列数
    public void switchSpanCount(boolean isDoubleColumn) {
        gridLayoutManager.setSpanCount(isDoubleColumn ? 2 : 1);
        recyclerView.getAdapter().notifyDataSetChanged();
    }


}
