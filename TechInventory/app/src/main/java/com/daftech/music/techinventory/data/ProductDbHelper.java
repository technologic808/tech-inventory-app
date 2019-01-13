package com.daftech.music.techinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;

import com.daftech.music.techinventory.data.ProductContract.ProductEntry;

//Database helper for the Inventory app. Manages Database creation and version

public class ProductDbHelper extends SQLiteOpenHelper {


    //Database name and version
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "catalog.db";

    //Constructor
    public ProductDbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " + ProductEntry.TABLE_NAME + "("
                        + ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                        + ProductEntry.COLUMN_PRODUCT_NAME + " TEXT, "
                        + ProductEntry.COLUMN_PRICE + " TEXT, "
                        + ProductEntry.COLUMN_QUANTITY + " INTEGER, "
                        + ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                        + ProductEntry.COLUMN_PHONE_NUMBER + " TEXT " +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(
                "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME + ";"
        );
        onCreate(db);
    }
}
