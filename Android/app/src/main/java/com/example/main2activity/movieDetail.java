package com.example.main2activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class movieDetail extends AppCompatActivity {

    int movieJSONID;
    int dateID;
    String name;
    String blurb;
    String director;
    String actors;
    String certificate;
    Bitmap picture;
    Bitmap poster;
    int movie_id;
    int user_age;
    String scheduleId_str;
    int scheduleId;
    JSONArray schedules;
    String currentDate = "";
    ArrayList<String> timeList;
    ArrayList<String> dateList;
    ArrayList<String> scheduleList;
    ArrayList<String> priceList;
    TextView blurbTXT;

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if (msg.what == 1){
                try{
                    TextView certificateTXT = findViewById(R.id.textView10);
                    certificateTXT.setText("imdb编码: " + certificate);

                    TextView directorTXT = findViewById(R.id.textView8);
                    directorTXT.setText("导演: " + director);

                    TextView actorsTXT = findViewById(R.id.textView9);
                    actorsTXT.setText("主演: " + actors);

                    blurbTXT = findViewById(R.id.textView7);
                    blurbTXT.setText(blurb);

                    TextView nameTXT = findViewById(R.id.textView);
                    nameTXT.setText(name);

                    ImageView posterImage = findViewById(R.id.posterView);
                    posterImage.setVisibility(View.VISIBLE);
                    posterImage.setImageBitmap(poster);


                    Map<String,Integer> checkList = new HashMap<String,Integer>();
                    dateList = new ArrayList<String>();
                    for(int i=0;i<schedules.length();i++){
                        JSONObject current = schedules.getJSONObject(i);
                        String current_date = current.getString("date");
                        if(!checkList.containsKey(current_date)){
                            checkList.put(current_date,1);
                            dateList.add(current_date);
                        }
                    }

                    dateLayout();
                    currentDate = dateList.get(dateID);
                    timeList = new ArrayList<String>();
                    scheduleList = new ArrayList<String>();
                    priceList = new ArrayList<String>();

                    for(int i=0;i<schedules.length();i++){
                        JSONObject current = schedules.getJSONObject(i);
                        if(current.getString("date").equals(currentDate)){
                            String time = current.getString("start_time") + "-" + current.getString("end_time");
                            scheduleList.add(current.getString("schedule_id"));
                            priceList.add(current.getString("price"));
                            timeList.add(time);
                        }
                    }
                    timeLayout();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
            else if (msg.what == 2){

                Toast.makeText(movieDetail.this, "未满16岁不能观看该电影", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(movieDetail.this, allmovie.class);
                startActivityForResult(login, 0);
            }
            else if (msg.what == 3){
                Intent movie_intent = new Intent(movieDetail.this, selectSeat.class);
                movie_intent.putExtra("movieName",name);
                movie_intent.putExtra("scheduleID",scheduleId);
                startActivityForResult(movie_intent, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_detail);

        findViewById(R.id.big_font).setOnClickListener(new FontOnClickListener());
        findViewById(R.id.middle_font).setOnClickListener(new FontOnClickListener());
        findViewById(R.id.small_font).setOnClickListener(new FontOnClickListener());

        ImageButton back_movie = findViewById(R.id.back_movie);
        back_movie.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent movie_intent = new Intent(movieDetail.this, allmovie.class);
                startActivityForResult(movie_intent, 0);
            }
        });

        Intent intent = getIntent();
        //第二个参数表示没有接收到的时候 给的默认值
        movieJSONID = intent.getIntExtra("movieJSONID",0);
        dateID = intent.getIntExtra("dateID",0);

        new Thread() {
            public void run() {

                try{
                    String detail_all = ConAPI.getAllMovies();
                    System.out.println("all movie detail is: " + detail_all);
                    if (!TextUtils.isEmpty(detail_all)){
                        JSONObject obj = new JSONObject(detail_all);
                        JSONArray data = obj.getJSONArray("data");
                        JSONObject current = data.getJSONObject(movieJSONID);

                        movie_id = current.getInt("movie_id");
                        name = current.getString("name");
                        blurb = current.getString("blurb");
                        director = current.getString("director");
                        actors = current.getString("actors");
                        certificate = current.getString("certificate");
//                        byte[] pictureByte = ConAPI.getImage(current.getString("picture"));
//                        picture = BitmapFactory.decodeByteArray(pictureByte, 0, pictureByte.length);
                        byte[] posterByte = ConAPI.getImage(current.getString("poster"));
                        poster = BitmapFactory.decodeByteArray(posterByte, 0, posterByte.length);
                    }
                    String current_schedule = ConAPI.findSchedule(movie_id);
                    //System.out.println("movie id: " + movie_id + ". current schedule: " + current_schedule);
                    if (!TextUtils.isEmpty(current_schedule)){
                        JSONObject obj = new JSONObject(current_schedule);
                        schedules = obj.getJSONArray("data");
                    }
                    else{
                        System.out.println("empty");
                    }
                }catch (Exception e){
                    System.out.println("error movie detail");
                    e.printStackTrace();
                }

                handler.sendEmptyMessage(1);
            }
        }.start();
    }

    private void dateLayout(){

        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.dateLayout);

        for(int i = 0; i < dateList.size(); i++){
            Button button = new Button(this);

            button.setText(dateList.get(i));
            button.setTextSize(15);
            button.setTextColor(getResources().getColor(R.color.colorDarkGray));
            button.setBackground(getResources().getDrawable(R.drawable.shape_white_button));
            button.setId(i);
            button.setOnClickListener(new DateOnClickListener());

            LinearLayout.LayoutParams button_parent_params
                    = new LinearLayout.LayoutParams(300, LinearLayout.LayoutParams.WRAP_CONTENT);

            button_parent_params.setMargins(0,0,10,0);

            rootLayout.addView(button,button_parent_params);
        }
        findViewById(dateID).setBackground(getResources().getDrawable(R.drawable.shape_light_dark));
        Button current = findViewById(dateID);
        current.setTextColor(Color.WHITE);
    }

    private void timeLayout(){

        if(! timeList.isEmpty()){
            int size = timeList.size();
            LinearLayout rootLayout = (LinearLayout)findViewById(R.id.timeLayout);
            for(int i = 0; i < size; i++){
                RelativeLayout buttonLayout = new RelativeLayout(this);
                Button button = new Button(this);
                button.setId(i);
                button.setOnClickListener(new MyOnClickListener());

                button.setText(timeList.get(i));
                button.setTextSize(17);
                button.setTextColor(getResources().getColor(R.color.colorDarkGray));
                button.setBackgroundColor(Color.parseColor("#ddffffff"));


                LinearLayout.LayoutParams buttonLayout_parent_params
                        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                RelativeLayout.LayoutParams button_parent_params
                        = new RelativeLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 200);

                button_parent_params.addRule(RelativeLayout.CENTER_IN_PARENT);
                buttonLayout.setPadding(0,0,0,10);

                LinearLayout.LayoutParams rootLayout_parent_params
                        = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                //rootLayout.setLayoutParams(rootLayout_parent_params);

                buttonLayout.addView(button,button_parent_params);
                rootLayout.addView(buttonLayout,buttonLayout_parent_params);
            }
        }
    }

    class DateOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){// 点击事件的处理方法
            Intent intent =new Intent(movieDetail.this,movieDetail.class);
            intent.putExtra("dateID",v.getId());
            intent.putExtra("movieJSONID",movieJSONID);
            startActivity(intent);
        }
    }

    class MyOnClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v){ // 点击事件的处理方法
            scheduleId_str = scheduleList.get(v.getId());
            new Thread() {
                public void run() {

                    try{
                        scheduleId = Integer.parseInt(scheduleId_str);
                        String detail_all = ConAPI.getCurrentUser();
                        if (!detail_all.equals("NEED_LOGIN") ){
                            JSONObject obj1 = new JSONObject(detail_all);
                            user_age = obj1.getInt("age");;
                            if(user_age < 16 && certificate.equals("tt0120338")){
                                handler.sendEmptyMessage(2);
                            }
                            else{
                                handler.sendEmptyMessage(3);
                            }
                        }
                        else{
                            handler.sendEmptyMessage(3);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
    }

    class FontOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) { // 点击事件的处理方法
            if (v.getId() == R.id.big_font) {
                blurbTXT.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
            }
            else if (v.getId() == R.id.middle_font) {
                blurbTXT.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
            }
            else if (v.getId() == R.id.small_font) {
                blurbTXT.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18); //getResources().getDimension(R.dimen.small_font)
            }
        }
    }
}
