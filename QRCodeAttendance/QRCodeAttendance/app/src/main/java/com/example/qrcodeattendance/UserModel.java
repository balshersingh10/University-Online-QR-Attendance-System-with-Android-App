package com.example.qrcodeattendance;

public class UserModel {
    private String username;
    private String password;

    public String getUsername() {
        return username;
    }

    public UserModel(String username, String pass) {
        this.username = username;
        this.password = pass;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPass() {
        return password;
    }

    public void setPass(String pass) {
        this.password = pass;
    }
}
