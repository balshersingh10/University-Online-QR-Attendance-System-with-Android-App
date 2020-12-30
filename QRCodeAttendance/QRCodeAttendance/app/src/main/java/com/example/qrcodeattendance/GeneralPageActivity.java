package com.example.qrcodeattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
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

public class GeneralPageActivity extends AppCompatActivity {
    Button Enroll,LogOut;
    API api;
    LinearLayout CoursesList;
    TextView ProcessingList;
    ImageButton Refresh;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.general_page);
        Global.setRefresh(Global.getRefresh()+1);
        if(Global.getRefresh()==1){
            Toast.makeText(getApplicationContext(),"LogIn Successful",Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(getApplicationContext(),"Refreshing",Toast.LENGTH_LONG).show();
        }
        Enroll = findViewById(R.id.enroll);
        CoursesList = findViewById(R.id.courseslist);
        LogOut = findViewById(R.id.logout);
        Refresh = findViewById(R.id.refresh);
        ProcessingList = findViewById(R.id.processinglist);
        ProcessingList.setText("Please Wait: Getting Registered Courses List");
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(API.MY_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        api = retrofit.create(API.class);
        Call<ResponseBody> call = api.getCourse(Global.getUsername());
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
                            String courses = jsonObject.getString("courses");
                            if (courses.equals("-1")){
                                ProcessingList.setText("No Course Registered");
                                // set text - no registered courses
                            }
                            else {
                                ProcessingList.setText("Click Course to View/Mark Attendance");
                                String[] list_of_courses = ProcessCourses(courses);
                                int l = list_of_courses.length;
                                //System.out.println(courses);
                                for(int j = 0;j<l;j++){
                                    LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                                            LinearLayout.LayoutParams.MATCH_PARENT,
                                            LinearLayout.LayoutParams.WRAP_CONTENT);
                                    Button btn = new Button(GeneralPageActivity.this);
                                    btn.setId(j);
                                    final int id_ = btn.getId();
                                    btn.setText(list_of_courses[j]);
                                    btn.setBackgroundColor(Color.rgb(102,255,102));
                                    CoursesList.addView(btn, params1);
                                    Button btn1 = findViewById(id_);
                                    btn1.setOnClickListener(new View.OnClickListener() {
                                        public void onClick(View view) {
                                            Global.setSelected_course(list_of_courses[btn1.getId()]);
                                            startActivity(new Intent(GeneralPageActivity.this, GeneralCoursePageActivity.class));
                                        }
                                    });
                                }
                                //set recycler view to courses, courses are in a string joined by '-' Ex: "CS301-CS302-CS303"
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                // set text to t.getMessage()
            }
        });
        Enroll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GeneralPageActivity.this, EnrollPageActivity.class));
            }
        });
        LogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Global.Empty();
                startActivity(new Intent(GeneralPageActivity.this, MainActivity.class));
                finish();
            }
        });
        Refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(GeneralPageActivity.this, GeneralPageActivity.class));
                finish();
            }
        });
    }
    public static String[] ProcessCourses(String string)
    {
        return string.split("-", 0);
    }

}