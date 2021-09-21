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

import org.json.JSONObject;


public class verification extends AppCompatActivity implements View.OnClickListener{

    EditText et_phone;
    EditText et_code;
    String phone_number;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 0){ //发送验证码
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(verification.this);
                    builder.setMessage("The verification code has been sent to your Phone.");
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(verification.this);
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to send the verification code. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else if(msg.what == 1){ //检查验证码
                try{
                    if(msg.obj.equals("right")){
                        Intent intent = new Intent(verification.this, register.class);
                        intent.putExtra("phone",phone_number);
                        startActivityForResult(intent, 0);
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(verification.this);
                        builder.setMessage(msg.obj.toString());
                        builder.setPositiveButton("Ok.", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(verification.this);
                    String s1 = e.toString();
                    builder.setMessage("Verification code wrong. " + s1);
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
        setContentView(R.layout.activity_verification);

        Button send_button = findViewById(R.id.send_button);
        send_button.setOnClickListener(this);

        Button next_button = findViewById(R.id.next);
        next_button.setOnClickListener(verification.this);

        ImageButton back_veri = findViewById(R.id.back_veri);
        back_veri.setOnClickListener(this);

        et_phone = findViewById(R.id.edit_phone_veri);
        et_code = findViewById(R.id.edit_text_code);
    }

    public void onClick(View v){ // 点击事件的处理方法

        if (v.getId() == R.id.send_button){ // 判断是否是send_button被点击
            new Thread(){
                public void run(){
                    try{
                        phone_number = et_phone.getText().toString();
                        String detail_phone = ConAPI.sendPhoneCode(phone_number);
                        System.out.println("Send telephone code debug message: " + detail_phone);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                };
            }.start();
        }
        if (v.getId() == R.id.next){ //判断是否是next_button被点击
            new Thread(){
                public void run(){
                    String detail_code = " ";
                    int code = 5;
                    try{
                        System.out.println("et_code is: ");
                        String veri_code = et_code.getText().toString();
                        System.out.println(veri_code);

                        System.out.println("detail code is: ");
                        detail_code = ConAPI.checkPhoneCode(veri_code);
                        System.out.println(detail_code);
                        if (!TextUtils.isEmpty(detail_code)) {
                            JSONObject obj = new JSONObject(detail_code);
                            code = obj.getInt("code");
                        }
                        else{
                            System.out.println("detail_code empty");
                            System.out.println("et_code is: " + et_code);
                        }
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    Message msg_code = Message.obtain();
                    msg_code.what = 1;
                    if(code == 1){
                        msg_code.obj = "验证码错误";
                    }
                    else if(code == 2){
                        msg_code.obj = "请先获取验证码再验证";
                    }
                    else if(code == 0){
                        msg_code.obj = "right";
                    }
                    else{
                        msg_code.obj = "Error about checking the code.";
                        System.out.println("check " + code);
                    }
                    handler.sendMessage(msg_code);
                };
            }.start();

        }
        if (v.getId() == R.id.back_veri){
            Intent back_intent = new Intent(verification.this, welcome.class);
            startActivityForResult(back_intent, 0);
        }
    }
}




