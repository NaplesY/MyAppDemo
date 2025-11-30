package com.example.myappdemo.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.lifecycle.ViewModelProvider;

import com.example.myappdemo.R;
import com.example.myappdemo.callback.LoginCallback;
import com.example.myappdemo.database.User;
import com.example.myappdemo.database.UserViewModel;

public class LogPageActivity extends AppCompatActivity {


    EditText etAccount, etPassword;
    Button btnRegister, btnLogin, btnDeleteAll;
    UserViewModel userViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_log_page);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        etAccount = findViewById(R.id.editTextAccount);
        etPassword = findViewById(R.id.editTextPassword);
        btnRegister = findViewById(R.id.btnRegister);
        btnLogin = findViewById(R.id.btnLogin);
        btnDeleteAll = findViewById(R.id.btnDeleteAll);

        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);

        //注册功能
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                User userNew = new User(account, password, "请输入昵称", null);
                userViewModel.register(userNew);
            }
        });

        //登录功能
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                userViewModel.login(account, password, new LoginCallback(){
                    @Override
                    public void onLoginResult(boolean success, String msg) {
                        if (success) {
                            Toast.makeText(LogPageActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LogPageActivity.this, AccInfActivity.class);
                            intent.putExtra("ACCOUNT", account);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LogPageActivity.this, msg, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        btnDeleteAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.deleteAllUsers();
            }
        });
    }



}