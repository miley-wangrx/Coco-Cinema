package com.example.main2activity.PayClass;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;

import com.example.main2activity.ConAPI;
import com.example.main2activity.R;
import com.example.main2activity.afterPay;
import com.example.main2activity.openVIP;
import com.example.main2activity.userCenter;

import java.util.ArrayList;


public class CustomDialog extends DialogFragment {

    private PasswordView pwdView;
    public static ArrayList<Integer> order_id;
    public int is_pay_for_vip = 0;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Dialog dialog = new Dialog(getActivity(), R.style.Dialog);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCanceledOnTouchOutside(true);
        /**设置宽度为屏宽、靠近屏幕底部*/
        Window window = dialog.getWindow();
        /**设置背景透明*/
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        WindowManager.LayoutParams wlp = window.getAttributes();
        wlp.gravity = Gravity.BOTTOM;
        dialog.setContentView(R.layout.dialog_normal);
        wlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        wlp.height = (int) getResources().getDimension(R.dimen.custom_hei);
        window.setAttributes(wlp);
        initViews(dialog);
        return dialog;
    }

    private void initViews(Dialog dialog) {
        pwdView = dialog.findViewById(R.id.pwd_view);
        pwdView.setOnFinishInput(new PasswordView.OnPasswordInputFinish() {
            @Override
            public void inputFinish() {
                if(is_pay_for_vip == 0){
                    new Thread() {
                        public void run() {
                            try {
                                for(int i=0;i < order_id.size();i++){
                                    String checked_msg = ConAPI.payOrder(order_id.get(i));
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }.start();
                    //Toast.makeText(getActivity(), pwdView.getStrPassword(), Toast.LENGTH_SHORT).show();
                    Toast.makeText(getActivity(), "支付成功", Toast.LENGTH_SHORT).show();
                    Intent logout = new Intent(getActivity(), afterPay.class);
                    startActivityForResult(logout, 0);
                }
                else{
                    Intent intent = new Intent(getActivity(), userCenter.class);
                    intent.putExtra("operation",1);
                    Toast.makeText(getActivity(), "开通成功", Toast.LENGTH_SHORT).show();
                    startActivityForResult(intent, 0);
                }
            }
        });


    }
}
