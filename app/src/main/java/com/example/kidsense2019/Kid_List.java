package com.example.kidsense2019;

public class Kid_List {

    String fullName, nickName;
    int kidId;

    public Kid_List(String fullName, String nickName, int kidId) {
        this.fullName = fullName;
        this.nickName = nickName;
        this.kidId = kidId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getNickName() {
        return nickName;
    }

    public int getKidId() {
        return kidId;
    }
}
