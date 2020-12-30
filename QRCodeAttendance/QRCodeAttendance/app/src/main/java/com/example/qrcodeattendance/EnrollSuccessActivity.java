package com.example.qrcodeattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class EnrollSuccessActivity extends AppCompatActivity {
    Button EnnMsgOk;
    TextView EnnMsg;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enroll_success);
        EnnMsgOk = (Button)findViewById(R.id.EnrollMsgOK);
        EnnMsg = (TextView)findViewById(R.id.EnrollMsg);
        EnnMsgOk = findViewById(R.id.EnrollMsgOK);
        EnnMsg = findViewById(R.id.EnrollMsg);
        EnnMsg.setText(Global.getEnroll_message());
        EnnMsgOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(EnrollSuccessActivity.this, GeneralPageActivity.class));
            }
        });
    }
}