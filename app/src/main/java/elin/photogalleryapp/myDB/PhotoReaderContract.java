package elin.photogalleryapp.myDB;

import android.provider.BaseColumns;

public final class PhotoReaderContract {
    private PhotoReaderContract() {}

    public static class FeedEntry implements BaseColumns {
        public static final String TABLE_NAME = "Gallery";
        public static final String COLUMN_NAME_PHOTOPATH = "PhotoPath";
        public static final String COLUMN_NAME_DATE = "Date";
        public static final String COLUMN_NAME_CAPTION = "Caption";
        public static final String COLUMN_NAME_LAT = "Latitude";
        public static final String COLUMN_NAME_LONG = "Longitude";


    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + FeedEntry.TABLE_NAME + " (" +
                    FeedEntry._ID + " INTEGER PRIMARY KEY," +
                    FeedEntry.COLUMN_NAME_PHOTOPATH + " TEXT," +
                    FeedEntry.COLUMN_NAME_DATE + " TEXT," +
                    FeedEntry.COLUMN_NAME_CAPTION + " TEXT," +
                    FeedEntry.COLUMN_NAME_LAT + " INTEGER," +
                    FeedEntry.COLUMN_NAME_LONG + " INTEGER)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + FeedEntry.TABLE_NAME;
}
