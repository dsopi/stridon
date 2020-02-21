package com.example.stridon;

public class Stride {

//    private double lat1;
//    private double long1;
//    private double lat2;
//    private double long2;
//    private double lat3;
//    private double long3;
//    private double lat4;
//    private double long4;
    private int distance;
    private String strideType;
    private boolean favorited;
    private String encodedPolyline;

    public Stride(int distance, String strideType, String encodedPolyline) {
        this.distance = distance;
        this.strideType = strideType;
        this.favorited = false;
        this.encodedPolyline = encodedPolyline;
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

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
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
}