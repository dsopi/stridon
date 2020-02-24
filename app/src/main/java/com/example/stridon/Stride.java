package com.example.stridon;

import android.os.Parcel;
import android.os.Parcelable;

public class Stride implements Parcelable {
    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Stride createFromParcel(Parcel in) {
            return new Stride(in);
        }

        public Stride[] newArray(int size) {
            return new Stride[size];
        }
    };
//    private double lat1;
//    private double long1;
//    private double lat2;
//    private double long2;
//    private double lat3;
//    private double long3;
//    private double lat4;
//    private double long4;
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

    //    public Stride(double lat1, double long1, double lat2, double long2, double lat3, double long3, double lat4, double long4, int distance, String strideType, boolean favorited) {
//        this.lat1 = lat1;
//        this.long1 = long1;
//        this.lat2 = lat2;
//        this.long2 = long2;
//        this.lat3 = lat3;
//        this.long3 = long3;
//        this.lat4 = lat4;
//        this.long4 = long4;
//        this.distance = distance;
//        this.strideType = strideType;
//        this.favorited = favorited;
//    }


//    public double getLat1() {
//        return lat1;
//    }
//
//    public void setLat1(double lat1) {
//        this.lat1 = lat1;
//    }
//
//    public double getLong1() {
//        return long1;
//    }
//
//    public void setLong1(double long1) {
//        this.long1 = long1;
//    }
//
//    public double getLat2() {
//        return lat2;
//    }
//
//    public void setLat2(double lat2) {
//        this.lat2 = lat2;
//    }
//
//    public double getLong2() {
//        return long2;
//    }
//
//    public void setLong2(double long2) {
//        this.long2 = long2;
//    }
//
//    public double getLat3() {
//        return lat3;
//    }
//
//    public void setLat3(double lat3) {
//        this.lat3 = lat3;
//    }
//
//    public double getLong3() {
//        return long3;
//    }
//
//    public void setLong3(double long3) {
//        this.long3 = long3;
//    }
//
//    public double getLat4() {
//        return lat4;
//    }
//
//    public void setLat4(double lat4) {
//        this.lat4 = lat4;
//    }
//
//    public double getLong4() {
//        return long4;
//    }
//
//    public void setLong4(double long4) {
//        this.long4 = long4;
//    }

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
}