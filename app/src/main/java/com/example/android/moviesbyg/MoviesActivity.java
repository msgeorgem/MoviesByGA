package com.example.android.moviesbyg;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.example.android.moviesbyg.MoviesAdapter.EXTRA_OVERVIEW;

public class MoviesActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<SingleMovie>> {

    public static final String LOG_TAG = MoviesActivity.class.getName();
    private static final String BUNDLE_RECYCLER_LAYOUT = "MoviesActivity.newsRecyclerView.activity_movies";

    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIES_LOADER_ID = 1;
    private static final String URL =
            "https://api.themoviedb.org/3/discover/movie?";

    private static final String QUERY_BASE_URL = URL;
    Parcelable state;



    /* API_KEY gained from themoviedb.org */
    private static final String API_KEY = "api_key";
    private static final String api_key = "1157007d8e3f7d5e0af6d7e4165e2730";
    private static final String SORT_BY = "sort_by";
    private static final String BY_VOTE = "vote_average.desc";
    private static final String BY_POPULARITY = "popularity.desc";

    private static final int VERTICAL_ITEM_SPACE = 0;

    public static final String EXTRA_POSTER = "EXTRA_POSTER";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    /**
     * Adapter for the list of movies
     */
    public MoviesAdapter mAdapter;
    private RecyclerView newsRecyclerView;
    private ArrayList<SingleMovie> movieGrid = new ArrayList<>();
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        Log.i(LOG_TAG, "initLoader");

        // Find a reference to the {@link ListView} in the layout
        newsRecyclerView = (RecyclerView) findViewById(R.id.list_item);

        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            newsRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        }
        else{
            newsRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mAdapter = new MoviesAdapter(movieGrid);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        newsRecyclerView.setAdapter(mAdapter);
        newsRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        newsRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        newsRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view);

        // Get a reference to the ConnectivityManager to check state of network connectivity
        ConnectivityManager cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        // Get details on the currently active default data network
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {

            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIES_LOADER_ID, null, MoviesActivity.this);
        } else {
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Set empty state text to display "No internet connection"
            mEmptyStateTextView.setText(R.string.no_internet);
        }


    }

    @Override
    public Loader<ArrayList<SingleMovie>> onCreateLoader(int i, Bundle bundle) {

        Log.i(LOG_TAG, "onCreateLoader");
        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this);
//        String query = sharedPrefs.getString(
//                getString(R.string.settings_query_key),
//                getString(R.string.settings_query_default)
//        );

        String order = sharedPrefs.getString(
                getString(R.string.pref_order_key),
                getString(R.string.pref_order_default)
        );

        Uri baseUri = Uri.parse(QUERY_BASE_URL);
        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(API_KEY, api_key);
        uriBuilder.appendQueryParameter(SORT_BY, order);

        return new MoviesLoader(this, uriBuilder.toString());
    }

    @Override
    public void onLoadFinished(Loader<ArrayList<SingleMovie>> loader, ArrayList<SingleMovie> news) {
        // Hide loading indicator because the data has been loaded
        Log.i(LOG_TAG, "onLoadFinished");
        View loadingIndicator = findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        newsRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new MoviesAdapter(movieGrid);

        // If there is a valid list of {@link Movies}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (news != null && !news.isEmpty()) {
            mAdapter = new MoviesAdapter(news);
            newsRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<SingleMovie>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "onLoaderReset");
        mAdapter = new MoviesAdapter(movieGrid);
    }

    @Override
    public void onResume() {  // After a pause OR at startup
        super.onResume();
        //Refresh your stuff here
    }

    @Override
    public void onPause() {
        // Save ListView state @ onPause
        Log.d(LOG_TAG, "saving listview state @ onPause");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }



    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if(savedInstanceState != null)
        {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            newsRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, newsRecyclerView.getLayoutManager().onSaveInstanceState());
    }

}
