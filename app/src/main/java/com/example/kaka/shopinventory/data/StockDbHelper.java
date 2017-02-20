package com.example.kaka.shopinventory.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.kaka.shopinventory.data.StockContract.StockEntry;

/**
 * Created by Kaka on 2/16/2017.
 */

public class StockDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = StockDbHelper.class.getSimpleName();
    private static final String DATABASE_NAME = "stock.db";
    private static final int DATABASE_VERSION = 1;

    public StockDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String SQL_CREATE_STOCK_TABLE = "CREATE TABLE " + StockEntry.TABLE_NAME + "("
                + StockEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + StockEntry.COLUMN_PRODUCT_NAME + " TEXT, "
                + StockEntry.COLUMN_PRICE + " INTEGER, "
                + StockEntry.COLUMN_QUANTITY + " INTEGER, "
                + StockEntry.COLUMN_IMAGE + " TEXT, "
                + StockEntry.COLUMN_SUPPLIER_NAME + " TEXT, "
                + StockEntry.COLUMN_SUPPLIER_EMAIL + " TEXT);";

        db.execSQL(SQL_CREATE_STOCK_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StockContract.StockEntry.TABLE_NAME);
        onCreate(db);
    }
}
