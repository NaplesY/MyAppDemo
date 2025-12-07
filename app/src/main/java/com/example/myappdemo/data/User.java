package com.example.myappdemo.data;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "user")

public class User {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String account;
    private String password;
    private String name;
    @ColumnInfo(defaultValue = "false")
    private boolean hasAvatar;
    @ColumnInfo(defaultValue = "null")
    private String avatarPath;
    @ColumnInfo(defaultValue = "false")
    private boolean hasVideo;
    @ColumnInfo(defaultValue = "0")
    private int videoDuration;
    @ColumnInfo(defaultValue = "null")
    private String videoCoverPath;

    public User(String account, String password) {
        this.account = account;
        this.password = password;
        this.name = "默认昵称";
        this.hasAvatar = false;
        this.avatarPath = null;
        this.hasVideo = false;
        this.videoDuration = 0;
        this.videoCoverPath = null;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public void setAvatarPath(String avatarPath) {
        this.avatarPath = avatarPath;
    }

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    public int getVideoDuration() {
        return videoDuration;
    }

    public void setVideoDuration(int videoDuration) {
        this.videoDuration = videoDuration;
    }

    public String getVideoCoverPath() {
        return videoCoverPath;
    }

    public void setVideoCoverPath(String videoCoverPath) {
        this.videoCoverPath = videoCoverPath;
    }


    public boolean isHasAvatar() {
        return hasAvatar;
    }

    public void setHasAvatar(boolean hasAvatar) {
        this.hasAvatar = hasAvatar;
    }
}
