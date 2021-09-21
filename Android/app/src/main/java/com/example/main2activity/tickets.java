package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.shapes.Shape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class tickets extends AppCompatActivity {

    int len = 5;
    ArrayList<Bitmap> tickets_list = new ArrayList<Bitmap>();
    private ScrollView scrollView;
    private Button toTopBtn;
    private View contentView;
    private int scrollY = 0;// 标记上次滑动位置
    private final String TAG = "kk123456789";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    ticketsLayout();
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(tickets.this);
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to make layout. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else if(msg.what == 2){
                Toast.makeText(tickets.this, "请先登录", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(tickets.this, welcome.class);
                startActivityForResult(login, 0);
            }
        }
    };

    /**
     * 初始化视图
     */
    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scroll_tickets);
        if (contentView == null) {
            contentView = scrollView.getChildAt(0);
        }

        toTopBtn = (Button) findViewById(R.id.top_btn_tickets);
        toTopBtn.setOnClickListener(new MyOnClickListener());
        /******************** 监听ScrollView滑动停止 *****************************/
        scrollView.setOnTouchListener(new View.OnTouchListener() {
            private int lastY = 0;
            private int touchEventId = -9983761;
            Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    View scroller = (View) msg.obj;
                    if (msg.what == touchEventId) {
                        if (lastY == scroller.getScrollY()) {
                            handleStop(scroller);
                        } else {
                            handler.sendMessageDelayed(handler.obtainMessage(
                                    touchEventId, scroller), 5);
                            lastY = scroller.getScrollY();
                        }
                    }
                }
            };

            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    handler.sendMessageDelayed(
                            handler.obtainMessage(touchEventId, v), 5);
                }
                return false;
            }

            /**
             * ScrollView 停止
             *
             * @param view
             */
            private void handleStop(Object view) {

                Log.i(TAG, "handleStop");
                ScrollView scroller = (ScrollView) view;
                scrollY = scroller.getScrollY();

                doOnBorderListener();
            }
        });
    }
    private void doOnBorderListener() {
        // 底部判断
        if (contentView != null
                && contentView.getMeasuredHeight() <= scrollView.getScrollY()
                + scrollView.getHeight()) {
            toTopBtn.setVisibility(View.VISIBLE);
            Log.i(TAG, "bottom");
        }
        // 顶部判断
        else if (scrollView.getScrollY() <= 30) {
            toTopBtn.setVisibility(View.GONE);
            Log.i(TAG, "top");
        } else {
            toTopBtn.setVisibility(View.VISIBLE);
            Log.i(TAG, "test");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tickets);

        ImageButton back_login = findViewById(R.id.back_tickets);
        back_login.setOnClickListener(new MyOnClickListener());

        initView();

        new Thread(){

            public void run(){

                try{
                    Message msg_layout = Message.obtain();
                    String detail_all = ConAPI.getUserTickets();
                    if (!TextUtils.isEmpty(detail_all) && !detail_all.equals("NEED_LOGIN") ){
                        JSONObject obj = new JSONObject(detail_all);
                        JSONArray data = obj.getJSONArray("data");
                        len = data.length();
                        for(int i = 0; i < len; i++){
                            JSONObject data_obj = data.getJSONObject(i);
                            String ticket_uuid = data_obj.getString("ticket_uuid");
                            byte[] image = ConAPI.getTicketImage(ticket_uuid);
                            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                            tickets_list.add(bmp);
                        }
                        msg_layout.what = 1;
                        handler.sendMessage(msg_layout);
                    }
                    else if(detail_all.equals("NEED_LOGIN")){
                        msg_layout.what = 2;
                        handler.sendMessage(msg_layout);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }


    private void ticketsLayout(){

        Bitmap resource;
        try{
            LinearLayout rootLayout = (LinearLayout)findViewById(R.id.root_tickets);
            for(int i = 0; i < len; i++){
                resource = tickets_list.get(i);
                ImageView ticket = new ImageView(this);
                ticket.setImageBitmap(resource);
                ticket.setAdjustViewBounds(true);
                //ticket.setOutlineSpotShadowColor(Color.BLACK);

                LinearLayout.LayoutParams ticket_parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                ticket_parent_params.setMargins(0,10,0,10);

                LinearLayout card = new LinearLayout(this);
                LinearLayout.LayoutParams card_parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                card.addView(ticket,ticket_parent_params);
                card.setBackgroundColor(Color.WHITE);
                card.setPadding(8,5,0,5);

                // 分割线
                ImageView parting_line = new ImageView(this);
                parting_line.setImageResource(R.drawable.line);
                LinearLayout.LayoutParams line_parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                rootLayout.addView(card,card_parent_params);
                rootLayout.addView(parting_line, line_parent_params);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.back_tickets) { //返回
                Intent movie_intent = new Intent(tickets.this, userCenter.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.top_btn_tickets){

                scrollView.post(new Runnable() {
                    @Override
                    public void run() {
//                        scrollView.fullScroll(ScrollView.FOCUS_DOWN);滚动到底部
//                        scrollView.fullScroll(ScrollView.FOCUS_UP);滚动到顶部
//
//                        需要注意的是，该方法不能直接被调用
//                        因为Android很多函数都是基于消息队列来同步，所以需要一部操作，
//                        addView完之后，不等于马上就会显示，而是在队列中等待处理，虽然很快，但是如果立即调用fullScroll， view可能还没有显示出来，所以会失败
//                                应该通过handler在新线程中更新
                        scrollView.fullScroll(ScrollView.FOCUS_UP);
                    }
                });
                toTopBtn.setVisibility(View.GONE);
            }
        }
    }

}
