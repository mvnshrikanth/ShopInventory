package com.example.kaka.shopinventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Selection;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.kaka.shopinventory.data.StockContract.StockEntry;
import com.squareup.picasso.Picasso;

import static android.text.TextUtils.isEmpty;

/**
 * Created by Kaka on 2/19/2017.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_STOCK_LOADER = 0;
    int id;
    private Uri mCurrentStockUri;
    private Uri mImageUri;
    private ImageButton imageButton;
    private Button costIncButton;
    private Button costDecButton;
    private Button qtyIncButton;
    private Button qtyDecButton;
    private Button ordrButton;
    private Button delButton;
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
    private int PICK_IMAGE_REQUEST = 1;
    private boolean mStockDataChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mStockDataChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

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

        Intent intent = getIntent();
        mCurrentStockUri = intent.getData();

        if (mCurrentStockUri == null) {
            setTitle("Add a Product");
            delButton.setVisibility(View.GONE);
        } else {
            setTitle("Edit a Product");
            getLoaderManager().initLoader(EXISTING_STOCK_LOADER, null, this);
        }

        costEditText.addTextChangedListener(textWatcher);

        nameEditText.setOnTouchListener(mTouchListener);
        sellerNameEditText.setOnTouchListener(mTouchListener);
        emailEditText.setOnTouchListener(mTouchListener);
        costEditText.setOnTouchListener(mTouchListener);
        qtyEditText.setOnTouchListener(mTouchListener);

        costIncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float cost = 0;

                if (!isEmpty(costEditText.getText())) {
                    cost = Float.parseFloat(costEditText.getText().toString().replace("$", ""));
                }
                cost++;
                costEditText.setText(String.valueOf(cost));
                mStockDataChanged = true;
            }
        });

        costDecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                float cost = 0;
                if (!isEmpty(costEditText.getText())) {
                    cost = Float.parseFloat(costEditText.getText().toString().replace("$", ""));
                }
                if (cost <= 0) {
                    Toast.makeText(DetailActivity.this, "Cost cannot be less than 0", Toast.LENGTH_SHORT).show();
                } else {
                    cost--;
                    costEditText.setText(String.valueOf(cost));
                    mStockDataChanged = true;
                }
            }
        });

        qtyIncButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;

                if (!isEmpty(qtyEditText.getText())) {
                    quantity = Integer.parseInt(qtyEditText.getText().toString());
                }
                quantity++;
                qtyEditText.setText(String.valueOf(quantity));
                mStockDataChanged = true;
            }
        });


        qtyDecButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = 0;

                if (!isEmpty(qtyEditText.getText())) {
                    quantity = Integer.parseInt(qtyEditText.getText().toString());
                }
                if (quantity <= 0) {
                    Toast.makeText(DetailActivity.this, "Quantity cannot be less than 0", Toast.LENGTH_SHORT).show();
                } else {
                    quantity--;
                    qtyEditText.setText(String.valueOf(quantity));
                    mStockDataChanged = true;
                }
            }
        });

        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent;

                if (Build.VERSION.SDK_INT < 19) {
                    intent = new Intent(Intent.ACTION_GET_CONTENT);
                } else {
                    intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
                    intent.addCategory(Intent.CATEGORY_OPENABLE);
                }

                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);

            }
        });

        ordrButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(android.content.Intent.ACTION_SENDTO);
                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + emailEditText.getText().toString().trim()));
                intent.putExtra(android.content.Intent.EXTRA_SUBJECT, "New order");
                String bodyMessage = "Please send us as soon as possible more " +
                        nameEditText.getText().toString().trim() +
                        "!!!";
                intent.putExtra(android.content.Intent.EXTRA_TEXT, bodyMessage);
                startActivity(intent);
            }
        });

        delButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            mImageUri = data.getData();
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = getContentResolver().query(mImageUri, projection, null, null, null);
            assert cursor != null;
            cursor.moveToNext();
            cursor.close();

            imageButton.setImageURI(mImageUri);
            imageButton.invalidate();
            mStockDataChanged = true;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveStock();
                return true;
            case android.R.id.home:
                if (!mStockDataChanged) {
                    NavUtils.navigateUpFromSameTask(DetailActivity.this);
                    return true;
                }
                DialogInterface.OnClickListener discardButtonClickListner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                };
        }
        return super.onOptionsItemSelected(item);
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

            id = cursor.getInt(idColumnIndex);
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

    @Override
    public void onBackPressed() {
        if (!mStockDataChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListner = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        showUnsavedChangesDialog(discardButtonClickListner);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListner) {

        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setMessage("Discard your changes and quit editing?");
        builder.setPositiveButton("Discard", discardButtonClickListner);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        android.app.AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this product");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteStock();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void saveStock() {
        String prodName = nameEditText.getText().toString().trim();
        String sellerName = sellerNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String cost = costEditText.getText().toString().trim().replace("$", "");
        String qty = qtyEditText.getText().toString().trim();
        //If mImageUri is null then the user did not select any iamge then take the Uri of the exisiting no_image_100x100.png
        if (mImageUri == null) {
            mImageUri = Uri.parse("android.resource://com.example.kaka.shopinventory/drawable/no_image_100x100");
        }
        if (isEmpty(prodName) || isEmpty(sellerName)
                || isEmpty(email) || isEmpty(cost) || isEmpty(qty) || mImageUri == null) {
            Toast.makeText(this, "Please fill all the data", Toast.LENGTH_SHORT).show();
            return;
        }
        ContentValues contentValues = new ContentValues();
        contentValues.put(StockEntry.COLUMN_PRODUCT_NAME, prodName);
        contentValues.put(StockEntry.COLUMN_PRICE, cost);
        contentValues.put(StockEntry.COLUMN_QUANTITY, qty);
        contentValues.put(StockEntry.COLUMN_IMAGE, mImageUri.toString());
        contentValues.put(StockEntry.COLUMN_SUPPLIER_NAME, sellerName);
        contentValues.put(StockEntry.COLUMN_SUPPLIER_EMAIL, email);

        if (mCurrentStockUri == null) {
            Uri uri = getContentResolver().insert(StockEntry.CONTENT_URI, contentValues);

            if (uri == null) {
                Toast.makeText(this, "Error saving the product", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = getContentResolver().update(mCurrentStockUri, contentValues, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, "Error saving the product", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

    private void deleteStock() {
        if (mCurrentStockUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentStockUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, "Deletion failed", Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }
}
