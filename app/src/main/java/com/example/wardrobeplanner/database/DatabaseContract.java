package com.example.wardrobeplanner.database;

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
        public static final String COLUMN_SEASON = "season";
        public static final String COLUMN_COLOR = "color";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_USER_ID = "user_id";
    }

    public static class OutfitEntry implements BaseColumns {
        public static final String TABLE_NAME = "outfits";
        public static final String COLUMN_OUTFIT_NAME = "outfit_name";
        public static final String COLUMN_USER_ID = "user_id";
    }

    public static class OutfitClothingEntry implements BaseColumns {
        public static final String TABLE_NAME = "outfit_clothing";
        public static final String COLUMN_OUTFIT_ID = "outfit_id";
        public static final String COLUMN_CLOTHING_ID = "clothing_id";
    }

    public static class UserEntry implements BaseColumns {
        public static final String TABLE_NAME = "users";
        public static final String COLUMN_USERNAME = "username";
        public static final String COLUMN_EMAIL = "email";
        public static final String COLUMN_PASSWORD = "password";
    }
}
