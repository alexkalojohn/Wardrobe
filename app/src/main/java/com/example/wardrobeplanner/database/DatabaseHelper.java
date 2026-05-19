package com.example.wardrobeplanner.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.wardrobeplanner.models.ClothingItem;
import com.example.wardrobeplanner.models.Outfit;
import com.example.wardrobeplanner.database.DatabaseContract.OutfitEntry;
import com.example.wardrobeplanner.database.DatabaseContract.OutfitClothingEntry;

// Κάνουμε import το συμβόλαιο που φτιάξαμε πριν
import com.example.wardrobeplanner.database.DatabaseContract.ClothingEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "Wardrobe.db";

    private static final String SQL_CREATE_OUTFITS =
            "CREATE TABLE " + OutfitEntry.TABLE_NAME + " (" +
                    OutfitEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    OutfitEntry.COLUMN_OUTFIT_NAME + " TEXT)";

    private static final String SQL_CREATE_OUTFIT_CLOTHING =
            "CREATE TABLE " + OutfitClothingEntry.TABLE_NAME + " (" +
                    OutfitClothingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    OutfitClothingEntry.COLUMN_OUTFIT_ID + " INTEGER," +
                    OutfitClothingEntry.COLUMN_CLOTHING_ID + " INTEGER," +
                    "FOREIGN KEY(" + OutfitClothingEntry.COLUMN_OUTFIT_ID + ") REFERENCES " + OutfitEntry.TABLE_NAME + "(" + OutfitEntry._ID + ")," +
                    "FOREIGN KEY(" + OutfitClothingEntry.COLUMN_CLOTHING_ID + ") REFERENCES " + ClothingEntry.TABLE_NAME + "(" + ClothingEntry._ID + "))";


    // Εντολή SQL για τη δημιουργία του πίνακα
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ClothingEntry.TABLE_NAME + " (" +
                    ClothingEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    ClothingEntry.COLUMN_NAME + " TEXT," +
                    ClothingEntry.COLUMN_CATEGORY + " TEXT," +
                    ClothingEntry.COLUMN_IMAGE_URI + " TEXT," +
                    ClothingEntry.COLUMN_SEASON + " TEXT," +
                    ClothingEntry.COLUMN_COLOR + " TEXT," +
                    ClothingEntry.COLUMN_DESCRIPTION + " TEXT)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Εκτελείται την πρώτη φορά που ανοίγει η εφαρμογή
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_OUTFITS);
        db.execSQL(SQL_CREATE_OUTFIT_CLOTHING);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + OutfitClothingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OutfitEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ClothingEntry.TABLE_NAME);
        onCreate(db);
    }

    public long addClothingItem(String name, String category, String imageUri,
                                 String season, String color, String description) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(DatabaseContract.ClothingEntry.COLUMN_NAME, name);
        values.put(DatabaseContract.ClothingEntry.COLUMN_CATEGORY, category);
        values.put(DatabaseContract.ClothingEntry.COLUMN_IMAGE_URI, imageUri);
        values.put(DatabaseContract.ClothingEntry.COLUMN_SEASON, season);
        values.put(DatabaseContract.ClothingEntry.COLUMN_COLOR, color);
        values.put(DatabaseContract.ClothingEntry.COLUMN_DESCRIPTION, description);

        // Επιστρέφει το ID της γραμμής που δημιουργήθηκε
        return db.insert(DatabaseContract.ClothingEntry.TABLE_NAME, null, values);
    }

    public List<ClothingItem> getAllClothing() {
        List<ClothingItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + ClothingEntry.TABLE_NAME, null);

        if (cursor.moveToFirst()) {
            do {
                ClothingItem item = new ClothingItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ClothingEntry._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_SEASON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_COLOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_DESCRIPTION))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    public long addOutfit(String outfitName, List<Integer> clothingIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues outfitValues = new ContentValues();
            outfitValues.put(OutfitEntry.COLUMN_OUTFIT_NAME, outfitName);

            long outfitId = db.insert(OutfitEntry.TABLE_NAME, null, outfitValues);
            if (outfitId == -1) {
                return -1;
            }

            for (Integer clothingId : clothingIds) {
                ContentValues linkValues = new ContentValues();
                linkValues.put(OutfitClothingEntry.COLUMN_OUTFIT_ID, outfitId);
                linkValues.put(OutfitClothingEntry.COLUMN_CLOTHING_ID, clothingId);
                db.insert(OutfitClothingEntry.TABLE_NAME, null, linkValues);
            }

            db.setTransactionSuccessful();
            return outfitId;
        } finally {
            db.endTransaction();
        }
    }

    public List<Outfit> getAllOutfits() {
        List<Outfit> outfits = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(
                OutfitEntry.TABLE_NAME,
                null,
                null,
                null,
                null,
                null,
                OutfitEntry._ID + " DESC"
        );

        if (cursor.moveToFirst()) {
            do {
                int outfitId = cursor.getInt(cursor.getColumnIndexOrThrow(OutfitEntry._ID));
                String outfitName = cursor.getString(cursor.getColumnIndexOrThrow(OutfitEntry.COLUMN_OUTFIT_NAME));
                outfits.add(new Outfit(outfitId, outfitName, getClothingItemsForOutfit(outfitId)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        return outfits;
    }

    public List<ClothingItem> getClothingItemsForOutfit(int outfitId) {
        List<ClothingItem> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT c.* FROM " + ClothingEntry.TABLE_NAME + " c " +
                "INNER JOIN " + OutfitClothingEntry.TABLE_NAME + " oc " +
                "ON c." + ClothingEntry._ID + " = oc." + OutfitClothingEntry.COLUMN_CLOTHING_ID + " " +
                "WHERE oc." + OutfitClothingEntry.COLUMN_OUTFIT_ID + " = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(outfitId)});

        if (cursor.moveToFirst()) {
            do {
                ClothingItem item = new ClothingItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ClothingEntry._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_SEASON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_COLOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_DESCRIPTION))
                );
                itemList.add(item);
            } while (cursor.moveToNext());
        }

        cursor.close();
        return itemList;
    }

    public void deleteOutfit(int outfitId) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(
                    OutfitClothingEntry.TABLE_NAME,
                    OutfitClothingEntry.COLUMN_OUTFIT_ID + " = ?",
                    new String[]{String.valueOf(outfitId)}
            );
            db.delete(
                    OutfitEntry.TABLE_NAME,
                    OutfitEntry._ID + " = ?",
                    new String[]{String.valueOf(outfitId)}
            );
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    /**
     * Insert a clothing item into the database.
     * @param clothingItem the item to insert
     * @return true if insertion was successful, false otherwise
     */
    public boolean insertClothing(ClothingItem clothingItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ClothingEntry.COLUMN_NAME, clothingItem.getName());
        values.put(ClothingEntry.COLUMN_CATEGORY, clothingItem.getCategory());
        values.put(ClothingEntry.COLUMN_COLOR, clothingItem.getColor());
        values.put(ClothingEntry.COLUMN_SEASON, clothingItem.getSeason());
        values.put(ClothingEntry.COLUMN_DESCRIPTION, clothingItem.getDescription());
        values.put(ClothingEntry.COLUMN_IMAGE_URI, clothingItem.getImageUri());

        long result = db.insert(ClothingEntry.TABLE_NAME, null, values);
        return result != -1;
    }

    /**
     * Retrieve all clothing items from the database.
     * @return list of all ClothingItem objects
     */
    public List<ClothingItem> getAllClothes() {
        List<ClothingItem> clothingList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ClothingEntry._ID,
                ClothingEntry.COLUMN_NAME,
                ClothingEntry.COLUMN_CATEGORY,
                ClothingEntry.COLUMN_COLOR,
                ClothingEntry.COLUMN_SEASON,
                ClothingEntry.COLUMN_DESCRIPTION,
                ClothingEntry.COLUMN_IMAGE_URI
        };

        Cursor cursor = db.query(
                ClothingEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        if (cursor != null && cursor.moveToFirst()) {
            do {
                ClothingItem item = new ClothingItem(
                        cursor.getInt(cursor.getColumnIndexOrThrow(ClothingEntry._ID)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_NAME)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_CATEGORY)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_IMAGE_URI)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_SEASON)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_COLOR)),
                        cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_DESCRIPTION))
                );
                clothingList.add(item);
            } while (cursor.moveToNext());
            cursor.close();
        }

        return clothingList;
    }

    /**
     * Delete a clothing item by its ID.
     * @param id the clothing item ID
     * @return the number of rows affected
     */
    public int deleteClothing(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = ClothingEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };
        return db.delete(ClothingEntry.TABLE_NAME, selection, selectionArgs);
    }

    /**
     * Retrieve a single clothing item by its ID.
     * @param id the clothing item ID
     * @return the ClothingItem object, or null if not found
     */
    public ClothingItem getClothingById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String[] projection = {
                ClothingEntry._ID,
                ClothingEntry.COLUMN_NAME,
                ClothingEntry.COLUMN_CATEGORY,
                ClothingEntry.COLUMN_COLOR,
                ClothingEntry.COLUMN_SEASON,
                ClothingEntry.COLUMN_DESCRIPTION,
                ClothingEntry.COLUMN_IMAGE_URI
        };

        String selection = ClothingEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(id) };

        Cursor cursor = db.query(
                ClothingEntry.TABLE_NAME,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        ClothingItem item = null;
        if (cursor != null && cursor.moveToFirst()) {
            item = new ClothingItem(
                    cursor.getInt(cursor.getColumnIndexOrThrow(ClothingEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_NAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_CATEGORY)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_IMAGE_URI)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_SEASON)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_COLOR)),
                    cursor.getString(cursor.getColumnIndexOrThrow(ClothingEntry.COLUMN_DESCRIPTION))
            );
            cursor.close();
        }

        return item;
    }

}
