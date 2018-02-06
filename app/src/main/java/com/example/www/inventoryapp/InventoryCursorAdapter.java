package com.example.www.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.www.inventoryapp.data.InventoryContract.InventoryEntry;


public class InventoryCursorAdapter extends CursorAdapter {


    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }


    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        ImageView productImageView = (ImageView) view.findViewById(R.id.product_image);
        TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);
        Button saleButton = (Button) view.findViewById(R.id.sale_button);
        TextView phoneNumberTextView = (TextView) view.findViewById(R.id.phoneNumber);
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_NAME);
        int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PRICE);
        int productColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_IMAGE);
        int phonenumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_INVENTORY_PHONENUMBER);
        String phoneNumber = cursor.getString(phonenumberColumnIndex);
        String image = cursor.getString(productColumnIndex);
        Uri currentProductImage = Uri.parse(image);
        productImageView.setImageURI(currentProductImage);
        Integer productQuantity = cursor.getInt(quantityColumnIndex);
        String productName = cursor.getString(nameColumnIndex);
        Integer productPrice = cursor.getInt(priceColumnIndex);
        if (TextUtils.isEmpty(productName)) {
            productName = context.getString(R.string.unknown_name);
        }
        quantityTextView.setText("Quantity: "+ Integer.toString(productQuantity));
        nameTextView.setText("Name: "+productName);
        priceTextView.setText("Price: "+Integer.toString(productPrice));
        phoneNumberTextView.setText("Phone Number: "+phoneNumber);
        final int productId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        String currentQuantity = cursor.getString(quantityColumnIndex);
        final int QuantityInt = Integer.valueOf(currentQuantity);

        saleButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (QuantityInt > 0) {
                    int newQuantity = QuantityInt - 1;
                    Uri quantitytUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, productId );

                    ContentValues values = new ContentValues();
                    values.put(InventoryEntry.COLUMN_INVENTORY_QUANTITY, newQuantity);
                    context.getContentResolver().update(quantitytUri, values, null, null);
                } else {
                    Toast.makeText(context, "Sold out!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}