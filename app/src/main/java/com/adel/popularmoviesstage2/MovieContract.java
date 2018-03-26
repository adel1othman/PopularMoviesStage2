package com.adel.popularmoviesstage2;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

public final class MovieContract {

    private MovieContract() {}

    public static final String CONTENT_AUTHORITY = "com.adel.popularmoviesstage2";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_MOVIES = "movies";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_MOVIES);

        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIES;

        public final static String TABLE_NAME = "movies";
        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_MOVIE_ID ="movieID";
        public final static String COLUMN_MOVIE_TITLE ="title";
        public final static String COLUMN_MOVIE_Release_Date = "release";
        public final static String COLUMN_MOVIE_Movie_Poster = "poster";
        public final static String COLUMN_MOVIE_Vote_Average = "votes";
        public final static String COLUMN_MOVIE_Overview = "overview";

        public static boolean isValidInfo(String info) {
            if (!TextUtils.isEmpty(info)) {
                return true;
            }
            return false;
        }

        public static Uri currentMovieUri(int id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static boolean isImageResourceProvided(String imgResource) {
            if (!TextUtils.isEmpty(imgResource)) {
                return true;
            }
            return false;
        }
    }
}