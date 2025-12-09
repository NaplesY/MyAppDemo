package com.example.myappdemo.data;

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

    // 测试用！一键生成几个垃圾数据
    public void generateTestData() {
        executor.execute(new Runnable() {
            @Override
            public void run() {

                User u1 = new User("test1", "123456");
                u1.setName("小张");
                u1.setHasVideo(true);
                u1.setVideoDuration(15);
                u1.setVideoCoverPath("https://picsum.photos/300/200?t=156194");
                userDao.insertUser(u1);

                User u2 = new User("test2", "123456");
                u2.setName("小王");
                u2.setHasAvatar(true);
                u2.setAvatarPath("https://picsum.photos/100/100?t=74864");
                userDao.insertUser(u2);

                User u3 = new User("test3", "123456");
                u3.setName("文字卡片");
                userDao.insertUser(u3);

                User u4 = new User("test4", "123456");
                u4.setName("视频卡片");
                u4.setHasAvatar(true);
                u4.setAvatarPath("https://picsum.photos/100/100?t=849");
                u4.setHasVideo(true);
                u4.setVideoDuration(5);
                u4.setVideoCoverPath("https://picsum.photos/300/200?t=1986189");
                userDao.insertUser(u4);

                User u5 = new User("test5", "123456");
                u5.setName("图像卡片");
                u5.setHasAvatar(true);
                u5.setAvatarPath("https://picsum.photos/100/100?t=884");
                userDao.insertUser(u5);

                User u6 = new User("test6", "123456");
                u6.setName("小李");
                u6.setHasAvatar(true);
                u6.setAvatarPath("https://picsum.photos/100/100?t=1111");
                u6.setHasVideo(true);
                u6.setVideoDuration(10);
                u6.setVideoCoverPath("https://picsum.photos/300/200?t=68168");
                userDao.insertUser(u6);

                User u7 = new User("test7", "123456");
                u7.setName("111");
                userDao.insertUser(u7);

                User u8 = new User("test8", "123456");
                u8.setName("222");
                u8.setHasAvatar(true);
                u8.setAvatarPath("https://picsum.photos/100/100?t=2784");
                userDao.insertUser(u8);

                User u9 = new User("test9", "123456");
                u9.setName("凑数的");
                userDao.insertUser(u9);

                User u10 = new User("test10", "123456");
                u10.setName("qwerty");
                u10.setHasAvatar(true);
                u10.setAvatarPath("https://picsum.photos/100/100?t=114511");
                u10.setHasVideo(true);
                u10.setVideoDuration(8);
                u10.setVideoCoverPath("https://picsum.photos/300/200?t=1919810");
                userDao.insertUser(u10);

                User u11 = new User("test11", "123456");
                u11.setName("afwfawf");
                u11.setHasAvatar(true);
                u11.setAvatarPath("https://picsum.photos/100/100?t=114831");
                u11.setHasVideo(true);
                u11.setVideoDuration(4);
                u11.setVideoCoverPath("https://picsum.photos/300/200?t=14534");
                userDao.insertUser(u11);
            }
        });
    }

}
