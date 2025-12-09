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
import com.example.myappdemo.data.User;
import com.example.myappdemo.data.UserViewModel;

public class LoginActivity extends AppCompatActivity {


    EditText etAccount, etPassword;
    Button btnRegister, btnLogin, btnDeleteAll, btnTestData;
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
        btnRegister = findViewById(R.id.buttonRegister);
        btnLogin = findViewById(R.id.buttonLogin);
        btnDeleteAll = findViewById(R.id.buttonDeleteAll);
        btnTestData = findViewById(R.id.buttonTestData);

        userViewModel = new ViewModelProvider.AndroidViewModelFactory(getApplication()).create(UserViewModel.class);

        //注册功能
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = etAccount.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                User userNew = new User(account, password);
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
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(LoginActivity.this, AccountInfoActivity.class);
                            intent.putExtra("ACCOUNT", account);
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
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

        btnTestData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userViewModel.generateTestData();
            }
        });
    }



}