package com.example.main2activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

public class first extends AppCompatActivity {

    //声明时间有多少;
     private int count = 3;
     private Animation animation;

    //进行一个消息的处理
     @SuppressLint("HandlerLeak")
     private Handler handler = new Handler() {
         public void handleMessage(android.os.Message msg) {
             if (msg.what == 0) {
//              textView.setText(getCount()+"");
                getCount();
                handler.sendEmptyMessageDelayed(0, 1000);
                //animation.reset();
//              textView.startAnimation(animation);
             }

         };
     };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first);
//        animation = AnimationUtils.loadAnimation(this, R.anim.animation_text);
        handler.sendEmptyMessageDelayed(0, 1000);
    }

    //咱在写一个计算Welcome界面的广告时间结束后进入主界面的方法
     private int getCount() {
         count--;
         if (count == 0) {
                 Intent intent = new Intent(this, allmovie.class);
                 startActivity(intent);
                 //finish();
             }
         return count;
     }
}


