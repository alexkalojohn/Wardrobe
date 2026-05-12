package com.example.myapplication.database;

import android.provider.BaseColumns;

public final class DatabaseContract {
    // Ιδιωτικός constructor για να μην μπορεί να δημιουργηθεί αντικείμενο της κλάσης
    private DatabaseContract() {}

    /* Ορισμός του πίνακα για τα Ρούχα */
    public static class ClothingEntry implements BaseColumns {
        public static final String TABLE_NAME = "clothing";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_CATEGORY = "category";
        public static final String COLUMN_IMAGE_URI = "image_uri";
    }

    public static class OutfitEntry implements BaseColumns {
        public static final String TABLE_NAME = "outfits";
        public static final String COLUMN_OUTFIT_NAME = "outfit_name";
    }

    // Πρόσθεσε αυτό μέσα στην κλάση DatabaseContract
    public static class OutfitClothingEntry implements BaseColumns {
        public static final String TABLE_NAME = "outfit_clothing";
        public static final String COLUMN_OUTFIT_ID = "outfit_id";
        public static final String COLUMN_CLOTHING_ID = "clothing_id";
    }
}