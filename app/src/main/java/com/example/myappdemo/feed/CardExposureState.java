package com.example.myappdemo.feed;

public class CardExposureState {
    //用于临时储存卡片曝光状态
    private int cardId;
    private boolean hasExposed;
    private boolean hasHalfExposed;
    private boolean hasFullyExposed;
    private boolean hasDisappear;

    public CardExposureState(int cardId, boolean hasExposed, boolean hasHalfExposed, boolean hasFullyExposed, boolean hasDisappear) {
        this.cardId = cardId;
        this.hasExposed = false;
        this.hasHalfExposed = false;
        this.hasFullyExposed = false;
        this.hasDisappear = true;
    }

    public int getCardId() {
        return cardId;
    }

    public void setCardId(int cardId) {
        this.cardId = cardId;
    }

    public boolean isHasExposed() {
        return hasExposed;
    }

    public void setHasExposed(boolean hasExposed) {
        this.hasExposed = hasExposed;
    }

    public boolean isHasHalfExposed() {
        return hasHalfExposed;
    }

    public void setHasHalfExposed(boolean hasHalfExposed) {
        this.hasHalfExposed = hasHalfExposed;
    }

    public boolean isHasFullyExposed() {
        return hasFullyExposed;
    }

    public void setHasFullyExposed(boolean hasFullyExposed) {
        this.hasFullyExposed = hasFullyExposed;
    }

    public boolean isHasDisappear() {
        return hasDisappear;
    }

    public void setHasDisappear(boolean hasDisappear) {
        this.hasDisappear = hasDisappear;
    }

    //重置所有状态，备用
    public void reset() {
        this.hasExposed = false;
        this.hasHalfExposed = false;
        this.hasFullyExposed = false;
        this.hasDisappear = true;
    }
}
