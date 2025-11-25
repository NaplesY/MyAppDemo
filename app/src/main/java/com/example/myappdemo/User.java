package com.example.myappdemo;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")

public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String account;
    private String password;
    private String name;
    private String avatarPath;

    public User() {}
    public User(String account, String password, String name, String avatarPath) {
        this.account = account;
        this.password = password;
        this.name = name;
        this.avatarPath = avatarPath;
    }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getAccount() { return account; }
    public void setAccount(String account) { this.account = account; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getAvatarPath() {
        return avatarPath;
    }
    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }
}
