package com.example.android.moviesbyg;

import android.content.Context;
import android.os.FileObserver;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-12.
 */

public class MoviesLoader extends AsyncTaskLoader<ArrayList<SingleMovie>> {

    /** Tag for log messages */
    private static final String LOG_TAG = MoviesLoader.class.getName();
    private ArrayList<SingleMovie> mData;
    private FileObserver mFileObserver;
    /** Query URL */
    private String mUrl;

    /**
     * Constructs a new {@link MoviesLoader}.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public MoviesLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {

        Log.i(LOG_TAG,"onStartLoading");
        if (mData != null) {
            Log.i(LOG_TAG, "Use cached data");
            // Use cached data
            deliverResult(mData);
        } else {
            forceLoad();
        }
//        if (mFileObserver == null) {
//            Log.i(LOG_TAG, "mFileObserver == null");
//            String path = new File(
//                    IMDBOnlineFragment.context.getFilesDir(), "moviesIMDB.json").getPath();
//            Log.i(LOG_TAG, path);
//            mFileObserver = new FileObserver(path) {
//                @Override
//                public void onEvent(int event, String path) {
//                    Log.i(LOG_TAG, "Notify the loader to reload the data");
//                    // Notify the loader to reload the data
//                     onContentChanged();
//
//                    // If the loader is started, this will kick off
//                    // loadInBackground() immediately. Otherwise,
//                    // the fact that something changed will be cached
//                    // and can be later retrieved via takeContentChanged()
//                }
//            };
//        }
//        mFileObserver.startWatching();
//        if (takeContentChanged() || mData == null) {
//            Log.i(LOG_TAG, "Something has changed or no data ");
//            // Something has changed or we have no data,
//            // so kick off loading it
//            forceLoad();
//        }
    }

    /**
     * This is on a background thread.
     */
    @Override
    public ArrayList<SingleMovie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }
        Log.i(LOG_TAG,"loadInBackground");
        // Perform the network request, parse the response, and extract a list of news.
        ArrayList<SingleMovie> singleMovie = QueryUtils.fetchMoviesData(mUrl);
//        isLoadInBackgroundCanceled();
        return singleMovie;
    }

    @Override
    public void deliverResult(ArrayList<SingleMovie> data) {
        if (isReset()) {
//            onReleaseResources(data);
            return;
        }
        // We’ll save the data for later retrieval
//        ArrayList<SingleMovie> oldData = mData;
        mData = data;
        // We can do any pre-processing we want here
        // Just remember this is on the UI thread so nothing lengthy!
        // Only deliver result if the loader is started

        if (isStarted()) {
            super.deliverResult(data);
        }

//        if (oldData != null && oldData != data)
//            onReleaseResources(oldData);
    }

    @Override
    protected void onStopLoading() {
        cancelLoad();
    }

    @Override
    protected void onReset() {
        onStopLoading();

//        if (mData != null) {
//            onReleaseResources(mData);
//            mData = null;
//        }
    }

//    @Override
//    public void onCanceled(ArrayList<SingleMovie> data) {
//        super.onCanceled(data);
//
//        onReleaseResources(data);
//    }

//    protected void onReleaseResources(ArrayList<SingleMovie> data) {
//        // Stop watching for file changes
//        if (mFileObserver != null) {
//            mFileObserver.stopWatching();
//            mFileObserver = null;
//        }
//    }
}
