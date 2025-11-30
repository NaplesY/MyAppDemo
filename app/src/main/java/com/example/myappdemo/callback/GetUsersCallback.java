package com.example.myappdemo.callback;

import com.example.myappdemo.database.User;

import java.util.List;

public interface GetUsersCallback {
    void onGetUsersResult(List<User> users);
}
