package com.example.qrcodeattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

public class EnrollPageActivity extends AppCompatActivity {
    Button Enroll;
    EditText CourseId;
    TextView startmessage;
    LinearLayout allcourses;
    API api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enroll_page);
        Enroll = (Button)findViewById(R.id.CourseEnroll);
        CourseId = (EditText)findViewById(R.id.CourseId);
        Enroll = findViewById(R.id.CourseEnroll);
        CourseId = findViewById(R.id.CourseId);
        CourseId.addTextChangedListener(logintextwatcher);
        startmessage = findViewById(R.id.startmessage);
        allcourses = findViewById(R.id.listtoenrollcourses);
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(API.class);
        Call<ResponseBody> call = api.getallcourses("Nothing");
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()){
                    Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                    //textView.setText("Code: "+ response.code());
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
                            String course_id = jsonObject.getString("course_id");
                            String course_title = jsonObject.getString("course_title");
                            if (course_id.equals("-1") || course_title.equals("-1")){
                                startmessage.setText("No Course Registered");
                                // set text - no registered courses
                            }
                            else {
                                startmessage.setVisibility(View.GONE) ;
                                startmessage.setText("Courses List below :");
                                String[] list_of_courseid = GetAllCourses(course_id);
                                String[] list_of_coursetitle = GetAllCourses(course_title);
                                int l = list_of_courseid.length;
                                //System.out.println(list_of_courseid);
                                for(int j = 0;j<l;j++){
                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    TextView textView = new TextView(EnrollPageActivity.this);
                                    textView.setId(j);
                                    textView.setText("Course_Id :"+list_of_courseid[j]+" and Title :"+list_of_coursetitle[j]);
                                    allcourses.addView(textView, params1);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                // set text to t.getMessage()
            }
        });
        Enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Checking info, Please Wait",Toast.LENGTH_LONG).show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API.MY_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                api = retrofit.create(API.class);
                Call<ResponseBody> call = api.enroll(Global.getUsername(),CourseId.getText().toString());
                call.enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (!response.isSuccessful()){
                            //textView.setText("Code: "+ response.code());
                            Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                        }
                        else{
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
                                    Global.setEnroll_message(message +CourseId.getText());
                                    Global.setEnroll_message(message +" "+CourseId.getText());
                                    startActivity(new Intent(EnrollPageActivity.this, EnrollSuccessActivity.class));
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                            }
                        }


                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }
    private TextWatcher logintextwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String courseInput = CourseId.getText().toString().trim();
            Enroll.setEnabled(!courseInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };
    public static String[] GetAllCourses(String course)
    {
        return course.split("-", 0);
    }
}