package com.example.csr;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DBHelper extends SQLiteOpenHelper {

    static final String DATABASE_NAME = "test.db";
    //static final int DATABASE_VERSION = 2;
 //   static final int version = 2;

    public DBHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }
/*
    public DBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }
*/

    @Override
    public void onCreate(SQLiteDatabase db) {
       // db.execSQL(DATABASE_CREATE_TEAM);
        db.execSQL("CREATE TABLE tableName ( name TEXT, info TEXT, num INTEGER);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS tableName");
        // db.execSQL("DROP TABLE IF EXISTS Word");
        //db.execSQL("DROP TABLE IF EXISTS word");
        onCreate(db);
    }

}
