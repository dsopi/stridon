package com.example.stridon.SQLite;

import android.provider.BaseColumns;

public final class StrideColumns implements BaseColumns {

    public static final String STRIDE_TABLE_NAME = "stride";
    // TODO how to store lat and long pairs? and do we store all 4 points?
    public static final String STRIDE_COLUMN_DISTANCE = "distance";
    public static final String STRIDE_COLUMN_STRIDE_TYPE = "stride_type";
    public static final String STRIDE_COLUMN_FAVORITED = "favorited";
    // TODO any other attributes of a Stride, possibly elevation information
}
