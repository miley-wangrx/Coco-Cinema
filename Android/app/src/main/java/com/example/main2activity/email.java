package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class email extends AppCompatActivity implements View.OnClickListener{

    TextView email;
    Button bind;
    Button change;
    Button unbind;
    String user_email = "";

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 1){ // set textView and button
                if(user_email.equals("NO_EMAIL")){
                    bind.setVisibility(View.VISIBLE);
                }
                else if (!user_email.isEmpty()){
                    email.setText(user_email);
                    change.setVisibility(View.VISIBLE);
                    unbind.setVisibility(View.VISIBLE);
                }
            }
            else if(msg.what == 2){
                Toast.makeText(email.this, "请先登录", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(email.this, welcome.class);
                startActivityForResult(login, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email);

        ImageButton back = findViewById(R.id.back_email);
        email = findViewById(R.id.email);
        bind = findViewById(R.id.bind);
        change = findViewById(R.id.change);
        unbind = findViewById(R.id.unbind);

        back.setOnClickListener(this);
        bind.setOnClickListener(this);
        change.setOnClickListener(this);
        unbind.setOnClickListener(this);

        new Thread(){
            public void run(){

                try{
                    String detail_all = ConAPI.getCurrentUserEmail();

                    if (!TextUtils.isEmpty(detail_all) && !detail_all.equals("NEED_LOGIN") ){
                        JSONObject obj = new JSONObject(detail_all);
                        user_email = obj.getString("message");
                        handler.sendEmptyMessage(1);
                    }
                    else if(detail_all.equals("NEED_LOGIN")){
                        handler.sendEmptyMessage(2);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onClick(View v){
        if (v.getId() == R.id.bind){ // 绑定
            Intent intent = new Intent(email.this, editEmail.class);
            intent.putExtra("operation",0);
            startActivityForResult(intent, 0);
        }
        if (v.getId() == R.id.unbind){ // 解绑
            new Thread(){
                public void run(){
                    try{
                        String detail_all = ConAPI.resetEmail();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
            Intent intent = new Intent(email.this, email.class);
            intent.putExtra("operation",0);
            startActivityForResult(intent, 0);
        }
        if (v.getId() == R.id.change){ // 更换
            Intent intent = new Intent(email.this, editEmail.class);
            intent.putExtra("operation",1);
            startActivityForResult(intent, 0);
        }
        if (v.getId() == R.id.back_email){
            Intent intent = new Intent(email.this, userCenter.class);
            startActivityForResult(intent, 0);
        }
    }
}
