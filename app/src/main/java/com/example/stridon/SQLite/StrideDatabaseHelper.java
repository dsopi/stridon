package com.example.stridon.SQLite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class StrideDatabaseHelper extends SQLiteOpenHelper {

    private static final String STRIDE_DB_NAME = "stride_db"; // name of database
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_STRIDE_TABLE =
            "CREATE TABLE " + StrideColumns.STRIDE_TABLE_NAME + " (" +
                    StrideColumns._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_DISTANCE + " INTEGER NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_STRIDE_TYPE + " TEXT NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_FAVORITED + " BIT NOT NULL)";

    /*
        DEBUG ONLY
     */
    private static final String DELETE_STRIDE_TABLE =
            "DROP TABLE IF EXISTS " + StrideColumns.STRIDE_TABLE_NAME;

    private static StrideDatabaseHelper strideDatabaseHelper;

    public static synchronized StrideDatabaseHelper getInstance(Context context) {
        if (strideDatabaseHelper == null) {
            strideDatabaseHelper = new StrideDatabaseHelper(context.getApplicationContext());
        }
        return strideDatabaseHelper;
    }

    private StrideDatabaseHelper(Context context) {
        super(context, STRIDE_DB_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_STRIDE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DELETE_STRIDE_TABLE);
        onCreate(db);
    }
}
