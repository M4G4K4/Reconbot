package com.e.reconbot;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import androidx.annotation.Nullable;

public class SQLiteDatabaseHelper extends SQLiteOpenHelper {
    public SQLiteDatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

// -------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(DbHandler.CREATE_History_Table);
        } catch (SQLException e) {
            Log.e("CREATE TABLE", "Error: " + e.toString());

        }
    }

// -------------------------------------------------------------------------------------------------------------------------

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
