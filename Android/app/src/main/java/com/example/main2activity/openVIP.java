package com.example.main2activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main2activity.PayClass.CustomDialog;

import org.json.JSONObject;

public class openVIP extends AppCompatActivity implements View.OnClickListener {

    TextView vip_info;
    String if_vip;
    Button open;
    String user_card;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 1){ // set textView and button
                if(if_vip.equals("1")){
                    String info = "You are already a VIP.";
                    vip_info.setText(info);
                    open.setVisibility(View.GONE);
                }
                else{
                    String info = "VIP can get movie discount when paying for movies. It only asks for $100, and then you can enjoy movie discount forever.";
                    vip_info.setText(info);
                    open.setVisibility(View.VISIBLE);
                }
            }
            if (msg.what == 2){
                Toast.makeText(openVIP.this, "请先绑定银行卡", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(openVIP.this, card.class);
                startActivityForResult(intent, 0);
            }
            if (msg.what == 3){
                CustomDialog customDialog = new CustomDialog();
                customDialog.is_pay_for_vip = 1;
                customDialog.show(getSupportFragmentManager(), "");
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_vip);

        vip_info = findViewById(R.id.vip_info);
        open = findViewById(R.id.open);
        open.setOnClickListener(this);
        findViewById(R.id.back_vip).setOnClickListener(this);

        new Thread(){
            public void run(){

                try{
                    String detail_all = ConAPI.getCurrentUserCard();

                    if (!TextUtils.isEmpty(detail_all) && !detail_all.equals("NEED_LOGIN") ){
                        JSONObject obj = new JSONObject(detail_all);
                        user_card = obj.getString("message");
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread(){

            public void run(){

                try{
                    String detail_all = ConAPI.currentUserIsVIP();
                    System.out.println("If VIP: " + detail_all);
                    if (!TextUtils.isEmpty(detail_all) && !detail_all.equals("NEED_LOGIN") ){
                        JSONObject obj = new JSONObject(detail_all);
                        if_vip = obj.getString("message");
                    }
                    else{
                        if_vip = "0";
                    }
                    handler.sendEmptyMessage(1);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    public void onClick(View v){
        if (v.getId() == R.id.open){ // 开通
            if(user_card.equals("NO_CARD")){
                handler.sendEmptyMessage(2);
            }
            else{
                new Thread(){
                    public void run(){
                        try{
                            ConAPI.setUserVIP();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }.start();
                handler.sendEmptyMessage(3);
            }
        }
        if (v.getId() == R.id.back_vip){
            Intent intent = new Intent(openVIP.this, userCenter.class);
            startActivityForResult(intent, 0);
        }
    }
}
