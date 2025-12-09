package com.example.myappdemo.feed;

import com.example.myappdemo.data.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FeedDataManager {
    private List<User> allUsers = new ArrayList<>();

    // 刷新数据
    public void setAllUsers(List<User> allUsers) {
        List<User> shuffledUsers = new ArrayList<>(allUsers);
        Collections.shuffle(shuffledUsers);
        this.allUsers.clear();
        this.allUsers.addAll(shuffledUsers);
    }

    // 加载更多数据
    public void addUsers(List<User> newUsers) {
        List<User> shuffledUsers = new ArrayList<>(newUsers);
        Collections.shuffle(shuffledUsers);
        this.allUsers.addAll(shuffledUsers);
    }

    // 删除指定位置数据
    public void removeUser(int position) {
        if (position >= 0 && position < allUsers.size()) {
            allUsers.remove(position);
        }
    }

    // 获取指定位置的User
    public User getUser(int position) {
        if (position >= 0 && position < allUsers.size()) {
            return allUsers.get(position);
        }
        return null;
    }

    // 获取User数量
    public int getItemCount() {
        return allUsers.size();
    }
}
