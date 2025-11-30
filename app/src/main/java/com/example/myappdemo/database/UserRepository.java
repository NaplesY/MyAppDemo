package com.example.myappdemo.database;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import androidx.lifecycle.LiveData;

import com.example.myappdemo.callback.GetUsersCallback;
import com.example.myappdemo.callback.LoginCallback;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class UserRepository {
    private LiveData<User> userLive;
    private final UserDao userDao;
    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final Handler mainHandler = new Handler(Looper.getMainLooper());


    // 构造方法：获取DAO实例
    public UserRepository(Context context) {
        UserDatabase userDatabase = UserDatabase.getDatabase(context.getApplicationContext());
        userDao = userDatabase.getUserDao();
    }

    // 删除所有用户
    public void deleteAllUsers() {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                userDao.deleteAllUsers();
            }
        });
    }

    // 注册
    public void register(User user) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                userDao.insertUser(user);
            }
        });
    }

    // 登录
    public void login(String account, String password, LoginCallback callback) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                String msg;
                boolean success = false;
                if (account == null || account.trim().isEmpty()) {
                    msg = "账号不能为空";
                } else if (password == null || password.trim().isEmpty()) {
                    msg = "密码不能为空";
                } else {
                    User user = userDao.getUserByAccount(account);
                    if (user == null) {
                        msg = "账号不存在";
                    } else {
                        String passwordSaved = user.getPassword();
                        if (passwordSaved.equals(password)) {
                            success = true;
                            msg = "登录成功";
                        } else {
                            msg = "密码错误";
                        };
                    }
                }
                final boolean finalSuccess = success;
                final String finalMsg = msg;
                mainHandler.post(() -> callback.onLoginResult(finalSuccess, finalMsg));
            }
        });
    }

    //获取账户信息livedata
    public LiveData<User> getUserLiveByAccount(String account) {
        return userDao.getUserLiveByAccount(account);
    }

    //获取所有账户信息livedata
    public LiveData<List<User>> getAllUsersLive() {
        return userDao.getAllUsersLive();
    }

    //获取所有账户信息
    public void getAllUsers(GetUsersCallback callback){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                List<User> users = userDao.getAllUsers();
                mainHandler.post(() -> callback.onGetUsersResult(users));
            }
        });
    }

    //更新账户信息
    public void updateUserInf(String account, Consumer<User> infModifier){
        executor.execute(new Runnable() {
            @Override
            public void run() {
                User user = userDao.getUserByAccount(account);
                if (user == null) return;
                infModifier.accept(user);
                userDao.updateUser(user);
            }
        });
    }

    // 更新
    public void update(User user) {
        executor.execute(new Runnable() {
            @Override
            public void run() {
                userDao.updateUser(user);
            }
        });
    }



}
