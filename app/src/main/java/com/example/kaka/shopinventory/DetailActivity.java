package com.example.kaka.shopinventory;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;

import com.example.kaka.shopinventory.data.StockContract.StockEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by Kaka on 2/19/2017.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_STOCK_LOADER = 0;

    private Uri mCurrentStockUri;

    private EditText nameEditText;
    private EditText sellerNameEditText;
    private EditText emailEditText;
    private EditText qtyEditText;
    private EditText costEditText;
    private final TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

            if (!s.toString().matches("^\\$(\\d{1,3}(\\,\\d{3})*|(\\d+))(\\.\\d{2})?$")) {
                String userInput = "" + s.toString().replaceAll("[^\\d]", "");
                StringBuilder cashAmountBuilder = new StringBuilder(userInput);

                while (cashAmountBuilder.length() > 3 && cashAmountBuilder.charAt(0) == '0') {
                    cashAmountBuilder.deleteCharAt(0);
                }
                while (cashAmountBuilder.length() < 3) {
                    cashAmountBuilder.insert(0, '0');
                }
                cashAmountBuilder.insert(cashAmountBuilder.length() - 2, '.');

                costEditText.removeTextChangedListener(this);
                costEditText.setText(cashAmountBuilder.toString());

                costEditText.setTextKeepState("$" + cashAmountBuilder.toString());
                Selection.setSelection(costEditText.getText(), cashAmountBuilder.toString().length() + 1);

                costEditText.addTextChangedListener(this);
            }

        }
    };
    private ImageButton imageButton;
    private Button costIncButton;
    private Button costDecButton;
    private Button qtyIncButton;
    private Button qtyDecButton;
    private Button ordrButton;
    private Button delButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        mCurrentStockUri = intent.getData();

        if (mCurrentStockUri == null) {
            setTitle("Add a Product");
        } else {
            setTitle("Edit a Product");
            getLoaderManager().initLoader(EXISTING_STOCK_LOADER, null, this);
        }

        nameEditText = (EditText) findViewById(R.id.edit_view_product_name);
        sellerNameEditText = (EditText) findViewById(R.id.edit_view_product_suplr_name);
        emailEditText = (EditText) findViewById(R.id.edit_view_product_suplr_email);
        costEditText = (EditText) findViewById(R.id.edit_view_product_price);
        qtyEditText = (EditText) findViewById(R.id.edit_view_product_qty);
        imageButton = (ImageButton) findViewById(R.id.image_btn_view_edit_product);
        costDecButton = (Button) findViewById(R.id.btn_view_dec_price);
        costIncButton = (Button) findViewById(R.id.btn_view_add_price);
        qtyIncButton = (Button) findViewById(R.id.btn_view_add_qty);
        qtyDecButton = (Button) findViewById(R.id.btn_view_dec_qty);
        ordrButton = (Button) findViewById(R.id.btn_view_ordr_frm_selr);
        delButton = (Button) findViewById(R.id.btn_view_dlt_prd);

        costEditText.addTextChangedListener(textWatcher);
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
                mCurrentStockUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
            int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME);
            int imageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_IMAGE);
            int qtyColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY);
            int prcColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRICE);
            int emailColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_EMAIL);
            int suppNameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_SUPPLIER_NAME);

            int id = cursor.getInt(idColumnIndex);
            String name = cursor.getString(nameColumnIndex);
            String imageUrl = cursor.getString(imageColumnIndex);
            int quantity = cursor.getInt(qtyColumnIndex);
            String price = cursor.getString(prcColumnIndex);
            String email = cursor.getString(emailColumnIndex);
            String suppName = cursor.getString(suppNameColumnIndex);

            if (imageUrl != null) {
                Picasso.with(getApplicationContext())
                        .load(imageUrl)
                        .into(imageButton);
            } else {
                imageButton.setImageResource(R.drawable.no_image_100x100);
            }

            nameEditText.setText(name);
            qtyEditText.setText(String.valueOf(quantity));
            costEditText.setText("$" + String.valueOf(price));
            emailEditText.setText(email);
            sellerNameEditText.setText(suppName);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        imageButton.setImageResource(R.drawable.no_image_100x100);
        nameEditText.setText("");
        qtyEditText.setText(String.valueOf(0));
        costEditText.setText(String.valueOf(0));
        emailEditText.setText("");
        sellerNameEditText.setText("");
    }
}
