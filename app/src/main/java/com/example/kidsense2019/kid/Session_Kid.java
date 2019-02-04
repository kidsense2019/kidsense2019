package com.example.kidsense2019.kid;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ASUS on 12/02/2017.
 */
public class Session_Kid {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;
    private String fullname, nickName, profilePicturePath;
    private int kidId;

    public Session_Kid(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myApp", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(Boolean loggedIn) {
        editor.putBoolean("loggedInmodeKid",loggedIn);
        editor.commit();
    }

    public boolean loggedin() {
        return prefs.getBoolean("loggedInmodeKid",false);
    }

    public void saveProfilePicturePath(String profilePicturePath) {
        this.profilePicturePath = profilePicturePath;
        editor.putString("profilePicturePath", profilePicturePath);
        editor.commit();
    }

    public String getProfilePicturePath() {

        return prefs.getString("profilePicturePath", profilePicturePath);
    }

    public void saveKidFullname(String fullname) {
        this.fullname = fullname;
        editor.putString("fullname", fullname);
        editor.commit();
    }

    public String getKidFullname() {

        return prefs.getString("fullname", fullname);
    }

    public void saveKidNickname(String nickName) {
        this.nickName = nickName;
        editor.putString("nickName", nickName);
        editor.commit();
    }

    public String getKidNickname() {

        return prefs.getString("nickName", nickName);
    }

    public void saveKidId(int kidId) {
        this.kidId = kidId;
        editor.putInt("kidId", kidId);
        editor.commit();
    }

    public int getKidId() {
        return prefs.getInt("kidId", kidId);
    }

}
