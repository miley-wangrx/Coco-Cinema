package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import static android.app.AlertDialog.THEME_HOLO_LIGHT;

public class allmovie extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener, DatePickerDialog.OnDateSetListener {

    private ScrollView scrollView;
    private Button toTopBtn;
    private View contentView;
    private int scrollY = 0;// 标记上次滑动位置
    private final String TAG = "kk123456789";
    EditText search_content;
    TextView search_date;
    String date;
    int isDate = 0;

    ArrayList<String> names = new ArrayList<String>();
    ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
    private String[] optionArray = {"Keyword", "Date"};
    int len = -1;
    String search_info = "";

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){
            if(msg.what == 1){
                try{
                    //temp_resource_array();
                    int pxWid = getScreenWidth(allmovie.this);
                    int marginWid = dip2px(allmovie.this, 10);
                    addMovies(len, (pxWid - marginWid)/ 2);
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(allmovie.this);
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to make layout. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else if(msg.what == 2){
                Intent user_center = new Intent(allmovie.this, userCenter.class);
                startActivityForResult(user_center, 0);
            }
            else if(msg.what == 3){
                Toast.makeText(allmovie.this, "请先登录", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(allmovie.this, welcome.class);
                startActivityForResult(login, 0);
            }
        }
    };

    private void setScreen(){
        setContentView(R.layout.activity_allmovie);

        ImageButton user_center = findViewById(R.id.center_button);
        user_center.setOnClickListener(this);

        initSpinner();

        Button search = findViewById(R.id.button3);
        search.setOnClickListener(this);

        search_date = findViewById(R.id.search_date);
        search_date.setOnClickListener(this);

        search_content = findViewById(R.id.editText);
        search_content.addTextChangedListener(new TextWatcher(){

            @Override
            public void afterTextChanged(Editable s) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                search_info = s.toString();
                isDate = 0;
            }

        });
    }

    private void initSpinner(){
        ArrayAdapter<String> optionAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, optionArray);
        //optionAdapter.setDropDownViewResource();
        Spinner sp = findViewById(R.id.search_option);
        sp.setAdapter(optionAdapter);
//        sp.setPrompt("Option");
//        sp.setSelection(0);
        sp.setOnItemSelectedListener(this);
    }

        @Override
        public void onItemSelected(AdapterView<?>parent, View arg1, int position, long id){
            //System.out.println("enter onItemSelected");
            //String content = parent.getItemAtPosition(position).toString();
            switch (position){
                case 0:  //keyword
                    search_content.setVisibility(View.VISIBLE);
                    search_date.setVisibility(View.GONE);
                    break;
                case 1:  //date
                    search_content.setVisibility(View.GONE);
                    search_date.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }
        @Override
        public void onNothingSelected(AdapterView<?>arg0){}


    // 获取屏幕宽度
    public static int getScreenWidth(Context ctx){
        WindowManager wm = (WindowManager)ctx.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public static int dip2px(Context context, float dpValue){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int)(dpValue * scale + 0.5f);
    }

    /**
     * 初始化视图
     */
    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scroll_all);
        if (contentView == null) {
            contentView = scrollView.getChildAt(0);
        }

        toTopBtn = (Button) findViewById(R.id.top_btn);
        toTopBtn.setOnClickListener(this);
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
        } else  {
            toTopBtn.setVisibility(View.VISIBLE);
            Log.i(TAG, "test");
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setScreen();
        initView();
/*
        new Thread(){
            public void run(){
                try{
                    String detail = ConAPI.login("13086618316", "123");
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
*/

        new Thread(){

            public void run(){

                try{
                    String detail_all = ConAPI.getAllMovies();
                    System.out.println("all movie detail is: " + detail_all);
                    if (!TextUtils.isEmpty(detail_all)){
                        JSONObject obj = new JSONObject(detail_all);
                        JSONArray data = obj.getJSONArray("data");
                        len = data.length();
                        names.clear();
                        bmps.clear();
                        for(int i = 0; i < len; i++){
                            JSONObject data_obj = data.getJSONObject(i);
                            String name = data_obj.getString("name");
                            String pic = data_obj.getString("picture");
                            System.out.println("Picture:" + pic);
                            byte[] image = ConAPI.getImage(pic);
                            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                            names.add(name);
                            bmps.add(bmp);
                        }

                    }
                }catch (Exception e){
                    System.out.println("error all movie");
                    e.printStackTrace();
                }


                Message msg_layout = Message.obtain();
                msg_layout.what = 1;
                handler.sendMessage(msg_layout);
            }
        }.start();
    }

    class WelOnClickListener implements ImageButton.OnClickListener{

        @SuppressLint("ResourceType")
        @Override
        public void onClick(View v) { // 点击事件的处理方法
            Intent intent =new Intent(allmovie.this,movieDetail.class);
            intent.putExtra("movieJSONID",v.getId());
            startActivity(intent);
        }
    }


    private void addMovies(int len, int wid){

        wid -= 35;
        LinearLayout rootLayout = (LinearLayout)findViewById(R.id.root_layout);
        int hei = wid / 3 * 4;
        String text = "movie1";

        int posterNum = 0;
        for(int i = 0; i < len-1; i = i + 2){
            // 每两个水平的电影添加到一个水平布局
            LinearLayout horizontal = new LinearLayout(this);
            LinearLayout.LayoutParams horizontal_parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            //horizontal_parent_params.setMargins(10,0,10,0);
            for(int j = 0; j < 2; j++) {

                // 每个电影的布局
                LinearLayout together = new LinearLayout(this);
                together.setOrientation(LinearLayout.VERTICAL);
                LinearLayout.LayoutParams together_parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);

                // 图片
                ImageButton image = new ImageButton(this);
                image.setBackgroundColor(Color.parseColor("#00ffffff"));

                Bitmap bitmap = bmps.get(i+j);
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(bitmap);
                image.setId(posterNum);
                image.setOnClickListener(new WelOnClickListener());
                posterNum += 1;

                //图片参数设置
                RelativeLayout.LayoutParams image_parent_params
                        = new RelativeLayout.LayoutParams(wid, hei);

                //图片在图片布局中居中
                image_parent_params.addRule(RelativeLayout.CENTER_IN_PARENT);

                together.addView(image,image_parent_params);

                // 图片适应布局尺寸
                image.setScaleType(ImageButton.ScaleType.FIT_CENTER);

                //文字

                //文字参数
                RelativeLayout.LayoutParams text_parent_params
                        = new RelativeLayout.LayoutParams(wid, RelativeLayout.LayoutParams.WRAP_CONTENT);

                TextView textView = new TextView(this);

                textView.setText(names.get(i+j));
                textView.getPaint().setFakeBoldText(true);
                //文字在文字布局中居中
                textView.setGravity(Gravity.CENTER_HORIZONTAL);
                textView.setTextSize(20);
                textView.setTextColor(getResources().getColor(R.color.colorDarkGray));

                together.addView(textView, text_parent_params);

                //电影布局加入水平布局
                horizontal.addView(together, together_parent_params);
            }
            //水平布局加入总布局
            rootLayout.addView(horizontal, horizontal_parent_params);
        }

        if(len % 2 == 1){
            LinearLayout together = new LinearLayout(this);
            together.setOrientation(LinearLayout.VERTICAL);
            LinearLayout.LayoutParams together_parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);


            // image
            ImageButton image = new ImageButton(this);
            image.setBackgroundColor(Color.WHITE);

            Bitmap bitmap = bmps.get(len-1);
            image.setVisibility(View.VISIBLE);
            image.setImageBitmap(bitmap);
            image.setId(posterNum);
            image.setOnClickListener(new WelOnClickListener());
            posterNum += 1;

            RelativeLayout.LayoutParams image_parent_params
                    = new RelativeLayout.LayoutParams(wid, hei);

            // add image to image layout
            together.addView(image, image_parent_params);

            // adjust image
            image.setScaleType(ImageButton.ScaleType.FIT_CENTER);
            image.setBackgroundColor(Color.parseColor("#00ffffff"));

            //text
            TextView textView = new TextView(this);

            text = names.get(len-1);
            textView.setText(text);

            //文字参数
            RelativeLayout.LayoutParams text_parent_params
                    = new RelativeLayout.LayoutParams(wid, RelativeLayout.LayoutParams.WRAP_CONTENT);

            //文字在文字布局中居中
            textView.setGravity(Gravity.CENTER_HORIZONTAL);
            textView.setTextSize(20);
            textView.setTextColor(getResources().getColor(R.color.colorDarkGray));

            together.addView(textView, text_parent_params);

            // add to rootLayout
            rootLayout.addView(together, together_parent_params);
        }
    }

    @Override
    public void onClick(View v){ // 点击事件的处理方法

        if (v.getId() == R.id.center_button){
            new Thread(){

                public void run(){

                    try{
                        Message msg_layout = Message.obtain();
                        String detail_all = ConAPI.getCurrentUser();
                        if (!TextUtils.isEmpty(detail_all) && !detail_all.equals("NEED_LOGIN") ){
                            msg_layout.what = 2;
                            handler.sendMessage(msg_layout);
                        }
                        else if(detail_all.equals("NEED_LOGIN")){
                            msg_layout.what = 3;
                            handler.sendMessage(msg_layout);
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        else if(v.getId() == R.id.button3){
            setScreen();

            new Thread(){
                public void run(){
                    try{
                        String detail_all;
                        if(isDate == 1){
                            detail_all = ConAPI.searchMovieByDate(date);
                        }
                        else{
                            detail_all = ConAPI.searchMovie(search_info);
                        }
                        if (!TextUtils.isEmpty(detail_all)){
                            JSONObject obj = new JSONObject(detail_all);
                            JSONArray data = obj.getJSONArray("data");
                            len = data.length();
                            names.clear();
                            bmps.clear();
                            for(int i = 0; i < len; i++){
                                JSONObject data_obj = data.getJSONObject(i);
                                String name = data_obj.getString("name");
                                String pic = data_obj.getString("picture");
                                System.out.println("Picture:" + pic);
                                byte[] image = ConAPI.getImage(pic);
                                Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                                names.add(name);
                                bmps.add(bmp);
                            }

                        }
                        Message msg_layout = Message.obtain();
                        msg_layout.what = 1;
                        handler.sendMessage(msg_layout);
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
            }.start();
        }
        else if (v.getId() == R.id.top_btn){

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
        else if (v.getId() == R.id.search_date) {
            Calendar calendar = Calendar.getInstance();

            DatePickerDialog dialog = new DatePickerDialog(this, THEME_HOLO_LIGHT,this,
                    calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)
            );
//            DatePicker datePicker = dialog.getDatePicker();
//            //datePicker.setMaxDate(new Date().getTime());//设置日期的上限日期
//            datePicker.setMinDate(new Date().getTime());//设置日期的下限日期，其中是参数类型是long型，为日期的时间戳
            dialog.show();

        }
    }
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){

        date = String.format("%d-%02d-%02d", year, monthOfYear+1, dayOfMonth);
        search_date.setText(date);
        isDate = 1;
    }
}


