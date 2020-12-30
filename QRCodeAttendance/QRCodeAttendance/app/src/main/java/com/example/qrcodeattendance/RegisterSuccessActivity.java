package com.example.qrcodeattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RegisterSuccessActivity extends AppCompatActivity {
    Button RegMsgOk;
    TextView RegMsg;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register_success);
        RegMsgOk = findViewById(R.id.EnrollMsgOK);
        RegMsg = findViewById(R.id.EnrollMsg);
        RegMsg.setText(Global.getRegister_message());
        RegMsgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterSuccessActivity.this, MainActivity.class));
                finish();
            }
        });
    }
}