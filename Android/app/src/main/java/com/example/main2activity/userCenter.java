package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

public class userCenter extends AppCompatActivity {

    private String phone = "";
    int user_age = -1;
    TextView username;
    ImageView vip_logo;
    TextView age;
    String if_vip;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 0){ //log out
                try{
                    if(msg.obj.equals("right")) {
                        Toast.makeText(userCenter.this, "登出成功", Toast.LENGTH_SHORT).show();
                        Intent logout = new Intent(userCenter.this, allmovie.class);
                        startActivityForResult(logout, 0);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(userCenter.this);
                        builder.setTitle("Error!");
                        String s0 = msg.obj.toString();
                        builder.setMessage("Error. " + s0);
                        builder.setPositiveButton("Ok.", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }
                catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(userCenter.this);
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to log out. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else if(msg.what == 1){
                username.setText(phone);
                age.setVisibility(View.VISIBLE);
                age.setText("Age: " + user_age);
                if(if_vip.equals("1")){
                    vip_logo.setVisibility(View.VISIBLE);
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_center);

        ImageButton back = findViewById(R.id.back_user_center);
        back.setOnClickListener(new WelOnClickListener());

        Button logout = findViewById(R.id.logout);
        logout.setOnClickListener(new WelOnClickListener());

        Button my_record = findViewById(R.id.my_record);
        my_record.setOnClickListener(new WelOnClickListener());

        Button my_tickets = findViewById(R.id.my_tickets);
        my_tickets.setOnClickListener(new WelOnClickListener());

        Button my_email = findViewById(R.id.my_email);
        my_email.setOnClickListener(new WelOnClickListener());

        Button my_card = findViewById(R.id.my_card);
        my_card.setOnClickListener(new WelOnClickListener());
        
        findViewById(R.id.open_vip).setOnClickListener(new WelOnClickListener());

        username = findViewById(R.id.username);
        age = findViewById(R.id.age);

        vip_logo = findViewById(R.id.vip_logo);
        vip_logo.setVisibility(View.GONE);

        new Thread(){

            public void run(){

                try{
                    Message msg_user = Message.obtain();
                    String detail_user = ConAPI.getCurrentUser();
                    System.out.println("detail user: " + detail_user);
                    if (!TextUtils.isEmpty(detail_user)){
                        JSONObject obj = new JSONObject(detail_user);
                        //JSONArray data = obj.getJSONArray("data");
                        phone = obj.getString("message");
                        user_age = obj.getInt("age");;
                        if_vip = obj.getString("vip");
                        msg_user.what = 1;
                        handler.sendMessage(msg_user);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    class WelOnClickListener implements ImageButton.OnClickListener{

        @SuppressLint("ResourceType")
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.my_tickets){
                Intent movie_intent = new Intent(userCenter.this, tickets.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.my_record){
                Intent movie_intent = new Intent(userCenter.this, record.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.back_user_center){
                Intent movie_intent = new Intent(userCenter.this, allmovie.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.my_email){
                Intent movie_intent = new Intent(userCenter.this, email.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.my_card){
                Intent movie_intent = new Intent(userCenter.this, card.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.open_vip){
                Intent movie_intent = new Intent(userCenter.this, openVIP.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.logout){
                new Thread(){
                    public void run(){
                        int code = 5;
                        try{
                            String detail = ConAPI.logout();
                            System.out.println("detail is " + detail);
                            if (!TextUtils.isEmpty(detail)){
                                JSONObject obj = new JSONObject(detail);
                                code = obj.getInt("code");
                            }
                        }catch(Exception e){
                            e.printStackTrace();
                        }
                        Message msg_logout = Message.obtain();
                        msg_logout.what = 0;
                        if(code == 0) {
                            msg_logout.obj = "right";
                        }
                        else{
                            msg_logout.obj = "Error about logout.";
                            System.out.println("logout received code: " + code);
                        }
                        handler.sendMessage(msg_logout);
                    };
                }.start();
            }
        }
    }

}
