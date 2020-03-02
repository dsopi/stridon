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

    private double distance;
    private String strideType;
    private boolean favorited;
    private String encodedPolyline;

    public Stride(double distance, String strideType, String encodedPolyline) {
        this.distance = distance;
        this.strideType = strideType;
        this.favorited = false;
        this.encodedPolyline = encodedPolyline;
    }

    public Stride(Parcel p){
        this.distance = p.readDouble();
        this.strideType = p.readString();
        String bool = p.readString();
        this.favorited = Boolean.getBoolean(bool);
        this.encodedPolyline = p.readString();
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

    public String getStrideType() {
        return strideType;
    }

    public void setStrideType(String strideType) {
        this.strideType = strideType;
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
        dest.writeDouble(distance);
        dest.writeString(strideType);
        dest.writeString(String.valueOf(favorited));
        dest.writeString(encodedPolyline);
    }

    @NonNull
    @Override
    public String toString() {
        String s = "Stride: " + strideType + " distance: " + distance + " polyline: " + encodedPolyline;
        return s;
    }
}