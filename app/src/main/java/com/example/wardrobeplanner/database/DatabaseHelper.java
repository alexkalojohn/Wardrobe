package com.example.wardrobeplanner.database;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.wardrobeplanner.models.ClothingItem;
import com.example.wardrobeplanner.models.Outfit;
import com.example.wardrobeplanner.models.User;
import com.example.wardrobeplanner.database.DatabaseContract.OutfitEntry;
import com.example.wardrobeplanner.database.DatabaseContract.OutfitClothingEntry;
import com.example.wardrobeplanner.database.DatabaseContract.UserEntry;

// Κάνουμε import το συμβόλαιο που φτιάξαμε πριν
import com.example.wardrobeplanner.database.DatabaseContract.ClothingEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 4;
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

    private static final String SQL_CREATE_USERS =
            "CREATE TABLE " + UserEntry.TABLE_NAME + " (" +
                    UserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    UserEntry.COLUMN_USERNAME + " TEXT UNIQUE NOT NULL," +
                    UserEntry.COLUMN_EMAIL + " TEXT UNIQUE NOT NULL," +
                    UserEntry.COLUMN_PASSWORD + " TEXT NOT NULL)";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Εκτελείται την πρώτη φορά που ανοίγει η εφαρμογή
        db.execSQL(SQL_CREATE_ENTRIES);
        db.execSQL(SQL_CREATE_OUTFITS);
        db.execSQL(SQL_CREATE_OUTFIT_CLOTHING);
        db.execSQL(SQL_CREATE_USERS);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + OutfitClothingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + OutfitEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + ClothingEntry.TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + UserEntry.TABLE_NAME);
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

    public Outfit getOutfitById(int outfitId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(
                OutfitEntry.TABLE_NAME,
                null,
                OutfitEntry._ID + " = ?",
                new String[]{String.valueOf(outfitId)},
                null,
                null,
                null
        );

        Outfit outfit = null;
        if (cursor.moveToFirst()) {
            String outfitName = cursor.getString(cursor.getColumnIndexOrThrow(OutfitEntry.COLUMN_OUTFIT_NAME));
            outfit = new Outfit(outfitId, outfitName, getClothingItemsForOutfit(outfitId));
        }

        cursor.close();
        return outfit;
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

    public boolean updateOutfit(int outfitId, String outfitName, List<Integer> clothingIds) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();

        try {
            ContentValues outfitValues = new ContentValues();
            outfitValues.put(OutfitEntry.COLUMN_OUTFIT_NAME, outfitName);

            int updatedRows = db.update(
                    OutfitEntry.TABLE_NAME,
                    outfitValues,
                    OutfitEntry._ID + " = ?",
                    new String[]{String.valueOf(outfitId)}
            );

            if (updatedRows <= 0) {
                return false;
            }

            db.delete(
                    OutfitClothingEntry.TABLE_NAME,
                    OutfitClothingEntry.COLUMN_OUTFIT_ID + " = ?",
                    new String[]{String.valueOf(outfitId)}
            );

            for (Integer clothingId : clothingIds) {
                ContentValues linkValues = new ContentValues();
                linkValues.put(OutfitClothingEntry.COLUMN_OUTFIT_ID, outfitId);
                linkValues.put(OutfitClothingEntry.COLUMN_CLOTHING_ID, clothingId);
                db.insert(OutfitClothingEntry.TABLE_NAME, null, linkValues);
            }

            db.setTransactionSuccessful();
            return true;
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
     * Update an existing clothing item.
     * @param clothingItem the item to update (must have valid id)
     * @return true if update was successful, false otherwise
     */
    public boolean updateClothing(ClothingItem clothingItem) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(ClothingEntry.COLUMN_NAME, clothingItem.getName());
        values.put(ClothingEntry.COLUMN_CATEGORY, clothingItem.getCategory());
        values.put(ClothingEntry.COLUMN_COLOR, clothingItem.getColor());
        values.put(ClothingEntry.COLUMN_SEASON, clothingItem.getSeason());
        values.put(ClothingEntry.COLUMN_DESCRIPTION, clothingItem.getDescription());
        values.put(ClothingEntry.COLUMN_IMAGE_URI, clothingItem.getImageUri());

        String selection = ClothingEntry._ID + " = ?";
        String[] selectionArgs = { String.valueOf(clothingItem.getId()) };

        int count = db.update(ClothingEntry.TABLE_NAME, values, selection, selectionArgs);
        return count > 0;
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

    /**
     * Hash a password using SHA-256
     */
    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password;
        }
    }

    /**
     * Register a new user in the database.
     * @param username the username
     * @param email the email
     * @param password the raw password (will be hashed)
     * @return true if registration was successful, false otherwise
     */
    public boolean registerUser(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(UserEntry.COLUMN_USERNAME, username);
        values.put(UserEntry.COLUMN_EMAIL, email);
        values.put(UserEntry.COLUMN_PASSWORD, hashPassword(password));

        long result = db.insert(UserEntry.TABLE_NAME, null, values);
        return result != -1;
    }

    /**
     * Authenticate a user by username/email and password.
     * @param identifier username or email
     * @param password raw password
     * @return the User object if authenticated, null otherwise
     */
    public User loginUser(String identifier, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        String hashedPassword = hashPassword(password);

        String selection = "(" + UserEntry.COLUMN_USERNAME + " = ? OR " + UserEntry.COLUMN_EMAIL + " = ?) AND " +
                UserEntry.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = { identifier, identifier, hashedPassword };

        Cursor cursor = db.query(
                UserEntry.TABLE_NAME,
                null,
                selection,
                selectionArgs,
                null,
                null,
                null
        );

        User user = null;
        if (cursor != null && cursor.moveToFirst()) {
            user = new User(
                    cursor.getInt(cursor.getColumnIndexOrThrow(UserEntry._ID)),
                    cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_USERNAME)),
                    cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_EMAIL)),
                    cursor.getString(cursor.getColumnIndexOrThrow(UserEntry.COLUMN_PASSWORD))
            );
            cursor.close();
        }

        return user;
    }

    /**
     * Check if a username already exists.
     * @param username the username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { UserEntry._ID };
        String selection = UserEntry.COLUMN_USERNAME + " = ?";
        String[] selectionArgs = { username };

        Cursor cursor = db.query(UserEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }

    /**
     * Check if an email already exists.
     * @param email the email to check
     * @return true if exists, false otherwise
     */
    public boolean emailExists(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { UserEntry._ID };
        String selection = UserEntry.COLUMN_EMAIL + " = ?";
        String[] selectionArgs = { email };

        Cursor cursor = db.query(UserEntry.TABLE_NAME, columns, selection, selectionArgs, null, null, null);
        boolean exists = cursor != null && cursor.getCount() > 0;
        if (cursor != null) cursor.close();
        return exists;
    }
}
