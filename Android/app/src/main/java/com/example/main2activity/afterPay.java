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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.main2activity.PayClass.CustomDialog;

import org.json.JSONObject;

public class afterPay extends AppCompatActivity {

    public static Bitmap picture;
    public static int price;
    public static int amount;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0){

                ImageView posterImage = findViewById(R.id.poster_payed);
                posterImage.setVisibility(View.VISIBLE);
                posterImage.setImageBitmap(picture);

                TextView priceTXT = findViewById(R.id.textView17);
                priceTXT.setText(String.valueOf(price * amount));
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payed);

        new Thread() {
            public void run() {
                try {
                    String detail_all = ConAPI.getRecordByID(CustomDialog.order_id.get(0));
                    System.out.println("Record detail: " + detail_all);
                    if (!TextUtils.isEmpty(detail_all)){
                        JSONObject obj = new JSONObject(detail_all);
                        JSONObject current = obj.getJSONObject("data");
                        price = current.getInt("price");
                        byte[] posterByte = ConAPI.getImage(current.getString("poster"));
                        picture = BitmapFactory.decodeByteArray(posterByte, 0, posterByte.length);
                    }
                    amount = CustomDialog.order_id.size();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handler.sendEmptyMessage(0);
            }
        }.start();

        Button back_button = findViewById(R.id.backbutton);
        back_button.setOnClickListener(new MyOnClickListener());
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){ // 点击事件的处理方法
            if (v.getId() == R.id.backbutton){
                Intent movie_intent = new Intent(afterPay.this, allmovie.class);
                startActivityForResult(movie_intent, 0);
            }
        }
    }
}
