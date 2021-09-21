package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.main2activity.PayClass.CustomDialog;
import com.qfdqc.views.seattable.SeatTable;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


public class selectSeat extends AppCompatActivity {
    public SeatTable seatTableView;
    public static ArrayList<Integer> order_id;
    public ArrayList<Integer> booked_rows = new ArrayList<Integer>();
    public ArrayList<Integer> booked_columns = new ArrayList<Integer>();
    public ArrayList<Integer> checked_rows = new ArrayList<Integer>();
    public ArrayList<Integer> checked_columns = new ArrayList<Integer>();
    int scheduleID;
    int isVIPHall = 0;
    int user_age = 0;
    String movieName;

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if(msg.what == 1){
                if(checked_rows.size() > 0){
                    Intent login = new Intent(selectSeat.this, beforePay.class);
                    startActivityForResult(login, 0);
                }
                else{
                    Toast.makeText(selectSeat.this, "您还没有选座位哦", Toast.LENGTH_SHORT).show();
                }
            }
            else if(msg.what == 2){
                Toast.makeText(selectSeat.this, "请先登录", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(selectSeat.this, welcome.class);
                startActivityForResult(login, 0);
            }
            else if(msg.what == 3){
                seatTableView.setScreenName(msg.obj + "荧幕");//设置屏幕名称
            }
            else if(msg.what == 4){
                if(msg.obj.equals("1")){
                    Toast.makeText(selectSeat.this, "当前影厅是VIP影厅", Toast.LENGTH_SHORT).show();
                }
            }
            else if(msg.what == 5){
                Toast.makeText(selectSeat.this, "正在创建订单", Toast.LENGTH_SHORT).show();
            }
            else if(msg.what == 6){
                Toast.makeText(selectSeat.this, "座位已经被其他用户预定", Toast.LENGTH_SHORT).show();
                Intent movie_intent = new Intent(selectSeat.this, selectSeat.class);
                movie_intent.putExtra("movieName",movieName);
                movie_intent.putExtra("scheduleID",scheduleID);
                startActivityForResult(movie_intent, 0);
            }
        }
    };

    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (v.getId() == R.id.next_seat) {
                new Thread() {

                    public void run() {

                        try {
                            Message msg_layout = Message.obtain();
                            Message msg2 = Message.obtain();
                            String detail_all = ConAPI.getCurrentUser();
                            int code = 0;

                            if (!detail_all.equals("NEED_LOGIN")) {
                                if (checked_rows.size() > 0) {
                                    msg2.what = 5;
                                    handler.sendMessage(msg2);
                                    for (int i = 0; i < checked_rows.size(); i++) {
                                        String checked_msg = ConAPI.newOrder(scheduleID, checked_rows.get(i), checked_columns.get(i), "cash");
                                        System.out.println("checked message:  " + checked_msg);
                                        JSONObject obj = new JSONObject(checked_msg);
                                        code = obj.getInt("code");
                                        if (code == 3) {
                                            break;
                                        }
                                        int data = obj.getInt("order_id");
                                        order_id.add(data);
                                    }
                                }
                                if (code == 3) {
                                    msg_layout.what = 6;
                                } else {
                                    msg_layout.what = 1;
                                }
                                handler.sendMessage(msg_layout);
                            } else if (detail_all.equals("NEED_LOGIN")) {
                                msg_layout.what = 2;
                                handler.sendMessage(msg_layout);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }.start();
            }
            else if (v.getId() == R.id.back_seat){
                Intent login = new Intent(selectSeat.this, allmovie.class);
                startActivityForResult(login, 0);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_seat);

        order_id = new ArrayList<Integer>();

        Intent intent = getIntent();
        movieName = intent.getStringExtra("movieName");
        scheduleID = intent.getIntExtra("scheduleID",1);
        TextView topicSeatTXT = findViewById(R.id.topic_seat);
        topicSeatTXT.setText(movieName);

        seatTableView = (SeatTable) findViewById(R.id.seatView);
        seatTableView.setMaxSelected(1);//设置最多选中

        checked_columns = new ArrayList<Integer>();
        checked_rows = new ArrayList<Integer>();
        Button button_next = findViewById(R.id.next_seat);
        button_next.setOnClickListener(new MyOnClickListener());
        findViewById(R.id.back_seat).setOnClickListener(new MyOnClickListener());

        new Thread() {
            public void run() {
                try {
                    String detail_all = ConAPI.findOrderDetailByScheduleID(scheduleID);
                    JSONObject obj = new JSONObject(detail_all);
                    JSONArray data = obj.getJSONArray("data");
                    for (int i = 0; i < data.length(); i++) {
                        JSONObject data_obj = data.getJSONObject(i);
                        int seat_row = data_obj.getInt("seat_row");
                        int seat_column = data_obj.getInt("seat_column");
                        booked_rows.add(seat_row);
                        booked_columns.add(seat_column);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    String detail_all = ConAPI.findHallNameByScheduleID(scheduleID);
                    Message msg_layout = Message.obtain();
                    msg_layout.what = 3;
                    msg_layout.obj = detail_all;
                    handler.sendMessage(msg_layout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        new Thread() {
            public void run() {
                try {
                    String detail_all = ConAPI.isVIPHall(scheduleID);
                    isVIPHall = Integer.parseInt(detail_all);
                    seatTableView.isVIP = isVIPHall;
                    Message msg_layout = Message.obtain();
                    msg_layout.what = 4;
                    msg_layout.obj = detail_all;
                    handler.sendMessage(msg_layout);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }.start();

        seatTableView.setSeatChecker(new SeatTable.SeatChecker() {
            @Override
            public boolean isValidSeat(int row, int column) {
                if(isVIPHall == 1 && column % 2 == 1){
                    return false;
                }
                return true;
            }

            @Override
            public boolean isSold(int row, int column) {
                for(int i=0;i < booked_rows.size();i++){
                    if(booked_rows.get(i) == row && booked_columns.get(i) == column){
                        return true;
                    }
                }
                return false;
            }

            @Override
            public void checked(int row, int column) {
                checked_rows.add(row);
                checked_columns.add(column);
            }

            @Override
            public void unCheck(int row, int column) {
                for(int i=0;i < checked_rows.size();i++){
                    if(checked_rows.get(i) == row && checked_columns.get(i) == column){
                        checked_rows.remove(i);
                        checked_columns.remove(i);
                        return;
                    }
                }
            }

            @Override
            public String[] checkedSeatTxt(int row, int column) {
                return null;
            }

        });
        seatTableView.setData(10,15);

    }



}
