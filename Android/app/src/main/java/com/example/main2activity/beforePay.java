package com.example.main2activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main2activity.PayClass.CustomDialog;

import org.json.JSONArray;
import org.json.JSONObject;

public class beforePay extends AppCompatActivity implements View.OnClickListener {

    TextView order_amount, order_date, order_time, order_movie, order_total;
    ImageView order_poster;
    int movieJSONID, amount, price;
    Bitmap poster;
    String date, time, movie;
    String user_card;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                order_amount.setText("" + amount);
                order_total.setText("" + amount * price);
                order_date.setText(date);
                order_time.setText(time);
                order_movie.setText(movie);
                order_poster.setVisibility(View.VISIBLE);
                order_poster.setImageBitmap(poster);
            }
            else if (msg.what == 1) {
                if(user_card.equals("NO_CARD")){
                    Toast.makeText(beforePay.this, "请先绑定银行卡", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(beforePay.this, card.class);
                    startActivityForResult(intent, 0);
                }
                else{
                    CustomDialog customDialog = new CustomDialog();
                    CustomDialog.order_id = selectSeat.order_id;
                    customDialog.show(getSupportFragmentManager(), "");
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_before_pay);

        order_amount = findViewById(R.id.order_amount);
        order_movie = findViewById(R.id.order_movie);
        order_date = findViewById(R.id.order_date);
        order_time = findViewById(R.id.order_time);
        order_total = findViewById(R.id.order_total);
        order_poster = findViewById(R.id.order_poster);

        findViewById(R.id.order_pay).setOnClickListener(this);
        findViewById(R.id.order_pay_by_cash).setOnClickListener(this);
        findViewById(R.id.back_before_pay).setOnClickListener(this);

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

        new Thread() {
            public void run() {

                try{
                    String detail_all = ConAPI.getRecordByID(selectSeat.order_id.get(0));
                    System.out.println("Record detail: " + detail_all);
                    if (!TextUtils.isEmpty(detail_all)){
                        JSONObject obj = new JSONObject(detail_all);
                        JSONObject current = obj.getJSONObject("data");
                        movie = current.getString("movie_name");
                        date = current.getString("date");
                        time = current.getString("start_time");
                        price = current.getInt("price");
                        byte[] posterByte = ConAPI.getImage(current.getString("poster"));
                        poster = BitmapFactory.decodeByteArray(posterByte, 0, posterByte.length);
                    }
                    amount = selectSeat.order_id.size();
                }catch (Exception e){
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(0);
            }
        }.start();

    }

    public void onClick(View v){
        if (v.getId() == R.id.order_pay){
            new Thread() {
                public void run() {
                    handler.sendEmptyMessage(1);
                }
            }.start();
        }
        else if (v.getId() == R.id.back_before_pay){
            Intent intent = new Intent(beforePay.this, allmovie.class);
            startActivityForResult(intent, 0);
        }
        else if (v.getId() == R.id.order_pay_by_cash){
            Intent intent = new Intent(beforePay.this, allmovie.class);
            startActivityForResult(intent, 0);
        }
    }
}
