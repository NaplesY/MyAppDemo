package com.example.myappdemo.database;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.myappdemo.callback.GetUsersCallback;
import com.example.myappdemo.callback.LoginCallback;

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

    public LiveData<User> getUserLiveByAccount(String account){
        return userRepository.getUserLiveByAccount(account);
    }

    LiveData<List<User>> getAllUsersLive(){
        return allUsersLive;
    }
    public void getAllUsers(GetUsersCallback callback){
        userRepository.getAllUsers(callback);
    }

    public void updateUserInf(String account, Consumer<User> infModifier){
        userRepository.updateUserInf(account, infModifier);
    }

    public void deleteAllUsers() {
        userRepository.deleteAllUsers();
    }
    public void register(User user) {
        userRepository.register(user);
    }
    public void login(String account, String password, LoginCallback callback) {
        userRepository.login(account, password, callback);
    }
    void update(User user) {
        userRepository.update(user);
    }


}
