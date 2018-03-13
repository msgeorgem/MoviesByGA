package com.example.android.moviesbyg;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-10-25.
 */

public class IMDBOnlineFragment extends Fragment implements LoaderManager.LoaderCallbacks<ArrayList<SingleMovie>> {

    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_OVERVIEW = "EXTRA_OVERVIEW";
    public static final String EXTRA_RELEASE_DATE = "EXTRA_RELEASE_DATE";
    public static final String EXTRA_VOTE = "EXTRA_VOTE";
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_POSTER = "EXTRA_POSTER";

    public static final String LOG_TAG = IMDBOnlineFragment.class.getName();
    private static final String URL =
            "https://api.themoviedb.org/3/discover/movie?";
    private static final String QUERY_BASE_URL = URL;
    /* API_KEY gained from themoviedb.org */
    private static final String api_key = BuildConfig.API_KEY;
    private static final String API_KEY = "api_key";

    private static final String SORT_BY = "sort_by";
    private static final String BUNDLE_RECYCLER_LAYOUT = "IMDBOnlineFragment.moviesRecyclerView";
    private static final int MOVIES_LOADER_ID = 1;

    public static Context context;
    private final ArrayList<SingleMovie> movieGrid = new ArrayList<>();
    SharedPreferences sharedPrefs;
    private Parcelable state;
    private View view;
    private RecyclerView moviesRecyclerView;
    /**
     * Adapter for the list of movies
     */
    private MoviesAdapter.OnItemClickListener mListener;
    private MoviesAdapter mAdapter = new MoviesAdapter(movieGrid, mListener);
    private TextView mEmptyStateTextView;
    private SwipeRefreshLayout mSwipeRefreshLayout;


    public IMDBOnlineFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_movies, container, false);
        context = getActivity();

        Log.i(LOG_TAG, "initLoader");
        // Get details on the currently active default data network
        loadingScheme();
        // Find a reference to the {@link ListView} in the layout
        moviesRecyclerView = view.findViewById(R.id.list_item);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(context, 2));
        } else {
            moviesRecyclerView.setLayoutManager(new GridLayoutManager(context, 4));
        }
        mListener = new MoviesAdapter.OnItemClickListener() {

            @Override
            public void onItemClick(SingleMovie item) {

                String currentMovieTitleString = item.getTitle();
                String currentMovieOverviewString = item.getOverview();
                String currentMovieReleaseDateString = item.getmReleaseDate();
                String currentMovieVotingString = item.getVoting();
                String currentMoviePosterString = item.getPoster();
                String currentMovieID = item.getMovieID();

                Intent intent1 = new Intent(getContext(), DetailActivity.class);

                intent1.putExtra(EXTRA_TITLE, currentMovieTitleString);
                intent1.putExtra(EXTRA_OVERVIEW, currentMovieOverviewString);
                intent1.putExtra(EXTRA_RELEASE_DATE, currentMovieReleaseDateString);
                intent1.putExtra(EXTRA_VOTE, currentMovieVotingString);
                intent1.putExtra(EXTRA_POSTER, currentMoviePosterString);
                intent1.putExtra(EXTRA_ID, currentMovieID);

                startActivity(intent1);
            }
        };
        mAdapter = new MoviesAdapter(movieGrid, mListener);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        moviesRecyclerView.setAdapter(mAdapter);
        moviesRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        moviesRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        moviesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }



    void loadingScheme() {
        NetworkInfo networkInfo = MoviesActivity.cm.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            // Get a reference to the LoaderManager, in order to interact with loaders.
            LoaderManager loaderManager = getLoaderManager();
            // Initialize the loader. Pass in the int ID constant defined above and pass in null for
            // the bundle. Pass in this activity for the LoaderCallbacks parameter (which is valid
            // because this activity implements the LoaderCallbacks interface).
            loaderManager.initLoader(MOVIES_LOADER_ID, null, this);
        } else {
            mEmptyStateTextView = view.findViewById(R.id.empty_view);
            mEmptyStateTextView.setText(R.string.no_internet);
            // Otherwise, display error
            // First, hide loading indicator so error message will be visible
            View loadingIndicator = view.findViewById(R.id.loading_indicator);
            loadingIndicator.setVisibility(View.GONE);
            // Set empty state text to display "No internet connection"
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // save RecyclerView state
        state = moviesRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (state != null) {
            moviesRecyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        // save RecyclerView state
        state = moviesRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public Loader<ArrayList<SingleMovie>> onCreateLoader(int i, Bundle bundle) {
        Log.i(LOG_TAG, "onCreateLoader");

        String urlOrder = sharedPrefs.getString(
                getString(R.string.imdb_settings_order_by_key),
                getString(R.string.imdb_settings_order_by_label)
        );

        Uri baseUri = Uri.parse(urlOrder);

        Uri.Builder uriBuilder = baseUri.buildUpon();

        uriBuilder.appendQueryParameter(API_KEY, api_key);
        Log.i(LOG_TAG, uriBuilder.toString());

        return new MoviesLoader(context, uriBuilder.toString());
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<SingleMovie>> loader, ArrayList<SingleMovie> movies) {
        // Hide loading indicator because the data has been loaded
        Log.i(LOG_TAG, "onLoadFinished");
        View loadingIndicator = view.findViewById(R.id.loading_indicator);
        loadingIndicator.setVisibility(View.GONE);

        moviesRecyclerView.setVisibility(View.VISIBLE);
        mAdapter = new MoviesAdapter(movieGrid, mListener);

        // If there is a valid list of {@link Movies}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (movies != null && !movies.isEmpty()) {
            mAdapter = new MoviesAdapter(movies, mListener);
            moviesRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<SingleMovie>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "onLoaderReset");
        mAdapter = new MoviesAdapter(movieGrid, mListener);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            moviesRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, moviesRecyclerView.getLayoutManager().onSaveInstanceState());

    }
}
