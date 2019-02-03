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
    private String token, name, email, profilePicturePath;
    private int guardianId;
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

    public boolean loggedin() {
        return prefs.getBoolean("loggedInmode",false);
    }

    public void saveIP() {
        editor.putString("ip", ip);
        editor.commit();
    }

    public String getIP() {
        return prefs.getString("ip", ip);
    }

    public void saveProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
        editor.putString("profilePicturePath", profilePicturePath);
        editor.commit();
    }

    public String getProfilePicturePath() {

        return prefs.getString("profilePicturePath", profilePicturePath);
    }

    public void saveGuardianName(String name) {
        this.name = name;
        editor.putString("name", name);
        editor.commit();
    }

    public String getGuardianName() {

        return prefs.getString("name", name);
    }

    public void saveGuardianEmail(String email) {
        this.email = email;
        editor.putString("email", email);
        editor.commit();
    }

    public String getGuardianEmail() {

        return prefs.getString("email", email);
    }

    public void saveFCM(String token) {
        this.token = token;
        editor.putString("token", token);
        editor.commit();
    }

    public String getFCM() {
        return prefs.getString("token", token);
    }

    public void saveGuardianId(int guardianId) {
        this.guardianId = guardianId;
        editor.putInt("guardianId", guardianId);
        editor.commit();
    }

    public int getGuardianId() {
        return prefs.getInt("guardianId", guardianId);
    }

}
