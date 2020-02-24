package com.example.tp4.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.tp4.model.db.AnnonceContract.AnnonceEntry;

public class AnnonceDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "AnnonceReader.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AnnonceContract.AnnonceEntry.TABLE_NAME + " (" +
                    AnnonceEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    AnnonceEntry.COLUMN_NAME_ID + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_TITRE + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_DESCRIPTION + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_PRIX + " INTEGER," +
                    AnnonceEntry.COLUMN_NAME_PSEUDO + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_EMAIL_CONTACT + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_TEL_CONTACT + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_VILLE + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_CP + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_IMAGES + " TEXT," +
                    AnnonceEntry.COLUMN_NAME_DATE + " DATETIME)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AnnonceContract.AnnonceEntry.TABLE_NAME;


    public AnnonceDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }
}
