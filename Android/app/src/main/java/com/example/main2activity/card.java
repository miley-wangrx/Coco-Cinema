package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
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

public class card extends AppCompatActivity implements View.OnClickListener {

    TextView card;
    Button add, edit, delete;
    String user_card = "";

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 1){ // set textView and button
                if(user_card.equals("NO_CARD")){
                    add.setVisibility(View.VISIBLE);
                }
                else if (!user_card.isEmpty()){
                    card.setText(user_card);
                    edit.setVisibility(View.VISIBLE);
                    delete.setVisibility(View.VISIBLE);
                }
            }
            else if(msg.what == 2){
                Toast.makeText(card.this, "请先登录", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(card.this, welcome.class);
                startActivityForResult(login, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        ImageButton back = findViewById(R.id.back_card);
        add = findViewById(R.id.add_card);
        edit = findViewById(R.id.edit_card);
        delete = findViewById(R.id.delete_card);
        card = findViewById(R.id.card);

        back.setOnClickListener(this);
        add.setOnClickListener(this);
        edit.setOnClickListener(this);
        delete.setOnClickListener(this);

        new Thread(){
            public void run(){

                try{
                    String detail_all = ConAPI.getCurrentUserCard();

                    if (!TextUtils.isEmpty(detail_all) && !detail_all.equals("NEED_LOGIN") ){
                        JSONObject obj = new JSONObject(detail_all);
                        user_card = obj.getString("message");
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
        if (v.getId() == R.id.add_card){ // 添加
            Intent intent = new Intent(card.this, editCard.class);
            intent.putExtra("operation",0);
            startActivityForResult(intent, 0);
        }
        if (v.getId() == R.id.delete_card){ // 删除
            AlertDialog.Builder builder = new AlertDialog.Builder(card.this);
            builder.setMessage("Are you sure?");
            builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    //删除

                    //跳转
                    new Thread(){
                        public void run(){
                            try{
                                String detail_all = ConAPI.resetCard();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    Intent intent = new Intent(card.this, card.class);
                    startActivityForResult(intent, 0);
                }
            });
            builder.setNegativeButton("No", null);
            AlertDialog alert = builder.create();
            alert.show();

        }
        if (v.getId() == R.id.edit_card){ // 编辑
            Intent intent = new Intent(card.this, editCard.class);
            intent.putExtra("operation",1);
            intent.putExtra("card_number",user_card);
            startActivityForResult(intent, 0);
        }
        if (v.getId() == R.id.back_card){
            Intent intent = new Intent(card.this, userCenter.class);
            startActivityForResult(intent, 0);
        }
    }
}
