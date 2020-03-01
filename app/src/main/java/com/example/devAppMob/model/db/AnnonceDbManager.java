package com.example.devAppMob.model.db;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

import com.example.devAppMob.model.Annonce;
import com.example.devAppMob.model.db.AnnonceContract.AnnonceEntry;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;
import com.squareup.moshi.Types;

import java.lang.reflect.Type;
import java.util.Date;
import java.util.List;

public class AnnonceDbManager {
    private SQLiteOpenHelper dbHelper;

    public AnnonceDbManager(SQLiteOpenHelper dbHelper) {
        this.dbHelper = dbHelper;
    }

    public long add(Annonce annonce) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(AnnonceEntry.COLUMN_NAME_ID, annonce.getId());
        values.put(AnnonceEntry.COLUMN_NAME_TITRE, annonce.getTitre());
        values.put(AnnonceEntry.COLUMN_NAME_DESCRIPTION, annonce.getDescription());
        values.put(AnnonceEntry.COLUMN_NAME_PRIX, annonce.getPrix());
        values.put(AnnonceEntry.COLUMN_NAME_PSEUDO, annonce.getPseudo());
        values.put(AnnonceEntry.COLUMN_NAME_EMAIL_CONTACT, annonce.getEmailContact());
        values.put(AnnonceEntry.COLUMN_NAME_TEL_CONTACT, annonce.getTelContact());
        values.put(AnnonceEntry.COLUMN_NAME_VILLE, annonce.getVille());
        values.put(AnnonceEntry.COLUMN_NAME_CP, annonce.getCp());

        Moshi moshi = new Moshi.Builder().build();
        Type types = Types.newParameterizedType(List.class, String.class);
        JsonAdapter<List<String>> jsonAdapter = moshi.adapter(types);
        String imagesJSON = jsonAdapter.toJson(annonce.getImages());
        values.put(AnnonceEntry.COLUMN_NAME_IMAGES, imagesJSON);

        String dateStr = new Date(annonce.getDate()).toString();
        values.put(AnnonceEntry.COLUMN_NAME_DATE, dateStr);

        long newRowId = db.insert(AnnonceEntry.TABLE_NAME, null, values);
        return newRowId;
    }

    public Cursor readAll() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                BaseColumns._ID,
                AnnonceEntry.COLUMN_NAME_ID,
                AnnonceEntry.COLUMN_NAME_TITRE,
                AnnonceEntry.COLUMN_NAME_DESCRIPTION,
                AnnonceEntry.COLUMN_NAME_PRIX,
                AnnonceEntry.COLUMN_NAME_PSEUDO,
                AnnonceEntry.COLUMN_NAME_EMAIL_CONTACT,
                AnnonceEntry.COLUMN_NAME_TEL_CONTACT,
                AnnonceEntry.COLUMN_NAME_VILLE,
                AnnonceEntry.COLUMN_NAME_CP,
                AnnonceEntry.COLUMN_NAME_PSEUDO,
                AnnonceEntry.COLUMN_NAME_IMAGES,
                AnnonceEntry.COLUMN_NAME_DATE};

        String sortOrder = AnnonceEntry._ID + " ASC";
        Cursor cursor = db.query(
                AnnonceEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                null,              // The columns for the WHERE clause
                null,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrder               // The sort order
        );
        return cursor;
    }

    public int delete(String id){
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String selection = AnnonceEntry.COLUMN_NAME_ID + " LIKE ?";
        String[] selectionArgs = {id};
        int deletedRows = db.delete(AnnonceEntry.TABLE_NAME, selection, selectionArgs);
        return deletedRows;
    }
}

