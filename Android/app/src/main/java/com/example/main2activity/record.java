package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.main2activity.PayClass.CustomDialog;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class record extends AppCompatActivity {

    int len = -1;
    ArrayList<String> movieName = new ArrayList<String>();
    ArrayList<String> actors = new ArrayList<String>();
    ArrayList<Bitmap> bmps = new ArrayList<Bitmap>();
    ArrayList<Integer> prices = new ArrayList<Integer>();
    ArrayList<String> numbers = new ArrayList<String>();
    ArrayList<String> payments = new ArrayList<String>();
    ArrayList<Integer> paids = new ArrayList<Integer>();
    ArrayList<Integer> order_ids = new ArrayList<Integer>();
    String user_card;
    private ScrollView scrollView;
    private Button toTopBtn;
    private View contentView;
    private int scrollY = 0;// 标记上次滑动位置
    private final String TAG = "kk123456789";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                try {
                    recordLayout();
                } catch (Exception e) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(record.this);
                    builder.setTitle("Error!");
                    String s0 = e.toString();
                    builder.setMessage("Fail to make layout. " + s0);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
            else if(msg.what == 2){
                Toast.makeText(record.this, "请先登录", Toast.LENGTH_SHORT).show();
                Intent login = new Intent(record.this, welcome.class);
                startActivityForResult(login, 0);
            }
        }
    };

    /**
     * 初始化视图
     */
    private void initView() {
        scrollView = (ScrollView) findViewById(R.id.scroll_record);
        if (contentView == null) {
            contentView = scrollView.getChildAt(0);
        }

        toTopBtn = (Button) findViewById(R.id.top_btn_record);
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
        setContentView(R.layout.activity_record);

        ImageButton back_login = findViewById(R.id.back_record);
        back_login.setOnClickListener(new MyOnClickListener());

        initView();

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
                    Message msg_layout = Message.obtain();
                    String detail_all = ConAPI.getUserRecords();
                    if (!TextUtils.isEmpty(detail_all) && !detail_all.equals("NEED_LOGIN") ){
                        System.out.println("record detail is " + detail_all);
                        JSONObject obj = new JSONObject(detail_all);
                        JSONArray data = obj.getJSONArray("data");
                        len = data.length();
                        movieName.clear();
                        bmps.clear();
                        prices.clear();
                        numbers.clear();
                        payments.clear();
                        paids.clear();
                        order_ids.clear();
                        for(int i = 0; i < len; i++){
                            JSONObject data_obj = data.getJSONObject(i);
                            String name = data_obj.getString("movie_name");
                            String actor = data_obj.getString("actors");
                            String pic = data_obj.getString("picture");
                            int price = data_obj.getInt("price");
                            String pay_method = data_obj.getString("pay_method");
                            String order_number = data_obj.getString("order_number");
                            int paid = data_obj.getInt("paid");
                            int order_id = data_obj.getInt("order_id");
                            byte[] image = ConAPI.getImage(pic);
                            Bitmap bmp = BitmapFactory.decodeByteArray(image, 0, image.length);
                            movieName.add(name);
                            actors.add(actor);
                            bmps.add(bmp);
                            prices.add(price);
                            numbers.add(order_number);
                            payments.add(pay_method);
                            paids.add(paid);
                            order_ids.add(order_id);
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

    private void recordLayout(){
        String price_string = "";
        try{
            for(int i = 0; i < len; i++){
                LinearLayout rootLayout = (LinearLayout)findViewById(R.id.root_record);

                // 图片
                ImageButton image = new ImageButton(this);
                Bitmap bitmap = bmps.get(i);
                image.setVisibility(View.VISIBLE);
                image.setImageBitmap(bitmap);
                RelativeLayout.LayoutParams image_parent_params
                        = new RelativeLayout.LayoutParams(360, 480);
                image_parent_params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                //image_parent_params.addRule(RelativeLayout.CENTER_VERTICAL);
                image.setScaleType(ImageButton.ScaleType.FIT_CENTER);
                image.setBackgroundColor(Color.WHITE);
                image.setId(100 + i);

                // 电影名
                TextView name = new TextView(this);
                name.setText(movieName.get(i));
                name.setTextSize(22);
                name.setTextColor(getResources().getColor(R.color.colorDarkGray));
                name.getPaint().setFakeBoldText(true);
                name.setId(1100 + i);
                RelativeLayout.LayoutParams text_parent_params
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                text_parent_params.setMargins(20, 20, 0, 0);
                text_parent_params.addRule(RelativeLayout.RIGHT_OF,100+i);

                // 主演
                TextView actor = new TextView(this);
                actor.setText("Actors：" + actors.get(i));
                actor.setTextSize(17);
                //actor.setTextColor(Color.BLACK);
                actor.setId(2100 + i);
                RelativeLayout.LayoutParams actor_parent_params
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                actor_parent_params.setMargins(0, 10, 0, 0);
                actor_parent_params.addRule(RelativeLayout.BELOW,1100+i); // 位置要相对于电影名的位置设置因为电影名可能有多行
                actor_parent_params.addRule(RelativeLayout.ALIGN_LEFT, 1100+i);

                // 价格
                TextView price = new TextView(this);
                price_string = "Price: $" + prices.get(i);
                price.setText(price_string);
                price.setTextSize(17);
                RelativeLayout.LayoutParams price_parent_params
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                price_parent_params.setMargins(0, 170, 0, 0);
                price_parent_params.addRule(RelativeLayout.BELOW,1100+i); // 位置要相对于主演的位置设置因为主演可能有多行
                price_parent_params.addRule(RelativeLayout.ALIGN_LEFT, 1100+i);

                // 支付方式
                TextView payment = new TextView(this);
                payment.setText("Payment Pattern: " + payments.get(i));
                payment.setTextSize(17);
                RelativeLayout.LayoutParams payment_parent_params
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                payment_parent_params.setMargins(0, 230, 0, 0);
                payment_parent_params.addRule(RelativeLayout.BELOW,1100+i);
                payment_parent_params.addRule(RelativeLayout.ALIGN_LEFT, 1100+i);

                //订单号码
                TextView number = new TextView(this);
                number.setText("Order Number：" + numbers.get(i));
                number.setTextSize(17);
                RelativeLayout.LayoutParams number_parent_params
                        = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.WRAP_CONTENT);
                number_parent_params.setMargins(0, 290, 0, 0);
                number_parent_params.addRule(RelativeLayout.BELOW,1100+i);
                number_parent_params.addRule(RelativeLayout.ALIGN_LEFT, 1100+i);



                // 相对布局
                RelativeLayout relative = new RelativeLayout(this);
                //relative.setBackground();
                relative.setBackgroundResource(R.drawable.shape_white_board);
                LinearLayout.LayoutParams relativeLayout_parent_params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                relativeLayout_parent_params.setMargins(0,50,0,0);

                // 添加视图
                relative.addView(name, text_parent_params);
                relative.addView(actor, actor_parent_params);
                relative.addView(image, image_parent_params);
                relative.addView(price, price_parent_params);
                relative.addView(payment, payment_parent_params);
                relative.addView(number, number_parent_params);
//                relative.addView(button,button_parent_params);

                if ( paids.get(i) == 0){
                    // 支付按钮
                    Button button = new Button(this);
                    //button.setCompoundDrawables(draw, null, null, null);
                    button.setText("Pay");
                    button.setId(order_ids.get(i));
                    button.setTextColor(Color.WHITE);
                    button.setOnClickListener(new ButtonOnClickListener());
                    //button.setBackgroundColor(getResources().getColor(R.color.colorLemmonRed));
                    //button.setBackgroundResource(R.drawable.shape_red_filling);
                    button.setBackground(getResources().getDrawable(R.drawable.shape_red_filling));
                    button.setTextSize(13);
                    //button.setTextColor(Color.GRAY);
                    //button.setBackgroundColor(Color.WHITE);
                    RelativeLayout.LayoutParams button_parent_params
                            = new RelativeLayout.LayoutParams(170,100);
                    button_parent_params.setMargins(0,380,0,0);
                    button_parent_params.addRule(RelativeLayout.BELOW,1100+i);
                    button_parent_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    relative.addView(button,button_parent_params);
                }
                else{
                    // 已支付
                    Button paid = new Button(this);
                    paid.setText("Paid √");
                    paid.setTextSize(13);
                    paid.setTextColor(getResources().getColor(R.color.colorRed));
                    paid.setBackgroundResource(R.drawable.shape_red_border);
                    RelativeLayout.LayoutParams paid_parent_params
                            = new RelativeLayout.LayoutParams(170,100);
                    paid_parent_params.setMargins(0, 380, 0, 0);
                    paid_parent_params.addRule(RelativeLayout.BELOW,1100+i);
                    paid_parent_params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                    relative.addView(paid,paid_parent_params);
                }

                rootLayout.addView(relative,relativeLayout_parent_params);
            }
        }catch(Exception e){
            System.out.println("bmps size " + bmps.size());
            System.out.println("price size" + prices.size());
            System.out.println("payment size" + payments.size());
            System.out.println("number size" + numbers.size());
            e.printStackTrace();
        }
    }



    class MyOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {

            if (v.getId() == R.id.back_record) { //返回
                Intent movie_intent = new Intent(record.this, userCenter.class);
                startActivityForResult(movie_intent, 0);
            }
            else if (v.getId() == R.id.top_btn_record){

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

    class ButtonOnClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if(user_card.equals("NO_CARD")){
                Toast.makeText(record.this, "请先绑定银行卡", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(record.this, card.class);
                startActivityForResult(intent, 0);
            }
            else{
                CustomDialog customDialog = new CustomDialog();
                CustomDialog.order_id = new ArrayList<Integer>();
                CustomDialog.order_id.add(v.getId());
                customDialog.show(getSupportFragmentManager(), "");
            }

        }
    }
}
