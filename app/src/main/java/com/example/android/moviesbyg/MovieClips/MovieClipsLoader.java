package com.example.android.moviesbyg.MovieClips;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-15.
 */

public class MovieClipsLoader extends AsyncTaskLoader<ArrayList<SingleMovieClip>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MovieClipsLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link com.example.android.moviesbyg.MoviesLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public MovieClipsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        Log.i(LOG_TAG, "onStartLoading");
        forceLoad();
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<SingleMovieClip> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.i(LOG_TAG, "loadInBackground");
        // Perform the network request, parse the response, and extract a list of news.
        ArrayList<SingleMovieClip> singleMovieClip = QueryClipsUtils.fetchMoviesData(mUrl);
        return singleMovieClip;
    }
}
