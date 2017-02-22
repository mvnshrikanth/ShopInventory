package com.example.kaka.shopinventory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaka.shopinventory.data.StockContract.StockEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by Kaka on 2/19/2017.
 */

public class StockCursorAdapter extends CursorAdapter {

    private final MainActivity mainActivity;

    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c);
        this.mainActivity = (MainActivity) context;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, final Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_product);
        TextView textViewName = (TextView) view.findViewById(R.id.text_view_product_name);
        TextView textViewQty = (TextView) view.findViewById(R.id.text_view_product_qty);
        TextView textViewPrice = (TextView) view.findViewById(R.id.text_view_product_price);
        ImageButton imageButtonSell = (ImageButton) view.findViewById(R.id.img_btn_view_sell_item);

        int idColumnIndex = cursor.getColumnIndex(StockEntry._ID);
        int nameColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRODUCT_NAME);
        int imageColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_IMAGE);
        int qtyColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_QUANTITY);
        int prcColumnIndex = cursor.getColumnIndex(StockEntry.COLUMN_PRICE);

        final int id = cursor.getInt(idColumnIndex);
        String name = cursor.getString(nameColumnIndex);
        String imageUrl = cursor.getString(imageColumnIndex);
        final String quantity = cursor.getString(qtyColumnIndex);
        String price = cursor.getString(prcColumnIndex);

        if (imageUrl != null) {
            Picasso.with(context)
                    .load(imageUrl)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.no_image_100x100);
        }

        textViewName.setText(name);
        textViewQty.setText(String.valueOf(quantity));
        textViewPrice.setText("$" + price);

        imageButtonSell.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mainActivity.sellProduct(id, quantity);
            }
        });
    }
}
