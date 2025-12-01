package com.example.myappdemo.feed;

import com.example.myappdemo.database.User;
import com.example.myappdemo.feed.card.FeedCard;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FeedCardRegistry {
    // singleton
    private static volatile FeedCardRegistry instance;

    // key：viewType，value：卡片
    private final Map<Integer, FeedCard> cardMap = new HashMap<>();
    private final List<FeedCard> cardList = new ArrayList<>();

    private FeedCardRegistry() {}

    public static FeedCardRegistry getInstance() {
        if (instance == null) {
            synchronized (FeedCardRegistry.class) {
                if (instance == null) {
                    instance = new FeedCardRegistry();
                }
            }
        }
        return instance;
    }

    //注册卡片
    public void registerCard(FeedCard card) {
        int viewType = card.getViewType();
        if (!cardMap.containsKey(viewType)) {
            cardMap.put(viewType, card);
            cardList.add(card);
        }
    }

    //选择用哪个卡片布局
    public FeedCard chooseCardForUser(User user) {
        for (FeedCard card : cardList) {
            if (card.useThisCard(user)) {
                return card;
            }
        }
        throw new IllegalArgumentException(user + "无卡片可用");
    }

    // 根据 viewType 找对应的卡片（用于在创建 ViewHolder）
    public FeedCard findCardByViewType(int viewType) {
        return cardMap.get(viewType);
    }
}
