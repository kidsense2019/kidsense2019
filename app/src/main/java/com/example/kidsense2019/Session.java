package com.example.kidsense2019;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ASUS on 12/02/2017.
 */
public class Session {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;
    private String username, password, email, status, token;
    private String ip = "http://203.189.123.200:3000";

    public Session(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myApp", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(Boolean loggedIn) {
        editor.putBoolean("loggedInmode",loggedIn);
        editor.commit();
    }

    public void saveIP() {
        editor.putString("ip", ip);
        editor.commit();
    }

    public void saveFCM(String token) {
        this.token = token;
        editor.putString("token", token);
        editor.commit();
    }


    public void saveUsername(String username) {
        this.username = username;
        editor.putString("username", username);
        editor.commit();
    }

    public void saveEmail(String email) {
        this.email = email;
        editor.putString("email", email);
        editor.commit();
    }

    public void savePassword(String password) {
        this.password = password;
        editor.putString("password", password);
        editor.commit();
    }

    public void saveStatus(String status) {
        this.status = status;
        editor.putString("status", status);
        editor.commit();
    }

    public String getsaveUsername() {
        return prefs.getString("username", username);
    }

    public String getToken() {

        return prefs.getString("token", token);
    }

    public String getsaveEmail() {

        return prefs.getString("email", email);
    }

    public String getsavePassword() {

        return prefs.getString("password", password);
    }

    public String getsaveStatus() {

        return prefs.getString("status", status);
    }

    public String getIP() {

        return prefs.getString("ip", ip);
    }

    public boolean loggedin() {

        return prefs.getBoolean("loggedInmode",false);
    }
}
