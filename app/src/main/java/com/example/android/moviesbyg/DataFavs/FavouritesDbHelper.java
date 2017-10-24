package com.example.android.moviesbyg.DataFavs;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry;

/**
 * Created by Marcin on 2017-10-24.
 */

public class FavouritesDbHelper extends SQLiteOpenHelper {


    public static final String DATABASE_NAME = "fav_movies.db";

    private static final int DATABASE_VERSION = 1;

    public FavouritesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVOURITES_TABLE =

                "CREATE TABLE " + FavouritesEntry.TABLE_NAME + " (" +
                        FavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        FavouritesEntry.COLUMN_MOVIE_ID + " INTEGER NOT NULL, " +
                        FavouritesEntry.COLUMN_TILE + " TEXT NOT NULL, " +
                        FavouritesEntry.COLUMN_RELEASE_DATE + " REAL NOT NULL, " +
                        FavouritesEntry.COLUMN_VOTE + " REAL NOT NULL, " +
                        FavouritesEntry.COLUMN_OVERVIEW + " REAL NOT NULL, " +
                        FavouritesEntry.COLUMN_POSTER + " TEXT NOT NULL);";

        db.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
