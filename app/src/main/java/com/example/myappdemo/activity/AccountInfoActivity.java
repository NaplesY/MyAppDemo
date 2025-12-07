package com.example.myappdemo.activity;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.myappdemo.R;
import com.example.myappdemo.callback.OnFinishEditListener;
import com.example.myappdemo.data.User;
import com.example.myappdemo.data.UserViewModel;

import java.util.function.Consumer;

public class AccountInfoActivity extends AppCompatActivity {

    EditText etName, etVideoDuration;
    ImageView userAvatar;
    Button btnEditName, btnCreateAvatar;
    TextView userName, userNameMain, userAccount;
    Switch swShowAvatar, swShowVideo;
    UserViewModel userViewModel;
    String account;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_acc_inf_acitivity);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        userName = findViewById(R.id.userName);
        userNameMain = findViewById(R.id.userNameMain);
        userAccount = findViewById(R.id.userAccount);
        userAvatar = findViewById(R.id.userAvatar);
        etName = findViewById(R.id.editTextName);
        etVideoDuration = findViewById(R.id.editVideoDuration);
        btnEditName = findViewById(R.id.buttonEditName);
        btnCreateAvatar = findViewById(R.id.buttonCreateAvatar);
        swShowAvatar = findViewById(R.id.switchShowAvatar);
        swShowVideo = findViewById(R.id.switchShowVideo);
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);

        Intent intent = getIntent();
        account = intent.getStringExtra("ACCOUNT");

        //展示昵称
        userViewModel.getUserLiveByAccount(account).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                if (user == null) {
                    userName.setText("未知用户");
                    return;
                }
                String nameText = user.getName();
                String accountText = user.getAccount();
                String avatarPath = user.getAvatarPath();
                userName.setText(nameText);
                userNameMain.setText(nameText);
                userAccount.setText(accountText);
                Glide.with(AccountInfoActivity.this)
                        .load(avatarPath)
                        .placeholder(android.R.drawable.progress_indeterminate_horizontal) // 加载中显示的默认图
                        .error(android.R.drawable.ic_menu_report_image) // 加载失败显示的图
                        .circleCrop() //
                        .into(userAvatar);
            }
        });
        //修改昵称
        btnEditName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText(userName.getText().toString());
                etName.setVisibility(VISIBLE);
                userName.setVisibility(INVISIBLE);
            }
        });
        // 结束昵称编辑条件
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    finishEditName();
                    return true;
                }
                return false;
            }
        });
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    finishEditName();
                }
            }
        });
        // 生成头像
        btnCreateAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newAvatarPath = getRandomPicUrl(100,100);
                userViewModel.updateUserInf(account, new Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        user.setAvatarPath(newAvatarPath);
                    }
                });
            }
        });
        // 设置开关的默认状态
        userViewModel.getUserLiveByAccount(account).observe(this, user -> {
            if (user != null) {
                swShowAvatar.setChecked(user.isHasAvatar());
                swShowVideo.setChecked(user.isHasVideo());
            }
        });
        // 是否展示头像
        swShowAvatar.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                userViewModel.updateUserInf(account, new Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        if (user != null) {
                            user.setHasAvatar(isChecked);
                        }
                    }
                });
                if (isChecked) {
                    Toast.makeText(AccountInfoActivity.this, "将会展示头像", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountInfoActivity.this, "将会隐藏头像", Toast.LENGTH_SHORT).show();
                }
            }
        });
        // 是否展示视频
        swShowVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(@NonNull CompoundButton buttonView, boolean isChecked) {
                userViewModel.updateUserInf(account, new Consumer<User>() {
                    @Override
                    public void accept(User user) {
                        if (user != null) {
                            user.setHasVideo(isChecked);
                        }
                    }
                });
                if (isChecked) {
                    Toast.makeText(AccountInfoActivity.this, "将会展示视频", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(AccountInfoActivity.this, "将会隐藏视频", Toast.LENGTH_SHORT).show();
                }
            }
        });
        //结束时长编辑条件
        etVideoDuration.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    finishEditVideoDuration();
                    return true;
                }
                return false;
            }
        });
        etVideoDuration.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    finishEditVideoDuration();
                }
            }
        });
    }

    //EditText结束编辑
    private void finishEdit(EditText editText, OnFinishEditListener onFinishEdit) {
        // 收起键盘
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (editText.getWindowToken() != null) {
            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
        }
        editText.clearFocus(); // 清空焦点
        String inputContent = editText.getText().toString().trim();
        onFinishEdit.onFinish(inputContent);
    }

    //结束昵称编辑
    private void finishEditName() {
        finishEdit(etName, new OnFinishEditListener() {
            @Override
            public void onFinish(String inputContent) {
                if (!inputContent.isEmpty()) {

                    userViewModel.updateUserInf(account, new Consumer<User>() {
                        @Override
                        public void accept(User user) {
                            if (user != null) {
                                user.setName(inputContent);

                            }
                        }
                    });
                } else {
                    userName.setText("请输入昵称");
                }
                etName.setVisibility(INVISIBLE);
                userName.setVisibility(View.VISIBLE);
            }
        });
    }
    //结束时长编辑
    private void finishEditVideoDuration(){
        finishEdit(etVideoDuration, new OnFinishEditListener() {
            @Override
            public void onFinish(String inputContent) {
                if (!inputContent.isEmpty()) {
                    String newCoverPath = getRandomPicUrl(300,200);
                    userViewModel.updateUserInf(account, new Consumer<User>() {
                        @Override
                        public void accept(User user) {
                            if (user != null) {
                                user.setVideoDuration(Integer.parseInt(inputContent));
                                user.setVideoCoverPath(newCoverPath);
                            }
                        }
                    });
                }
            }
        });
    }
    //生成随机图片（服务端数据下发）
    private String getRandomPicUrl(int width, int height) {
        long timestamp = System.currentTimeMillis();
        return "https://picsum.photos/" + width + "/" + height + "?t=" + timestamp;
    }
}