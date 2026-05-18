package com.example.wardrobeplanner.database;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.wardrobeplanner.models.ClothingItem;
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

}