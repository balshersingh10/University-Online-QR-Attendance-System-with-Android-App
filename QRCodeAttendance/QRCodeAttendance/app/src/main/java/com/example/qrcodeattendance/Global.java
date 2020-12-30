package com.example.qrcodeattendance;
import android.app.Application;

public class Global extends Application {
    public static String qrmessage;
    public static String username;
    public static String register_message;
    public static String enroll_message;
    public static int refresh;
    public static String selected_course;
    public Global() {
        qrmessage = null;
        username = null;
        register_message = null;
        enroll_message = null;
        refresh = 0;
        selected_course = null;
    }

    public static String getSelected_course() {
        return selected_course;
    }

    public static void setSelected_course(String selected_course) {
        Global.selected_course = selected_course;
    }

    public static int getRefresh() {
        return refresh;
    }

    public static void setRefresh(int refresh) {
        Global.refresh = refresh;
    }

    public static String getEnroll_message() {
        return enroll_message;
    }

    public static void setEnroll_message(String enroll_message) {
        Global.enroll_message = enroll_message;
    }

    public static String getQrmessage() {
        return qrmessage;
    }

    public static void setQrmessage(String qrmessage) {
        com.example.qrcodeattendance.Global.qrmessage = qrmessage;
    }

    public static String getUsername() {
        return username;
    }

    public static void setUsername(String username) {
        Global.username = username;
    }

    public static String getRegister_message() {
        return register_message;
    }

    public static void setRegister_message(String register_message) {
        Global.register_message = register_message;
    }
    public static void Empty(){
        Global.username=null;
        Global.qrmessage=null;
        Global.register_message=null;
        Global.enroll_message=null;
        Global.refresh=0;
        Global.selected_course = null;
    }
}
