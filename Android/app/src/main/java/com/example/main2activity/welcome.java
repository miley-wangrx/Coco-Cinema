package com.example.main2activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class welcome extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);

        Button wel_login = findViewById(R.id.wel_login);
        wel_login.setOnClickListener(new WelOnClickListener());

        Button wel_register = findViewById(R.id.wel_register);
        wel_register.setOnClickListener(new WelOnClickListener());
    }

    class WelOnClickListener implements View.OnClickListener{

        @Override
        public void onClick(View v) { // 点击事件的处理方法
            if (v.getId() == R.id.wel_login) { // 判断是否是login被点击
                Intent intent_login = new Intent(welcome.this, login.class);
                startActivityForResult(intent_login, 0);
            }
            if (v.getId() == R.id.wel_register) { //判断是否是register被点击
                Intent intent_register = new Intent(welcome.this, verification.class);
                startActivityForResult(intent_register, 0);
            }
        }
    }
}
