package com.example.kaka.shopinventory;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.kaka.shopinventory.data.StockContract.StockEntry;

public class MainActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final int STOCK_LOADER = 0;
    StockCursorAdapter mStockCursorAdapter;
    int lastVisibleItem = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                startActivity(intent);
            }
        });

        final ListView listView = (ListView) findViewById(R.id.product_list);

        View emptyView = findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);

        mStockCursorAdapter = new StockCursorAdapter(this, null);
        listView.setAdapter(mStockCursorAdapter);

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if (scrollState == 0) return;
                final int currentFirstVisibleItem = view.getFirstVisiblePosition();

                if (currentFirstVisibleItem > lastVisibleItem) {
                    floatingActionButton.show();
                } else if (currentFirstVisibleItem < lastVisibleItem) {
                    floatingActionButton.hide();
                }
                lastVisibleItem = currentFirstVisibleItem;
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {

            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, DetailActivity.class);
                Uri currentStockUri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
                intent.setData(currentStockUri);
                startActivity(intent);
            }
        });

        getLoaderManager().initLoader(STOCK_LOADER, null, this);
    }


    private void insertDummyStock() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(StockEntry.COLUMN_PRODUCT_NAME, "Computer Keyboard");
        contentValues.put(StockEntry.COLUMN_PRICE, "50.23");
        contentValues.put(StockEntry.COLUMN_QUANTITY, 5);
        contentValues.put(StockEntry.COLUMN_IMAGE, "android.resource://com.example.kaka.shopinventory/drawable/no_image_100x100");
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_data:
                insertDummyStock();
                return true;
            case R.id.action_delte_dummy_data:
                deleteAllStock();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void sellProduct(int id, String qty) {

        int quantity = Integer.parseInt(qty);

        if (quantity <= 0) {
            Toast.makeText(this, "No more items in store to sell", Toast.LENGTH_SHORT).show();
        } else {
            quantity--;

            ContentValues contentValues = new ContentValues();
            contentValues.put(StockEntry.COLUMN_QUANTITY, String.valueOf(quantity));
            Uri uri = ContentUris.withAppendedId(StockEntry.CONTENT_URI, id);
            int rowsAffected = getContentResolver().update(uri, contentValues, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error updating product quantity", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Item Sold", Toast.LENGTH_SHORT).show();
            }
        }
    }

}
