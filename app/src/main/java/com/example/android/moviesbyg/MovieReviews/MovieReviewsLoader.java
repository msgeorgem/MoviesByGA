package com.example.android.moviesbyg.MovieReviews;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-15.
 */

public class MovieReviewsLoader extends AsyncTaskLoader<ArrayList<SingleMovieReview>> {

    /**
     * Tag for log messages
     */
    private static final String LOG_TAG = MovieReviewsLoader.class.getName();

    /**
     * Query URL
     */
    private String mUrl;

    /**
     * Constructs a new {@link com.example.android.moviesbyg.MovieReviewsLoader}.
     *
     * @param context of the activity
     * @param url     to load data from
     */
    public MovieReviewsLoader(Context context, String url) {
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
    public ArrayList<SingleMovieReview> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.i(LOG_TAG, "loadInBackground");
        // Perform the network request, parse the response, and extract a list of news.
        ArrayList<SingleMovieReview> singleMovieReview = QueryReviewsUtils.fetchReviesData(mUrl);
        return singleMovieReview;
    }
}
