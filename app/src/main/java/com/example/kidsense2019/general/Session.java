package com.example.kidsense2019.general;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by ASUS on 12/02/2017.
 */
public class Session {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context ctx;
    private String token;
    private String ip = "http://203.189.123.200:3000";

    public Session(Context ctx) {
        this.ctx = ctx;
        prefs = ctx.getSharedPreferences("myApp", Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveIP() {
        editor.putString("ip", ip);
        editor.commit();
    }

    public String getIP() {
        return prefs.getString("ip", ip);
    }

    public void saveFCM(String token) {
        this.token = token;
        editor.putString("token", token);
        editor.commit();
    }

    public String getFCM() {
        return prefs.getString("token", token);
    }

}
