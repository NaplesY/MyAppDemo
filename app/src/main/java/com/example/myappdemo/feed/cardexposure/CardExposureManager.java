package com.example.myappdemo.feed.cardexposure;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.Map;

public class CardExposureManager {
    private final RecyclerView recyclerView;
    private final GridLayoutManager gridLayoutManager;
    private final CardExposureCallback exposureCallback;
    private final Map<Integer, CardExposureState> exposureStateMap = new HashMap<>();

    public CardExposureManager(RecyclerView recyclerView, GridLayoutManager gridLayoutManager, CardExposureCallback exposureCallback) {
        this.recyclerView = recyclerView;
        this.gridLayoutManager = gridLayoutManager;
        this.exposureCallback = exposureCallback;
        initListeners();
    }

    private void initListeners(){
        // 卡片消失时
        recyclerView.addOnChildAttachStateChangeListener(new RecyclerView.OnChildAttachStateChangeListener() {
            @Override
            public void onChildViewAttachedToWindow(@NonNull View view) {
            }

            @Override
            public void onChildViewDetachedFromWindow(@NonNull View view) {
                RecyclerView.ViewHolder holder = recyclerView.getChildViewHolder(view);
                int position = holder.getBindingAdapterPosition();
                if (position == RecyclerView.NO_POSITION) {
                    return;
                }
                exposureCallback.onCardDisappear(holder, position);
                // 重置卡片状态
                CardExposureState state = exposureStateMap.get(position);
                if (state != null) {
                    state.reset();
                    state.setHasDisappear(true);
                }

            }
        });

        //滚动时和滚动停止时
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                checkVisibleCards();
            }
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    checkVisibleCards();
                }
            }
        });
        //布局改变（单双列切换、刷新……）
        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                recyclerView.postDelayed(() -> checkVisibleCards(), 100);//防止View加载慢，延迟100ms
            }
        });
        //初始化时
        recyclerView.postDelayed(this::checkVisibleCards, 100);
    }

    private void checkVisibleCards() {
        if (recyclerView.getAdapter() == null || recyclerView.getAdapter().getItemCount() == 0){
            return;
        }
        int firstVisiblePos = gridLayoutManager.findFirstVisibleItemPosition();
        int lastVisiblePos = gridLayoutManager.findLastVisibleItemPosition();
        // 遍历可见卡片
        for (int position = firstVisiblePos; position <= lastVisiblePos; position++) {
            RecyclerView.ViewHolder viewHolder = recyclerView.findViewHolderForAdapterPosition(position);

            View cardView = viewHolder != null ? viewHolder.itemView : null;
            float visibleRatio = 0.0f;

            // 获取曝光状态or新建
            CardExposureState exposureState = exposureStateMap.computeIfAbsent(position, i -> new CardExposureState(i, false, false, false, true));
            // 计算曝光比例
            if (cardView != null) {
                visibleRatio = CardExposureCalculator.calculateVisibleRatio(cardView);
            }
            // 判断曝光状态
            updateCardExposureState(position, visibleRatio, exposureState);
        }
    }

    //根据曝光比例更新曝光状态，触发回调
    private void updateCardExposureState(int position, float visibleRatio, CardExposureState exposureState) {
        if (exposureState == null) {
            return;
        }
        // 卡片露出
        if (visibleRatio > 0.0f){
            exposureState.setHasDisappear(false);
            if (!exposureState.isHasExposed()){
                exposureCallback.onCardStartExpose(position, visibleRatio);
                exposureState.setHasExposed(true);
            }
            // 卡片露出超过50%
            if (visibleRatio > 0.5f && !exposureState.isHasHalfExposed()){
                exposureCallback.onCardHalfExpose(position, visibleRatio);
                exposureState.setHasHalfExposed(true);
            }
            // 卡片完整露出
            if (visibleRatio == 1.0f && !exposureState.isHasFullyExposed()){
                exposureCallback.onCardFullyExpose(position, visibleRatio);
                exposureState.setHasFullyExposed(true);
            }
        }

    }
}
