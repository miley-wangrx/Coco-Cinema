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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

public class login extends AppCompatActivity {

    EditText et_phone;
    EditText et_psw;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 0){ //发送验证码
                try{
                    if(msg.obj.equals("right")) {
                        Intent login = new Intent(login.this, allmovie.class);
                        startActivityForResult(login, 0);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                        builder.setTitle("Error!");
                        String s0 = msg.obj.toString();
                        builder.setMessage("Error. " + s0);
                        builder.setPositiveButton("Ok.", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(login.this);
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to log in. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button login_button = findViewById(R.id.login);
        login_button.setOnClickListener(new MyOnClickListener());

        ImageButton back_login = findViewById(R.id.back_login);
        back_login.setOnClickListener( new MyOnClickListener());

        et_phone = findViewById(R.id.edit_phone_login);
        et_psw = findViewById(R.id.edit_password_login);
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){ // 点击事件的处理方法

            if (v.getId() == R.id.login){ //判断是否是login_button被点击
                new Thread(){
                    public void run(){
                        int code = 5;
                        try{
                            String phone_number = et_phone.getText().toString();
                            System.out.println("phone number is " + phone_number);
                            String psw = et_psw.getText().toString();
                            System.out.println("psw is "+ psw);
                            String detail = ConAPI.login(phone_number, psw);
                            System.out.println("detail is " + detail);
                            if (!TextUtils.isEmpty(detail)){
                                JSONObject obj = new JSONObject(detail);
                                code = obj.getInt("code");
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        Message msg_login = Message.obtain();
                        msg_login.what = 0;
                        if(code == 0) {
                            msg_login.obj = "right";
                        }
                        else if(code == 1){
                            msg_login.obj = "只支持使用POST方法请求";
                        }
                        else if(code == 2) {
                            msg_login.obj = "账户不存在";
                        }
                        else if(code == 3){
                            msg_login.obj = "密码错误";
                        }
                        else if(code == 4){
                            msg_login.obj = "输入不合法";
                        }
                        else{
                            msg_login.obj = "Error about login.";
                            System.out.println("login received code: " + code);
                        }
                        handler.sendMessage(msg_login);
                        //handler.sendEmptyMessage(0);
                    };
                }.start();
                /*Intent login = new Intent(login.this, allmovie.class);
                startActivityForResult(login, 0);*/
            }
            if (v.getId() == R.id.back_login){
                Intent back_intent = new Intent(login.this, welcome.class);
                startActivityForResult(back_intent, 0);
            }
        }
    }
}
