package com.example.myappdemo.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface UserDao {
    @Insert
    void insertUser(User user);

    @Update
    void updateUser(User user);

    @Delete
    void deleteUser(User user);

    @Query("DELETE FROM user")
    void deleteAllUsers();

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAllUsersLive();

    @Query("SELECT * FROM user")
    List<User> getAllUsers();


    @Query("SELECT * FROM user WHERE account = :account")
    LiveData<User> getUserLiveByAccount(String account);

    @Query("SELECT * FROM user WHERE account = :account")
    User getUserByAccount(String account);
}
