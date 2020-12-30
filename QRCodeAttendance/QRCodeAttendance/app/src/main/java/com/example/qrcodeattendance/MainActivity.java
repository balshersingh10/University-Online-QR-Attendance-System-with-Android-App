package com.example.qrcodeattendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private Button login,register;
    private EditText username,password;
    API api;
    @Override
    public void onBackPressed() {
        //super.onBackPressed();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    }, 100);
        }
        login = (Button)findViewById(R.id.login);
        register = (Button)findViewById(R.id.register);
        username = (EditText) findViewById(R.id.username);
        password = (EditText) findViewById(R.id.password);
        username.addTextChangedListener(logintextwatcher);
        password.addTextChangedListener(logintextwatcher);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"PLEASE WAIT:CREDENTIALS ARE BEING CHECKED",Toast.LENGTH_LONG).show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API.MY_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                api = retrofit.create(API.class);
                checkUser(username.getText().toString(),password.getText().toString());
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignupActivity.class));
            }
        });
    }
    private TextWatcher logintextwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = username.getText().toString().trim();
            String passwordInput = password.getText().toString().trim();
            login.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void checkUser(String username, String pass){

        Call<ResponseBody> call = api.createUser(username,pass);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (!response.isSuccessful()){
                    //textView.setText("Code: "+ response.code());
                    Toast.makeText(getApplicationContext(),response.code(),Toast.LENGTH_LONG).show();
                    return;
                }
                String s = null;
                try {
                    s = response.body().string();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(s != null){
                    try {
                        JSONObject jsonObject = new JSONObject(s);
                        String flag = jsonObject.getString("flag");
                        if (flag.equals("Invalid Credentials")){
                            //Invalid Credential
                            Toast.makeText(MainActivity.this, "Invalid Credential", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Global.setUsername(username);
                            startActivity(new Intent(MainActivity.this, GeneralPageActivity.class));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //textView.setText(t.getMessage());
                Toast.makeText(getApplicationContext(),t.getMessage(),Toast.LENGTH_LONG).show();
            }
        });
    }
}