package com.example.myappdemo.feed.cardexposure;

import androidx.recyclerview.widget.RecyclerView;

public interface CardExposureCallback {

    //cardId 卡片id
    //visibleRatio 卡片曝光比例（0到1）

    //卡片露出
    void onCardStartExpose(int position, float visibleRatio);

    //卡片露出超过50％
    void onCardHalfExpose(int position, float visibleRatio);

    //卡片完整露出
    void onCardFullyExpose(int position, float visibleRatio);

    //卡片消失
    void onCardDisappear(RecyclerView.ViewHolder holder, int position);

}
