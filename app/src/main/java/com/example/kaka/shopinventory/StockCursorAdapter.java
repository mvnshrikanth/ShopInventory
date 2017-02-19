package com.example.kaka.shopinventory;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.kaka.shopinventory.data.StockContract.StockEntry;
import com.squareup.picasso.Picasso;

/**
 * Created by Kaka on 2/19/2017.
 */

public class StockCursorAdapter extends CursorAdapter {

    public StockCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ImageView imageView = (ImageView) view.findViewById(R.id.image_view_product);
        TextView textViewName = (TextView) view.findViewById(R.id.text_view_product_name);
        TextView textViewQty = (TextView) view.findViewById(R.id.text_view_product_qty);
        TextView textViewPrice = (TextView) view.findViewById(R.id.text_view_product_price);

        String name = String.valueOf(cursor.getColumnIndexOrThrow(StockEntry.COLUMN_PRODUCT_NAME));
        String imageUrl = String.valueOf(cursor.getColumnIndexOrThrow(StockEntry.COLUMN_IMAGE));
        int quantity = cursor.getColumnIndexOrThrow(StockEntry.COLUMN_QUANTITY);
        int price = cursor.getColumnIndexOrThrow(StockEntry.COLUMN_PRICE);

        if (imageUrl != null) {
            Picasso.with(context)
                    .load(imageUrl)
                    .into(imageView);
        } else {
            imageView.setImageResource(R.drawable.no_image_100x100);
        }

        textViewName.setText(name);
        textViewQty.setText(quantity);
        textViewPrice.setText(price);

    }
}
