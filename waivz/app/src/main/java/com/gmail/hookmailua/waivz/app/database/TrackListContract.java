package com.gmail.hookmailua.waivz.app.database;

import android.provider.BaseColumns;

public class TrackListContract {

    public TrackListContract() {
    }

    public static abstract class TrackList implements BaseColumns {
        public static final String TABLE_NAME = "tracklist";
        public static final String COLUMN_NAME_OWNER_ID = "owner_id";
        public static final String COLUMN_NAME_ARTIST = "artist";
        public static final String COLUMN_NAME_TITLE = "title";
        public static final String COLUMN_NAME_DURATION = "duration";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_URL = "url";
        public static final String COLUMN_NAME_LOCAL_URL = "local_url";
        public static final String COLUMN_NAME_LYRICS_ID = "lyrics_id";
        public static final String COLUMN_NAME_GENRE_ID = "genre_id";
    }

    public static final String CREATE_TRACKLIST =
            "CREATE TABLE " + TrackList.TABLE_NAME + " ("
                    + TrackList._ID + " INTEGER PRIMARY KEY, "
                    + TrackList.COLUMN_NAME_OWNER_ID + " INTEGER, "
                    + TrackList.COLUMN_NAME_ARTIST + " TEXT, "
                    + TrackList.COLUMN_NAME_TITLE + " TEXT, "
                    + TrackList.COLUMN_NAME_DURATION + " INTEGER, "
                    + TrackList.COLUMN_NAME_DATE + " INTEGER, "
                    + TrackList.COLUMN_NAME_URL + " TEXT, "
                    + TrackList.COLUMN_NAME_LOCAL_URL + " TEXT, "
                    + TrackList.COLUMN_NAME_LYRICS_ID + " INTEGER, "
                    + TrackList.COLUMN_NAME_GENRE_ID + " INTEGER "
                    + ")";

    public static final String DELETE_TRACKLIST =
            "DROP TABLE IF EXISTS " + TrackList.TABLE_NAME;
}
