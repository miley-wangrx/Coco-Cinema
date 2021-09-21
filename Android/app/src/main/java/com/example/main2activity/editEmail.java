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

public class editEmail extends AppCompatActivity implements View.OnClickListener {

    EditText et_email;
    EditText et_code;
    String email_info;
    int operation = 0;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 0){ //发送验证码
                try{
                    AlertDialog.Builder builder = new AlertDialog.Builder(editEmail.this);
                    builder.setMessage("The verification code has been sent to your Email.");
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
                catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(editEmail.this);
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
                        if(operation == 0){
                            Toast.makeText(editEmail.this, "绑定成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(editEmail.this, email.class);
                            startActivityForResult(intent, 0);
                        }
                        else{
                            Toast.makeText(editEmail.this, "更改成功", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(editEmail.this, email.class);
                            startActivityForResult(intent, 0);
                        }
                    }
                    else {
                        AlertDialog.Builder builder = new AlertDialog.Builder(editEmail.this);
                        builder.setMessage(msg.obj.toString());
                        builder.setPositiveButton("Ok.", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(editEmail.this);
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
        setContentView(R.layout.activity_edit_email);

        Button send_button = findViewById(R.id.send_edit_email);
        send_button.setOnClickListener(this);

        Button next_button = findViewById(R.id.next_edit_email);
        next_button.setOnClickListener(editEmail.this);

        ImageButton back = findViewById(R.id.back_edit_email);
        back.setOnClickListener(this);

        et_email = findViewById(R.id.edit_email);
        et_code = findViewById(R.id.edit_email_code);

        Intent intent = getIntent();
        operation = intent.getIntExtra("operation",0);



    }

    public void onClick(View v){ // 点击事件的处理方法

        if (v.getId() == R.id.send_edit_email){ // 判断是否是send button被点击
            new Thread(){
                public void run(){
                    try{
                        String email = et_email.getText().toString();
                        String detail_email = ConAPI.sendEmailCode(email);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                };
            }.start();
        }
        if (v.getId() == R.id.next_edit_email){ //判断是否是next button被点击
            new Thread(){
                public void run(){
                    String detail_code = " ";
                    int code = 5;
                    try{
                        System.out.println("et_code is: ");
                        String veri_code = et_code.getText().toString();
                        System.out.println(veri_code);

                        System.out.println("detail code is: ");
                        detail_code = ConAPI.checkEmailCode(veri_code);
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
        if (v.getId() == R.id.back_edit_email){
            Intent back_intent = new Intent(editEmail.this, email.class);
            startActivityForResult(back_intent, 0);
        }
    }
}
