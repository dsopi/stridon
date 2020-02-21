package com.example.stridon.SQLite;

import android.provider.BaseColumns;

public final class StrideColumns implements BaseColumns {

    public static final String STRIDE_TABLE_NAME = "stride";
    public static final String STRIDE_COLUMN_ENCODED_POLYLINE = "encoded_polyline";
    // TODO how to store lat and long pairs? and do we store all 4 points?
//    public static final String STRIDE_COLUMN_LAT1 = "lat1";
//    public static final String STRIDE_COLUMN_LONG1 = "long1";
//    public static final String STRIDE_COLUMN_LAT2 = "lat2";
//    public static final String STRIDE_COLUMN_LONG2 = "long2";
//    public static final String STRIDE_COLUMN_LAT3 = "lat3";
//    public static final String STRIDE_COLUMN_LONG3 = "long3";
//    public static final String STRIDE_COLUMN_LAT4 = "lat4";
//    public static final String STRIDE_COLUMN_LONG4 = "long4";
    public static final String STRIDE_COLUMN_DISTANCE = "distance";
    public static final String STRIDE_COLUMN_STRIDE_TYPE = "stride_type";
    public static final String STRIDE_COLUMN_FAVORITED = "favorited";
    // TODO any other attributes of a Stride, possibly elevation information
}
