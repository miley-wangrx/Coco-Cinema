package com.example.main2activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Date;

import static android.app.AlertDialog.THEME_DEVICE_DEFAULT_LIGHT;
import static android.app.AlertDialog.THEME_HOLO_LIGHT;


public class register extends AppCompatActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    //EditText et_phone;
    EditText et_psw1;
    EditText et_psw2;
    String birthday;
    String psw1 = "";
    String psw2 = "";
    private TextView tv_date;
    private String phone_number = "";

    private Handler handler = new Handler(){
        public void handleMessage(Message msg){

            if(msg.what == 2){ //注册
                try{
                    if(msg.obj.equals("right")){
                        Intent intent = new Intent(register.this, login.class);
                        startActivityForResult(intent, 0);
                    }
                    else{
                        AlertDialog.Builder builder = new AlertDialog.Builder(register.this);
                        builder.setMessage(msg.obj.toString());
                        builder.setPositiveButton("Ok.", null);
                        AlertDialog alert = builder.create();
                        alert.show();
                    }
                }catch (Exception e){
                    AlertDialog.Builder builder = new AlertDialog.Builder(register.this);
                    String s1 = e.toString();
                    builder.setMessage("Register wrong. " + s1);
                    builder.setPositiveButton("Ok.", null);
                    AlertDialog alert = builder.create();
                    alert.show();
                }
            }
        }
    };

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button register_button = findViewById(R.id.register);
        register_button.setOnClickListener(register.this);

        ImageButton back_regist = findViewById(R.id.back_regist);
        back_regist.setOnClickListener(this);

        //et_phone = findViewById(R.id.edit_phone_register);
        et_psw1 = findViewById(R.id.edit_password_register);
        et_psw2 = findViewById(R.id.edit_text_password_confir);
        tv_date = findViewById(R.id.birthday);
        findViewById(R.id.pick_birthday).setOnClickListener(this);

        Intent intent = getIntent();
        phone_number = intent.getStringExtra("phone");
    }

    public String checkPsw(String psw){

        int num = 0;
        int upper = 0;
        int lower = 0;

        for(int i = 0; i < psw.length(); i++){
            if (Character.isDigit(psw.charAt(i))){
                num = 1;
            }
            else if (Character.isUpperCase(psw.charAt(i))){
                upper = 1;
            }
            else if (Character.isLowerCase(psw.charAt(i))){
                lower = 1;
            }
        }
        if (psw.length() < 6 || psw.length() > 18)
            return "密码应为6-18位";
        else if (num == 0 || upper == 0 || lower == 0)
            return "密码至少包含一个数字，一个大写字母，和一个小写字母";
        else
            return "right";
    }

        public void onClick(View v){ // 点击事件的处理方法

            if (v.getId() == R.id.register){ //判断是否是register_button被点击
                new Thread(){
                    public void run(){
                        String detail_register = " ";
                        int code = 5;
                        boolean pass = false;
                        String check_result = "";
                        try{
                            //String phone_number = et_phone.getText().toString();
                            psw1 = et_psw1.getText().toString();
                            psw2 = et_psw2.getText().toString();
                            check_result = checkPsw(psw1);
                            if(check_result.equals("right")){
                                pass = true;
                                detail_register = ConAPI.register(phone_number,birthday, psw1, psw2);
                                System.out.println(detail_register);
                                if (!TextUtils.isEmpty(detail_register)) {
                                    JSONObject obj = new JSONObject(detail_register);
                                    code = obj.getInt("code");
                                }
                                else{
                                    System.out.println("detail_register empty");
                                }
                            }
                        }catch(Exception e){
                            check_result = "Fail to check the password.";
                            e.printStackTrace();
                        }
                        Message msg_register = Message.obtain();
                        msg_register.what = 2;

                        if (pass){
                            if (birthday == null){
                                msg_register.obj = "请选择生日";
                            }
                            else if (!psw1.equals(psw2)){
                                msg_register.obj = "确认密码失败";
                            }
                            else if(code == 1){
                                msg_register.obj = "只支持使用POST方法请求";
                            }
                            else if(code == 2){
                                msg_register.obj = "输入不合法";
                            }
                            else if(code == 0){
                                msg_register.obj = "right";
                            }
                            else if(code == 3){
                                msg_register.obj = "这个手机号已经注册过";
                            }
                            else if(code == 4){
                                msg_register.obj = "生日不合法";
                            }
                            else{
                                msg_register.obj = "Error about register.";
                                System.out.println("register " + code);
                            }
                        }
                        else{
                            msg_register.obj = check_result;
                        }
                        handler.sendMessage(msg_register);
                    };
                }.start();
            }
            if (v.getId() == R.id.pick_birthday){
                Calendar calendar = Calendar.getInstance();

                DatePickerDialog dialog = new DatePickerDialog(this, THEME_HOLO_LIGHT,this,1990,0,01
                        );
                DatePicker datePicker = dialog.getDatePicker();
                datePicker.setMaxDate(new Date().getTime());//设置日期的上限日期
                //datePicker.setMinDate(...);//设置日期的下限日期，其中是参数类型是long型，为日期的时间戳
                dialog.show();
            }
            if (v.getId() == R.id.back_regist){
                Intent back_intent = new Intent(register.this, welcome.class);
                startActivityForResult(back_intent, 0);
            }
        }

        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth){

            birthday = String.format("%d-%d-%d", year, monthOfYear+1, dayOfMonth);
            tv_date.setText(birthday);
        }
}


