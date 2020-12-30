package com.example.qrcodeattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GeneralCoursePageActivity extends AppCompatActivity {
    Button QRButton,deregister;
    TextView tcvalue,tavalue,perc,courseid;
    API api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_course_page);
        courseid = findViewById(R.id.courseid);
        courseid.setText(Global.getSelected_course());
        QRButton = findViewById(R.id.QRButton);
        tcvalue = findViewById(R.id.tcvalue);
        tavalue = findViewById(R.id.tavalue);
        perc = findViewById(R.id.percentattendance);
        deregister = findViewById(R.id.deregister);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(API.class);
        Call<ResponseBody> call = api.viewattendance(Global.getUsername(),Global.getSelected_course());
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()){
                    //textView.setText("Code: "+ response.code());
                    Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                }
                else {
                    String s = null;
                    try {
                        s = response.body().string();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    if(s != null){
                        try {
                            JSONObject jsonObject = new JSONObject(s);
                            String total_attended = jsonObject.getString("total_attended");
                            String total_classes = jsonObject.getString("total_classes");
                            tavalue.setText(total_attended);
                            tcvalue.setText(total_classes);
                            float ta = Float.parseFloat(total_attended);
                            float tc = Float.parseFloat(total_classes);
                            float p = (ta/tc)*100;
                            perc.setText("Attendance % : "+Float.toString(p));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // set text to t.getMessage()
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });

        QRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GeneralCoursePageActivity.this, QRCodeScannerActivity.class));
            }
        });
        deregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Please Wait! De-Registering...",Toast.LENGTH_LONG).show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API.MY_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                api = retrofit.create(API.class);
                Call<ResponseBody> call = api.deregister(Global.getUsername(),Global.getSelected_course());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()){
                            //textView.setText("Code: "+ response.code());
                            Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                        }
                        else {
                            String s = null;
                            try {
                                s = response.body().string();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                            if(s != null){
                                try {
                                    JSONObject jsonObject = new JSONObject(s);
                                    String message = jsonObject.getString("message");
                                    startActivity(new Intent(GeneralCoursePageActivity.this, EnrollSuccessActivity.class));
                                    Global.setEnroll_message(message);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        // set text to t.getMessage()
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

}