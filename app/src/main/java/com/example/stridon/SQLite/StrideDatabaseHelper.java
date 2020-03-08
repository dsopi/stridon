package com.example.stridon.SQLite;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;

import com.example.stridon.Stride;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class StrideDatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "StrideDatabaseHelper";

    private static final String STRIDE_DB_NAME = "stride_db"; // name of database
    private static final int DATABASE_VERSION = 1;

    private static final String CREATE_STRIDE_TABLE =
            "CREATE TABLE " + StrideColumns.STRIDE_TABLE_NAME + " (" +
                    StrideColumns._ID + " INTEGER PRIMARY KEY NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_START_LAT + " DOUBLE(2,6) NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_START_LONG + " DOUBLE(3,6) NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_ENCODED_POLYLINE + " VARCHAR(100000) NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_DISTANCE + " DOUBLE(3,10)  NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_DURATION + " INTEGER NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_STRIDE_TYPE + " TEXT NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_DEGREES + " DOUBLE(3,2) NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_DAY + " VARCHAR(10) NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_TIME + " INTEGER NOT NULL," +
                    StrideColumns.STRIDE_COLUMN_FAVORITED + " INTEGER NOT NULL)";

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
                values.put(StrideColumns.STRIDE_COLUMN_START_LAT, stride.getStartLat());
                values.put(StrideColumns.STRIDE_COLUMN_START_LONG, stride.getStartLong());
                values.put(StrideColumns.STRIDE_COLUMN_ENCODED_POLYLINE, stride.getEncodedPolyline());
                values.put(StrideColumns.STRIDE_COLUMN_DISTANCE, stride.getDistance());
                values.put(StrideColumns.STRIDE_COLUMN_DURATION, stride.getDuration());
                values.put(StrideColumns.STRIDE_COLUMN_STRIDE_TYPE, stride.getStrideType());
                values.put(StrideColumns.STRIDE_COLUMN_DEGREES, stride.getDegrees());
                values.put(StrideColumns.STRIDE_COLUMN_DAY, stride.getDay());
                values.put(StrideColumns.STRIDE_COLUMN_TIME, stride.getTime());
                values.put(StrideColumns.STRIDE_COLUMN_FAVORITED, stride.isFavorited() ? 1 : 0);

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


    public static class GetLast10Strides extends AsyncTask<Void, Void, List<Stride>> {
        private StrideDatabaseHelper strideDatabaseHelper;

        public GetLast10Strides(StrideDatabaseHelper strideDatabaseHelper) {
            this.strideDatabaseHelper = strideDatabaseHelper;
        }

        @Override
        protected List<Stride> doInBackground(Void... voids) {
            SQLiteDatabase db = strideDatabaseHelper.getReadableDatabase();

//            String whereClause = "";
//            String[] selectionArgs = new String[]{};
            String orderBy = StrideColumns.STRIDE_COLUMN_TIME + " DESC ";

            Cursor cursor = db.query(StrideColumns.STRIDE_TABLE_NAME, null, null, null, null, null, orderBy, "10");
            ArrayList<Stride> strides = new ArrayList<>();
            try {
                if (cursor.moveToFirst()) {
                    do {
                        double startLat = cursor.getDouble(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_START_LAT));
                        double startLong = cursor.getDouble(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_START_LONG));
                        String encodedPolyline = cursor.getString(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_ENCODED_POLYLINE));
                        double distance = cursor.getDouble(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_DISTANCE));
                        int duration = cursor.getInt(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_DURATION));
                        String strideType = cursor.getString(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_STRIDE_TYPE));
                        double degrees = cursor.getDouble(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_DEGREES));
                        String day = cursor.getString(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_DAY));
                        long time = cursor.getLong(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_TIME));
//                        boolean favorited = false;
//                        if (cursor.getInt(cursor.getColumnIndex(StrideColumns.STRIDE_COLUMN_FAVORITED)) == 1)
//                            favorited = true;


                        Stride stride = new Stride(startLat, startLong, encodedPolyline, distance, duration, strideType, degrees, day, time);
                        Log.i(TAG, stride.toString());
                        strides.add(stride);
                    } while (cursor.moveToNext());
                }
            } catch (Exception e) {
                Log.i(TAG, "Error while trying to get Strides from database");
                Log.i(TAG, e.toString());
            } finally {
                if (cursor != null && !cursor.isClosed()) {
                    cursor.close();
                }
            }
            return strides;
        }

        @Override
        protected void onPostExecute(List<Stride> strides) {
            super.onPostExecute(strides);
            Log.i(TAG, "retrieved strides");
            Log.i(TAG, strides.toString());
        }
    }
}
