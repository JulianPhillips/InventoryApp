package com.example.www.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;


public class InventoryProvider extends ContentProvider {



    private InventoryHelperDb mDbHelper;

    private static final int PRODUCT = 100;


    private static final int PRODUCT_ID = 101;



    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);


    static {
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY, PRODUCT);
        sUriMatcher.addURI(InventoryContract.CONTENT_AUTHORITY,InventoryContract.PATH_INVENTORY+"/#", PRODUCT_ID);
    }
    @Override
    public boolean onCreate() {
        mDbHelper = new InventoryHelperDb(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor = null;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                cursor=database.query(InventoryContract.InventoryEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,null,null,sortOrder);
                break;
            case  PRODUCT_ID:

                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };


                cursor = database.query(InventoryContract.InventoryEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(),uri);

        return cursor;
    }


    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }
    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private Uri insertProduct(Uri uri, ContentValues values) {
        // Check that the name is not null
        String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
        if (name == null) {
            Log.e(LOG_TAG, "Product requires a name ");
            throw new IllegalArgumentException("Product requires a name");
        }

        Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
        if (price != null && price <= 0) {
            Log.e(LOG_TAG, "Product must have a price greater than 0 ");
            throw new IllegalArgumentException("Must have a price greater than 0");
        }

        Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
        if(quantity!=null && quantity<0){
            Log.e(LOG_TAG, "You can't add a product thats out of stock");
            throw new IllegalArgumentException("You can't add a product thats out of stock");

        }
        String images = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE);
        if(images==null ){
            Log.e(LOG_TAG, "You must have an image");
            throw new IllegalArgumentException("you must have an image");

        }
        String phoneNumber = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER);
        if (phoneNumber == null) {
            Log.e(LOG_TAG, "Product requires a phone number ");
            throw new IllegalArgumentException("Product requires a phone number");
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        long id = database.insert(InventoryContract.InventoryEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri,null);

        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return updateInvetory(uri, contentValues, selection, selectionArgs);
            case  PRODUCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                return updateInvetory(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }


    private int updateInvetory(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME)) {
            String name = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_NAME);
            if (name == null) {
                Log.e(LOG_TAG, "Product requires a name ");
                throw new IllegalArgumentException("Product requires a name");
            }
        }

        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY)) {

            Integer quantity = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_QUANTITY);
            if (quantity != null && quantity < 0) {
                Log.e(LOG_TAG, "Product cant be less than zero");
                throw new IllegalArgumentException("Product cant be less than zero");
            }
        }

        if(values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE)){
            Integer price = values.getAsInteger(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PRICE);
            if(price!=null && price <=0){
                Log.e(LOG_TAG, "Product must cost more than zero ");
                throw new IllegalArgumentException("Product must cost more than zero");
            }
        }
        if (values.containsKey(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER)) {
            String phoneNumber = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_PHONENUMBER);
            if (phoneNumber == null) {
                Log.e(LOG_TAG, "Product requires a phone number");
                throw new IllegalArgumentException("Product requires a phone number");
            }
        }
        String images = values.getAsString(InventoryContract.InventoryEntry.COLUMN_INVENTORY_IMAGE);
        if(images==null ){
            Log.e(LOG_TAG, "You must have an image");
            throw new IllegalArgumentException("you must have an image");

        }
        if (values.size() == 0) {
            return 0;
        }
        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        // Returns the number of database rows affected by the update statement
        int rowsUpdated = database.update(InventoryContract.InventoryEntry.TABLE_NAME,values,selection,selectionArgs);
        if(rowsUpdated!=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                rowsDeleted =database.delete(InventoryContract.InventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            case  PRODUCT_ID:
                selection = InventoryContract.InventoryEntry._ID + "=?";
                selectionArgs = new String[] { String.valueOf(ContentUris.parseId(uri)) };
                rowsDeleted = database.delete(InventoryContract.InventoryEntry.TABLE_NAME,selection,selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        if(rowsDeleted !=0){
            getContext().getContentResolver().notifyChange(uri,null);
        }
        return rowsDeleted;
    }


    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCT:
                return InventoryContract.InventoryEntry.CONTENT_LIST_TYPE;
            case  PRODUCT_ID:
                return InventoryContract.InventoryEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
