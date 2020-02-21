package com.example.stridon.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.stridon.Stride;

public class StrideDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "StrideDatabaseHelper";

    private static final String STRIDE_DB_NAME = "stride_db"; // name of database
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_STRIDE_TABLE =
            "CREATE TABLE " + StrideColumns.STRIDE_TABLE_NAME + " (" +
                    StrideColumns._ID + " INTEGER PRIMARY KEY NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LAT1 + " DOUBLE(2,6) NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LONG1 + " DOUBLE(3,6) NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LAT2 + " DOUBLE(2,6) NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LONG2 + " DOUBLE(3,6) NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LAT3 + " DOUBLE(2,6) NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LONG3 + " DOUBLE(3,6) NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LAT4 + " DOUBLE(2,6) NOT NULL," +
//                    StrideColumns.STRIDE_COLUMN_LONG4 + " DOUBLE(3,6) NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_ENCODED_POLYLINE + " VARCHAR(100000) NOT NULL," +
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

    public static class StoreStrideTask extends AsyncTask<Stride, Void, Void> {

        private StrideDatabaseHelper strideDatabaseHelper;

        public StoreStrideTask(StrideDatabaseHelper strideDatabaseHelper) {
            this.strideDatabaseHelper = strideDatabaseHelper;
        }

        @Override
        protected Void doInBackground(Stride... strides) {
            Stride stride = strides[0];

            SQLiteDatabase db = strideDatabaseHelper.getWritableDatabase();
            db.beginTransaction();
            try {
                ContentValues values = new ContentValues();
//                values.put(StrideColumns.STRIDE_COLUMN_LAT1, stride.getLat1());
//                values.put(StrideColumns.STRIDE_COLUMN_LONG1, stride.getLong1());
//                values.put(StrideColumns.STRIDE_COLUMN_LAT2, stride.getLat2());
//                values.put(StrideColumns.STRIDE_COLUMN_LONG2, stride.getLong2());
//                values.put(StrideColumns.STRIDE_COLUMN_LAT3, stride.getLat3());
//                values.put(StrideColumns.STRIDE_COLUMN_LONG3, stride.getLat3());
//                values.put(StrideColumns.STRIDE_COLUMN_LAT4, stride.getLat4());
//                values.put(StrideColumns.STRIDE_COLUMN_LONG4, stride.getLong4());
                values.put(StrideColumns.STRIDE_COLUMN_ENCODED_POLYLINE, stride.getEncodedPolyline());
                values.put(StrideColumns.STRIDE_COLUMN_DISTANCE, stride.getDistance());
                values.put(StrideColumns.STRIDE_COLUMN_STRIDE_TYPE, stride.getStrideType());
                values.put(StrideColumns.STRIDE_COLUMN_FAVORITED, stride.isFavorited());

                long newRowId = db.insert(StrideColumns.STRIDE_TABLE_NAME, null, values);
                db.setTransactionSuccessful();
                Log.i(TAG, "store stride success");
            } catch (Exception e) {
                Log.i(TAG, "error in adding in stride databasehelpers");
            } finally {
                db.endTransaction();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            super.onPostExecute(aVoid);
        }
    }

    public static class GetAllStrides extends AsyncTask<Stride, Void, Void> {
        @Override
        protected Void doInBackground(Stride... strides) {
            return null;
        }
    }
}
