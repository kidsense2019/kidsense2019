package com.example.kidsense2019.guardian.sensor;

import android.os.Parcel;
import android.os.Parcelable;

public class heart_rate_data_struct implements Parcelable {
    private String date, time, hearRate;

    public heart_rate_data_struct(String date, String time, String hearRate) {
        this.date = date;
        this.time = time;
        this.hearRate = hearRate;
    }

    public String getDate() {

        return date;
    }

    public String getTime() {

        return time;
    }

    public String getHeartRate() {

        return hearRate;
    }

    public heart_rate_data_struct(Parcel in) {
        super();
        readFromParcel(in);
    }

    public static final Parcelable.Creator<heart_rate_data_struct> CREATOR = new Parcelable.Creator<heart_rate_data_struct>() {
        public heart_rate_data_struct createFromParcel(Parcel in) {

            return new heart_rate_data_struct(in);
        }

        public heart_rate_data_struct[] newArray(int size) {

            return new heart_rate_data_struct[size];
        }

    };

    public void readFromParcel(Parcel in) {
        this.date = in.readString();
        this.time = in.readString();
        this.hearRate = in.readString();
    }

    public int describeContents() {

        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(date);
        dest.writeString(time);
        dest.writeString(hearRate);
    }
}
