package com.example.myappdemo.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.myappdemo.callback.GetUsersCallback;
import com.example.myappdemo.callback.LoginCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class UserViewModel extends AndroidViewModel {

    private final MutableLiveData<List<User>> allUsersLive = new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<Boolean> isLoadingMore = new MutableLiveData<>(false);
    private final MutableLiveData<Boolean> isRefreshing = new MutableLiveData<>(false);
    private final MutableLiveData<List<User>> newUsersLive = new MutableLiveData<>();
    private final UserRepository userRepository;


    public UserViewModel(@NonNull Application application) {
        super(application);
        userRepository = new UserRepository(application);
        userRepository.getAllUsers(allUsersLive::setValue);
    }

    public LiveData<User> getUserLiveByAccount(String account){
        return userRepository.getUserLiveByAccount(account);
    }

    public LiveData<List<User>> getAllUsersLive(){
        return allUsersLive;
    }

    public LiveData<List<User>> getNewUsersLive() { return newUsersLive; }

    public LiveData<Boolean> isLoadingMore() {
        return isLoadingMore;
    }

    public LiveData<Boolean> isRefreshing() {
        return isRefreshing;
    }

    //数据库管理
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

    public void update(User user) {
        userRepository.update(user);
    }


    // feed流数据管理
    // 加载更多数据
    public void loadMoreUsers() {
        if (Boolean.TRUE.equals(isLoadingMore.getValue())) return;
        isLoadingMore.setValue(true);
        userRepository.getAllUsers(new GetUsersCallback() {
            @Override
            public void onGetUsersResult(List<User> users) {
                List<User> currentList = allUsersLive.getValue();
                if (currentList != null) {
                    currentList.addAll(users);
                }
                newUsersLive.setValue(users);
                isLoadingMore.setValue(false);
            }
        });
    }
    // 初始化数据
    public void initUsers() {
        userRepository.getAllUsers(new GetUsersCallback() {
            @Override
            public void onGetUsersResult(List<User> users) {
                allUsersLive.setValue(users);
            }
        });
    }
    // 刷新数据
    public void refreshUsers() {
        isRefreshing.setValue(true);
        userRepository.getAllUsers(new GetUsersCallback() {
            @Override
            public void onGetUsersResult(List<User> users) {
                allUsersLive.setValue(users);
                isRefreshing.setValue(false);
            }
        });
    }

}
