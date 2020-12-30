package com.example.qrcodeattendance;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
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

public class SignupActivity extends AppCompatActivity {
    Button RegisterButton;
    TextView RUsername,RPassword,REmail,FName,LName;
    API api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);

        FName = findViewById(R.id.firstname);
        LName = findViewById(R.id.lastname);
        RegisterButton = findViewById(R.id.RegisterButton);
        RUsername = findViewById(R.id.RegisterUsername);
        RPassword = findViewById(R.id.RegisterPassword);
        REmail = findViewById(R.id.RegisterEmail);
        RUsername.addTextChangedListener(logintextwatcher);
        RPassword.addTextChangedListener(logintextwatcher);
        REmail.addTextChangedListener(logintextwatcher);
        FName.addTextChangedListener(logintextwatcher);
        LName.addTextChangedListener(logintextwatcher);
        RegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(SignupActivity.this, "Adding New User", Toast.LENGTH_SHORT).show();
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(API.MY_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                api = retrofit.create(API.class);
                registerUser(RUsername.getText().toString(),
                        RPassword.getText().toString(),
                        FName.getText().toString(),
                        LName.getText().toString(),
                        REmail.getText().toString());
            }
        });
    }
    private TextWatcher logintextwatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String usernameInput = RUsername.getText().toString().trim();
            String passwordInput = RPassword.getText().toString().trim();
            String emailInput = REmail.getText().toString().trim();
            String fnameInput = FName.getText().toString().trim();
            String lnameInput = LName.getText().toString().trim();
            RegisterButton.setEnabled(!usernameInput.isEmpty() && !passwordInput.isEmpty() && !emailInput.isEmpty() && !fnameInput.isEmpty() && !lnameInput.isEmpty());
        }
        @Override
        public void afterTextChanged(Editable s) {
        }
    };

    private void registerUser(String username, String password,String fname,String lname,String email){

        Call<ResponseBody> call = api.signup(username,password,fname,lname,email);

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
                        if (flag.equals("Username already Exists")){
                            //Invalid Credential
                            Toast.makeText(SignupActivity.this, "Username already Exists", Toast.LENGTH_SHORT).show();
                        }
                        else if (flag.equals("This Email is Already Registered")){
                            //Invalid Credential
                            Toast.makeText(SignupActivity.this, "This Email is Already Registered", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            //Global.setUsername(username);
                            Global.setRegister_message("Successfully Registered");
                            startActivity(new Intent(SignupActivity.this, RegisterSuccessActivity.class));
                            finish();
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