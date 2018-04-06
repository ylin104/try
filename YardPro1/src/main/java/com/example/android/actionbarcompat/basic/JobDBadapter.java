package com.example.android.actionbarcompat.basic;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by joanne.
 */
public class JobDBadapter {

    private SQLiteDatabase db;
    private JobDBhelper dbHelper;
    private final Context context;

    private static final String DB_NAME = "job.db";
    private static int dbVersion = 1;

    private static final String JOBS_TABLE = "jobs";
    public static final String JOB_ID = "job_id";   // column 0
    public static final String JOB_WHERE = "job_where";
    public static final String JOB_WHEN = "job_when";
    public static final String JOB_WHO = "job_who";
    public static final String JOB_NOTES = "job_notes";
    public static final String JOB_COST = "job_cost";
    public static final String JOB_PAID = "job_paid";
    public static final String[] JOB_COLS = {JOB_ID, JOB_WHERE, JOB_WHEN, JOB_WHO, JOB_NOTES, JOB_COST, JOB_PAID};

    public JobDBadapter(Context ctx) {
        context = ctx;
        dbHelper = new JobDBhelper(context, DB_NAME, null, dbVersion);
    }

    public void open() throws SQLiteException {
        try {
            db = dbHelper.getWritableDatabase();
        } catch (SQLiteException ex) {
            db = dbHelper.getReadableDatabase();
        }
    }

    public void close() {
        db.close();
    }

    public void clear() {
        dbHelper.onUpgrade(db, dbVersion, dbVersion+1);  // change version to dump old data
        dbVersion++;
    }

    // database update methods

    public long insertItem(JobItem job) {
        // create a new row of values to insert
        ContentValues cvalues = new ContentValues();
        // assign values for each col
        cvalues.put(JOB_WHERE, job.getWhere());
        cvalues.put(JOB_WHEN, job.getWhen());
        cvalues.put(JOB_WHO, job.getWho());
        cvalues.put(JOB_NOTES, job.getNotes());
        cvalues.put(JOB_COST, job.getCost());
        cvalues.put(JOB_PAID, job.getPaid());
        // add to course table in database
        return db.insert(JOBS_TABLE, null, cvalues);
    }

    public boolean removeItem(long jid) {
        return db.delete(JOBS_TABLE, "JOB_ID="+jid, null) > 0;
    }

    public boolean updateField(long jid, int field, String wh) {
        ContentValues cvalue = new ContentValues();
        cvalue.put(JOB_COLS[field], wh);
        return db.update(JOBS_TABLE, cvalue, JOB_ID+"="+jid, null) > 0;
    }

    public boolean updateCost(long jid, float c) {
        ContentValues cvalue = new ContentValues();
        cvalue.put(JOB_COST, c);
        return db.update(JOBS_TABLE, cvalue, JOB_ID+"="+jid, null) > 0;
    }

    public boolean updatePaid(long jid, short pd) {
        ContentValues cvalue = new ContentValues();
        cvalue.put(JOB_PAID, pd);
        return db.update(JOBS_TABLE, cvalue, JOB_ID+"="+jid, null) > 0;
    }

    // database query methods
    public Cursor getAllItems() {
        return db.query(JOBS_TABLE, JOB_COLS, null, null, null, null, null);
    }

    public Cursor getItemCursor(long jid) throws SQLException {
        Cursor result = db.query(true, JOBS_TABLE, JOB_COLS, JOB_ID+"="+jid, null, null, null, null, null);
        if ((result.getCount() == 0) || !result.moveToFirst()) {
            throw new SQLException("No job items found for row: " + jid);
        }
        return result;
    }

    public JobItem getJobItem(long jid) throws SQLException {
        Cursor cursor = db.query(true, JOBS_TABLE, JOB_COLS, JOB_ID+"="+jid, null, null, null, null, null);
        if ((cursor.getCount() == 0) || !cursor.moveToFirst()) {
            throw new SQLException("No course items found for row: " + jid);
        }
        // must use column indices to get column values
        int whereIndex = cursor.getColumnIndex(JOB_WHERE);
        boolean paid = cursor.getColumnIndex(JOB_PAID) == 1;
        return new JobItem(cursor.getString(whereIndex), cursor.getString(2), cursor.getString(3), cursor.getString(4), cursor.getFloat(5), cursor.getShort(6));
    }


    private static class JobDBhelper extends SQLiteOpenHelper {

        // SQL statement to create a new database
        private static final String DB_CREATE = "CREATE TABLE " + JOBS_TABLE
                + " (" + JOB_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + JOB_WHERE + " TEXT,"
                + JOB_WHEN + " TEXT, " +  JOB_WHO + " TEXT, " +  JOB_NOTES + " TEXT," + JOB_COST + " REAL, "
        + JOB_PAID + " INTEGER);";

        public JobDBhelper(Context context, String name, SQLiteDatabase.CursorFactory fct, int version) {
            super(context, name, fct, version);
        }

        @Override
        public void onCreate(SQLiteDatabase adb) {
            // TODO Auto-generated method stub
            adb.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase adb, int oldVersion, int newVersion) {
            // TODO Auto-generated method stub
            Log.w("JobDB", "upgrading from version " + oldVersion + " to "
                    + newVersion + ", destroying old data");
            // drop old table if it exists, create new one
            // better to migrate existing data into new table
            adb.execSQL("DROP TABLE IF EXISTS " + JOBS_TABLE);
            onCreate(adb);
        }
    } // JobDBhelper class

} // JobDBadapter class
