package com.example.kaka.shopinventory;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kaka.shopinventory.data.StockContract.StockEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int STOCK_LOADER = 0;
    StockCursorAdapter mStockCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ListView listView = (ListView) findViewById(R.id.product_list);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mStockCursorAdapter = new StockCursorAdapter(this, null);
        listView.setAdapter(mStockCursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(MainActivity.this, "Clicked on a item" + position, Toast.LENGTH_SHORT).show();
            }
        });

        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                insertDummyStock();
            }
        });

        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }


    private void insertDummyStock() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StockEntry.COLUMN_PRODUCT_NAME, "Computer Keyboard");
        contentValues.put(StockEntry.COLUMN_PRICE, 50);
        contentValues.put(StockEntry.COLUMN_QUANTITY, 5);
        contentValues.put(StockEntry.COLUMN_IMAGE, R.drawable.shop);
        contentValues.put(StockEntry.COLUMN_SUPPLIER_NAME, "Dell Computers");
        contentValues.put(StockEntry.COLUMN_SUPPLIER_EMAIL, "mvnshrikanth@gmail.com");

        Uri uri = getContentResolver().insert(StockEntry.CONTENT_URI, contentValues);
    }

    private void deleteAllStock() {
        int rowsDeleted = getContentResolver().delete(StockEntry.CONTENT_URI, null, null);
        Log.v(LOG_TAG, rowsDeleted + " rows deleted from pet database");
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        String[] projection = {
                StockEntry._ID,
                StockEntry.COLUMN_PRODUCT_NAME,
                StockEntry.COLUMN_PRICE,
                StockEntry.COLUMN_QUANTITY,
                StockEntry.COLUMN_IMAGE,
                StockEntry.COLUMN_SUPPLIER_NAME,
                StockEntry.COLUMN_SUPPLIER_EMAIL
        };
        return new CursorLoader(this,
                StockEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mStockCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockCursorAdapter.swapCursor(null);
    }
}
