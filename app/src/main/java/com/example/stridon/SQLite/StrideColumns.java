package com.example.stridon.SQLite;

import android.provider.BaseColumns;

public final class StrideColumns implements BaseColumns {

    public static final String STRIDE_TABLE_NAME = "stride";
    public static final String STRIDE_COLUMN_START_LAT = "lat";
    public static final String STRIDE_COLUMN_START_LONG = "long";
    public static final String STRIDE_COLUMN_ENCODED_POLYLINE = "encoded_polyline";
    public static final String STRIDE_COLUMN_DISTANCE = "distance";
    public static final String STRIDE_COLUMN_DURATION = "duration";
    public static final String STRIDE_COLUMN_STRIDE_TYPE = "stride_type";
    public static final String STRIDE_COLUMN_DEGREES = "degrees";
    public static final String STRIDE_COLUMN_DAY = "day";
    public static final String STRIDE_COLUMN_TIME = "time";
    public static final String STRIDE_COLUMN_FAVORITED = "favorited";
    // TODO any other attribhutes of a Stride, possibly elevation information
}
