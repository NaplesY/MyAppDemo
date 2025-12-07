package com.example.myappdemo.feed.cardexposure;

import android.graphics.Rect;
import android.view.View;

public class CardExposureCalculator {
    public static float calculateVisibleRatio(View card){
        if (card == null || !card.isShown()) {
            return 0.0f;
        }

        Rect visibleRect = new Rect();
        card.getGlobalVisibleRect(visibleRect);
        if (visibleRect.isEmpty()) {
            return 0.0f;
        }

        //计算面积
        int cardWidth = card.getWidth();
        int cardHeight = card.getHeight();
        float cardArea = cardWidth * cardHeight;

        int visibleWidth = visibleRect.width();
        int visibleHeight = visibleRect.height();
        float visibleArea = visibleWidth * visibleHeight;

        float visibleRatio = visibleArea / cardArea;

        return Math.min(visibleRatio, 1.0f);
    }
}
