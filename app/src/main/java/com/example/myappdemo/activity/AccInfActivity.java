package com.example.myappdemo.activity;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.myappdemo.R;
import com.example.myappdemo.database.User;
import com.example.myappdemo.database.UserViewModel;

import java.util.function.Consumer;

public class AccInfActivity extends AppCompatActivity {

    EditText etName;
    Button btnEtName;
    TextView accName;
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

        accName = findViewById(R.id.accName);
        etName = findViewById(R.id.editTextName);
        btnEtName = findViewById(R.id.buttonEtName);
        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);

        Intent intent = getIntent();
        account = intent.getStringExtra("ACCOUNT");

        //展示昵称
        userViewModel.getUserLiveByAccount(account).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                String showText = user.getName();
                accName.setText(showText);
            }
        });
        //修改昵称
        btnEtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etName.setText(accName.getText().toString());
                etName.setVisibility(VISIBLE);
                accName.setVisibility(GONE);
            }
        });
        // 结束昵称编辑
        etName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    finishEdit();
                    return true;
                }
                return false;
            }
        });
        etName.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    finishEdit();
                }
            }
        });

    }

    private void finishEdit() {
        String newName = etName.getText().toString().trim();
        if (!newName.isEmpty()) {
            userViewModel.updateUserInf(account, new Consumer<User>() {
                @Override
                public void accept(User user) {
                    if (user != null) {
                        user.setName(newName);
                    }
                }
            });
        } else {
            accName.setText("请输入昵称");
        }

        etName.setVisibility(View.GONE);
        accName.setVisibility(View.VISIBLE);
    }
}