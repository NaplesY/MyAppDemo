package com.example.myappdemo;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.function.Consumer;

public class UserViewModel extends AndroidViewModel {

    private final UserRepository userRepository;
    private LiveData<List<User>> allUsersLive;

    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        allUsersLive = userRepository.getAllUsersLive();
    }

    LiveData<User> getUserLiveByAccount(String account){
        return userRepository.getUserLiveByAccount(account);
    }

    LiveData<List<User>> getAllUsersLive(){
        return allUsersLive;
    }
    void getAllUsers(GetUsersCallback callback){
        userRepository.getAllUsers(callback);
    }

    void updateUserInf(String account, Consumer<User> infModifier){
        userRepository.updateUserInf(account, infModifier);
    }

    void deleteAllUsers() {
        userRepository.deleteAllUsers();
    }
    void register(User user) {
        userRepository.register(user);
    }
    void login(String account, String password, LoginCallback callback) {
        userRepository.login(account, password, callback);
    }
    void update(User user) {
        userRepository.update(user);
    }


}
