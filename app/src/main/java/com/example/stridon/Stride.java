package com.example.stridon;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Stride implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Stride createFromParcel(Parcel in) {
            return new Stride(in);
        }

        public Stride[] newArray(int size) {
            return new Stride[size];
        }
    };

    private double startLat;
    private double startLong;
    private String encodedPolyline;
    private double distance; // miles
    private int duration; // minutes
    private String strideType;
    private double degrees; // fahrenheit
    private String day;
    private long time;
    private boolean favorited;

    public Stride(double startLat, double startLong, String encodedPolyline, double distance, int duration, String strideType, double degrees, String day, long time) {
        this.startLat = startLat;
        this.startLong = startLong;
        this.encodedPolyline = encodedPolyline;
        this.distance = distance;
        this.duration = duration;
        this.strideType = strideType;
        this.degrees = degrees;
        this.day = day;
        this.time = time;
        this.favorited = false;
    }

    public Stride(Parcel p) {
        this.startLat = p.readDouble();
        this.startLong = p.readDouble();
        this.encodedPolyline = p.readString();
        this.distance = p.readDouble();
        this.duration = p.readInt();
        this.strideType = p.readString();
        this.degrees = p.readDouble();
        this.day = p.readString();
        this.time = p.readLong();
        String bool = p.readString();
        this.favorited = Boolean.getBoolean(bool);
    }

    public double getStartLat() {
        return startLat;
    }

    public void setStartLat(double startLat) {
        this.startLat = startLat;
    }

    public double getStartLong() {
        return startLong;
    }

    public void setStartLong(double startLong) {
        this.startLong = startLong;
    }

    public String getEncodedPolyline() {
        return encodedPolyline;
    }

    public void setEncodedPolyline(String encodedPolyline) {
        this.encodedPolyline = encodedPolyline;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getStrideType() {
        return strideType;
    }

    public void setStrideType(String strideType) {
        this.strideType = strideType;
    }

    public double getDegrees() {
        return degrees;
    }

    public void setDegrees(double degrees) {
        this.degrees = degrees;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean isFavorited() {
        return favorited;
    }

    public void setFavorited(boolean favorited) {
        this.favorited = favorited;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(startLat);
        dest.writeDouble(startLong);
        dest.writeString(encodedPolyline);
        dest.writeDouble(distance);
        dest.writeInt(duration);
        dest.writeString(strideType);
        dest.writeDouble(degrees);
        dest.writeString(day);
        dest.writeLong(time);
        dest.writeString(String.valueOf(favorited));
    }

    @NonNull
    @Override
    public String toString() {
        String s = String.format("(startLat: %f, startLong: %f, encodedPolyline: %s, distance: %f, duration: %d, strideType: %s, degrees: %f, day: %s, time: %d)",
                startLat, startLong, encodedPolyline, distance, duration, strideType, degrees, day, time);
        return s;
    }
}