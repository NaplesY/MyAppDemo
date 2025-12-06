package com.example.myappdemo.callback;

import androidx.recyclerview.widget.RecyclerView;

public interface CardExposureListener {

    //cardId 卡片id
    //visibleRatio 卡片曝光比例（0到1）

    //卡片露出
    void onCardStartExpose(int cardId, float visibleRatio);

    //卡片露出超过50％
    void onCardHalfExpose(int cardId, float visibleRatio);

    //卡片完整露出
    void onCardFullyExpose(int cardId, float visibleRatio);

    //卡片消失
    void onCardDisappear(RecyclerView.ViewHolder holder, int cardId);

}
