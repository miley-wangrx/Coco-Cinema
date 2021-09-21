package com.example.main2activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class editCard extends AppCompatActivity implements View.OnClickListener {

    int operation = 0;
    TextView card;
    String card_number = "";
    String new_card_number = "";

    private Handler handler = new Handler() {
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Intent intent = new Intent(editCard.this, card.class);
                startActivityForResult(intent, 0);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_card);

        Button button = findViewById(R.id.next_edit_card);
        button.setOnClickListener(this);

        findViewById(R.id.back_edit_card).setOnClickListener(this);

        card = findViewById(R.id.edit_card_text);

        Intent intent = getIntent();
        operation = intent.getIntExtra("operation",0);
        if(operation == 1){
            card_number = intent.getStringExtra("card_number");
            card.setText(card_number);
        }
    }

    public void onClick(View v){
        if (v.getId() == R.id.next_edit_card){
            new_card_number = card.getText().toString();
            new Thread(){
                public void run(){
                    try{
                        String detail_email = ConAPI.setCard(new_card_number);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                };
            }.start();
        }
        if (v.getId() == R.id.back_edit_card){
            Intent intent = new Intent(editCard.this, card.class);
            startActivityForResult(intent, 0);
        }
    }
}
