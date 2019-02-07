package com.example.kidsense2019.guardian;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ASUS on 12/02/2017.
 */
public class Session_Guardian {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;
    private String name, email, profilePicturePath;
    private int guardianId;

    public Session_Guardian(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myApp", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void setLoggedIn(Boolean loggedIn) {
        editor.putBoolean("loggedInmodeGuardian",loggedIn);
        editor.commit();
    }

    public boolean loggedin() {
        return prefs.getBoolean("loggedInmodeGuardian",false);
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

    public void saveGuardianId(int guardianId) {
        this.guardianId = guardianId;
        editor.putInt("guardianId", guardianId);
        editor.commit();
    }

    public int getGuardianId() {
        return prefs.getInt("guardianId", guardianId);
    }

}
