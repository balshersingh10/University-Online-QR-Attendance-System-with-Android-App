package com.example.qrcodeattendance;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

public interface API {
    String MY_URL = "http://192.168.0.105:8000/";
    //String MY_URL = "https://jsonplaceholder.typicode.com/";
    @GET("api/user/")
    //@GET("posts")
    Call<List<UserModel>> getPost();

    @FormUrlEncoded
    @POST("api/")
    Call<ResponseBody> createUser(
            @Field("username") String username,
            @Field("password") String password
    );

    @FormUrlEncoded
    @POST("api/courses/")
    Call<ResponseBody> getCourse(
            @Field("username") String username
    );

    @FormUrlEncoded
    @POST("api/enroll/")
    Call<ResponseBody> enroll(
            @Field("username") String username,
            @Field("course_id") String course_id
    );

    @FormUrlEncoded
    @POST("api/qrcode/")
    Call<ResponseBody> sendQR(
            @Field("username") String username,
            @Field("qr") String qr
    );

    @FormUrlEncoded
    @POST("api/signup/")
    Call<ResponseBody> signup(
            @Field("username") String username,
            @Field("password") String password,
            @Field("fname") String fname,
            @Field("lname") String lname,
            @Field("email") String email
    );
    @FormUrlEncoded
    @POST("api/getallcourses/")
    Call<ResponseBody> getallcourses(
            @Field("username") String username
    );
    @FormUrlEncoded
    @POST("api/viewattendance/")
    Call<ResponseBody> viewattendance(
            @Field("username") String username,
            @Field("course_id") String course_id
    );

    @FormUrlEncoded
    @POST("api/deregister/")
    Call<ResponseBody> deregister(
            @Field("username") String username,
            @Field("course_id") String course_id
    );
}
