package org.mozilla.mozstumbler.provider;

import android.content.ContentProviderOperation;
import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import static org.mozilla.mozstumbler.provider.DatabaseContract.*;

public class Database extends SQLiteOpenHelper {
    private static final String LOGTAG = Database.class.getName();
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "stumbler.db";
    static final String TABLE_REPORTS = "reports";
    static final String TABLE_STATS = "stats";
    static final String TABLE_TRACK = "track";
    static final String INDEX_TRACK = "track_index";

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createTableReports(db);
        db.execSQL("CREATE TABLE " + TABLE_STATS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + StatsColumns.KEY + " VARCHAR(80) UNIQUE NOT NULL,"
                + StatsColumns.VALUE + " TEXT NOT NULL)");
        db.execSQL("CREATE TABLE " + TABLE_TRACK + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY,"
                + TrackColumns.LAT + " INTEGER NOT NULL,"
                + TrackColumns.LON + " INTEGER NOT NULL)");
        db.execSQL("CREATE INDEX " + INDEX_TRACK + " ON "
                + TABLE_TRACK + " (" + TrackColumns.LAT + ")");
        db.insertWithOnConflict(TABLE_STATS, null, Stats.values(Stats.KEY_LAST_UPLOAD_TIME, "0"), SQLiteDatabase.CONFLICT_REPLACE);
        db.insertWithOnConflict(TABLE_STATS, null, Stats.values(Stats.KEY_OBSERVATIONS_SENT, "0"), SQLiteDatabase.CONFLICT_REPLACE);
        db.insertWithOnConflict(TABLE_STATS, null, Stats.values(Stats.KEY_WIFIS_SENT, "0"), SQLiteDatabase.CONFLICT_REPLACE);
        db.insertWithOnConflict(TABLE_STATS, null, Stats.values(Stats.KEY_CELLS_SENT, "0"), SQLiteDatabase.CONFLICT_REPLACE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(LOGTAG, "onUpgrade() from " + oldVersion + " to " + newVersion);

        int version = oldVersion;
        switch (version) {
            case 1:
            case 2:
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
                db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
                onCreate(db);
                version = 3;
        }

        if (version != DATABASE_VERSION) {
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_REPORTS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATS);
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_TRACK);
            onCreate(db);
        }
    }

    private void createTableReports(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + TABLE_REPORTS + " ("
                + BaseColumns._ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + ReportsColumns.TIME + " INTEGER NOT NULL,"
                + ReportsColumns.LAT + " REAL NOT NULL,"
                + ReportsColumns.LON + " REAL NOT NULL,"
                + ReportsColumns.ALTITUDE + " INTEGER,"
                + ReportsColumns.ACCURACY + " INTEGER,"
                + ReportsColumns.RADIO + " VARCHAR(8) NOT NULL,"
                + ReportsColumns.CELL + " TEXT NOT NULL,"
                + ReportsColumns.WIFI + " TEXT NOT NULL,"
                + ReportsColumns.CELL_COUNT + " INTEGER NOT NULL,"
                + ReportsColumns.WIFI_COUNT + " INTEGER NOT NULL,"
                + ReportsColumns.RETRY_NUMBER + " INTEGER NOT NULL DEFAULT 0)");
    }
}
