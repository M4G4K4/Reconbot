package com.e.reconbot;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.ArrayList;
import java.util.Collections;

public class DbHandler {
    static final String DATABASE_NAME = "ReconBot.db";
    static final String TABLE_NAME = "History";
    static final int DATABASE_VERSION = 3;
    static final String CREATE_History_Table = "CREATE TABLE History (Id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, One TEXT, Two TEXT, Three TEXT, Image BLOB)";

    public static SQLiteDatabase db;
    private final Context context;
    private static SQLiteDatabaseHelper dbHelper;

    public DbHandler(Context cont) {
        context = cont;
        dbHelper = new SQLiteDatabaseHelper(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

// -------------------------------------------------------------------------------------------------------------------------


    public static boolean insertRecord(String Results1, String Results2, String Results3, byte[] imageByte) {

        int contagem, i;
        long lowId = 0;
        ArrayList<HistoryItem> items;
        items = getRows();
        contagem = items.size();
        System.out.println(contagem);

        if (contagem > 20) {
            lowId = items.get(0).getId();
            String lowIdString = "" + lowId;
            db = dbHelper.getWritableDatabase();
            db.delete(TABLE_NAME,"ID = ?",new String[] {lowIdString});
        }

        boolean insertSuccess = false;

        try {
            ContentValues val = new ContentValues();

            val.put("One", Results1);
            val.put("Two", Results2);
            val.put("Three", Results3);
            val.put("Image", imageByte);

            db = dbHelper.getWritableDatabase();
            long res = db.insert(TABLE_NAME, null, val);
            Log.d("Item add to history", " with result = " + res);

            if (res != -1)
                insertSuccess = true;
            db.close();

        } catch (Exception e) {
            Log.e("Insert item fail. ", "Error: " + e.toString());
        }

        return insertSuccess;
    }

// -------------------------------------------------------------------------------------------------------------------------

    public static ArrayList<HistoryItem> getRows() {
        ArrayList<HistoryItem> items = new ArrayList<>();
        HistoryItem item;
        db = dbHelper.getReadableDatabase();
        if (db != null) {
            Cursor itemCursor = db.query(TABLE_NAME, null, null, null, null, null, null, null);

            while (itemCursor.moveToNext()) {
                item = new HistoryItem();
                item.setId((itemCursor.getLong(itemCursor.getColumnIndex("Id"))));
                item.setResults1((itemCursor.getString(itemCursor.getColumnIndex("One"))));
                item.setResults2(itemCursor.getString(itemCursor.getColumnIndex("Two")));
                item.setResults3(itemCursor.getString(itemCursor.getColumnIndex("Three")));
                item.setPhoto(itemCursor.getBlob(itemCursor.getColumnIndex("Image")));
                items.add(item);
            }
            Collections.reverse(items);
            itemCursor.close();
        }
        return items;
    }

// -------------------------------------------------------------------------------------------------------------------------

}