package com.example.android.moviesbyg;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.moviesbyg.MovieClips.MovieClipsAdapter;
import com.example.android.moviesbyg.MovieClips.MovieClipsLoader;
import com.example.android.moviesbyg.MovieClips.SingleMovieClip;
import com.example.android.moviesbyg.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-10.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<ArrayList<SingleMovieClip>> {


    public static final String MDB_MOVIE_PATH1 = "https://api.themoviedb.org/3/movie/";
    public static final String TEST_MDB_MOVIE_PATH = "https://api.themoviedb.org/3/movie/321612/videos?api_key=1157007d8e3f7d5e0af6d7e4165e2730";
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String BUNDLE_RECYCLER_LAYOUT = "DetailActivity.clipsRecyclerView.activity_detail";
    private static final int CLIPS_LOADER_ID = 222;
    private static final String api_key = "1157007d8e3f7d5e0af6d7e4165e2730";
    public static final String MDB_MOVIE_PATH2 = "/videos?api_key=" + api_key;
    public static final String MDB_REVIEWS_PATH2 = "/reviews?api_key=" + api_key;
    public static String MDB_CURRENT_MOVIE_ID;
    public static final String MDB_MOVIE_ID = MDB_CURRENT_MOVIE_ID;
    private final String MDB_SHARE_HASHTAG = "IMDB Source";
    public String QUERY_BASE_URL_C;
    Parcelable state;
    private String mMovieSummary;
    private MovieClipsAdapter mAdapterC;
    private RecyclerView clipsRecyclerView;
    private ArrayList<SingleMovieClip> clipsList = new ArrayList<>();
    private Context context;
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String title = getIntent().getStringExtra(MoviesAdapter.EXTRA_TITLE);
        mDetailBinding.part2.title.setText(title);

        String releaseDate = getIntent().getStringExtra(MoviesAdapter.EXTRA_RELEASE_DATE);
        mDetailBinding.part2.releaseDate.setText(releaseDate);

        String vote = getIntent().getStringExtra(MoviesAdapter.EXTRA_VOTE);
        mDetailBinding.part2.rating.setText(vote);

        String overview = getIntent().getStringExtra(MoviesAdapter.EXTRA_OVERVIEW);
        mDetailBinding.part3.overview.setText(overview);

        String poster = getIntent().getStringExtra(MoviesAdapter.EXTRA_POSTER);
        context = mDetailBinding.part1.poster.getContext();
        Picasso.with(context).load(poster).into(mDetailBinding.part1.poster);

        MDB_CURRENT_MOVIE_ID = getIntent().getStringExtra(MoviesAdapter.EXTRA_ID);
        QUERY_BASE_URL_C = MDB_MOVIE_PATH1 + MDB_CURRENT_MOVIE_ID + MDB_MOVIE_PATH2;

        mMovieSummary = title + "" + releaseDate + "" + overview;
        Log.i(LOG_TAG, "initClipsLoader");

        // Find a reference to the {@link ListView} in the layout
        clipsRecyclerView = mDetailBinding.part4.listVideos;

        clipsRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapterC = new MovieClipsAdapter(clipsList);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        clipsRecyclerView.setAdapter(mAdapterC);
        clipsRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL_LIST));
        clipsRecyclerView.setItemAnimator(new DefaultItemAnimator());


        mEmptyStateTextView = (TextView) findViewById(R.id.empty_view1);

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
            loaderManager.initLoader(CLIPS_LOADER_ID, null, DetailActivity.this);

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
    public Loader<ArrayList<SingleMovieClip>> onCreateLoader(int i, Bundle bundle) {

        Log.i(LOG_TAG, "onCreateLoader");

        Uri baseUri = Uri.parse(QUERY_BASE_URL_C);
        // Uri.Builder uriBuilder = baseUri.buildUpon();

//        uriBuilder.appendQueryParameter(API_KEY, api_key);
//        uriBuilder.appendQueryParameter(SORT_BY, order);

        return new MovieClipsLoader(this, baseUri.toString());
    }


    @Override
    public void onLoadFinished(Loader<ArrayList<SingleMovieClip>> loader, ArrayList<SingleMovieClip> clips) {
        // Hide loading indicator because the data has been loaded
        Log.i(LOG_TAG, "onLoadFinished");
        View loadingIndicator = findViewById(R.id.loading_indicator1);
        loadingIndicator.setVisibility(View.GONE);

        clipsRecyclerView.setVisibility(View.VISIBLE);
        mAdapterC = new MovieClipsAdapter(clipsList);


        // If there is a valid list of {@link Movies}s, then add them to the adapter's
        // data set. This will trigger the ListView to update.
        if (clips != null && !clips.isEmpty()) {
            mAdapterC = new MovieClipsAdapter(clips);
            clipsRecyclerView.setAdapter(mAdapterC);
        }
    }

    @Override
    public void onLoaderReset(Loader<ArrayList<SingleMovieClip>> loader) {
        // Loader reset, so we can clear out our existing data.
        Log.i(LOG_TAG, "onLoaderReset");
        mAdapterC = new MovieClipsAdapter(clipsList);
    }

    //
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.d(LOG_TAG, "saving listview state @ onPause");
//        // save RecyclerView state @ onPause
////        mBundleRecyclerViewState = new Bundle();
//        state = newsRecyclerView.getLayoutManager().onSaveInstanceState();
////        mBundleRecyclerViewState.putParcelable(KEY_RECYCLER_STATE, state);
//
//
//    }
//
//    @Override
//    public void onResume() {  // After a pause OR at startup
//        super.onResume();
//        //Refresh your stuff here
//        if (state != null) {
//            Log.d(LOG_TAG, "state is not null @ onResume");
//            newsRecyclerView.requestFocus();
////            state = mBundleRecyclerViewState.getParcelable(KEY_RECYCLER_STATE);
//            newsRecyclerView.getLayoutManager().onRestoreInstanceState(state);;
//            }
//    }
    @Override
    protected void onPause() {
        super.onPause();

        // save RecyclerView state
        state = clipsRecyclerView.getLayoutManager().onSaveInstanceState();

    }

    @Override
    protected void onResume() {
        super.onResume();

        // restore RecyclerView state
        if (state != null) {
            clipsRecyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int id = item.getItemId();
//        if (id == R.id.action_settings_m) {
//            Intent settingsIntent = new Intent(this, SettingsActivity.class);
//            startActivity(settingsIntent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            clipsRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, clipsRecyclerView.getLayoutManager().onSaveInstanceState());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        /* Use AppCompatActivity's method getMenuInflater to get a handle on the menu inflater */
        MenuInflater inflater = getMenuInflater();
        /* Use the inflater's inflate method to inflate our menu layout to this menu */
        inflater.inflate(R.menu.detail, menu);
        /* Return true so that the menu is displayed in the Toolbar */
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();


        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(DetailActivity.this);
            return true;
        }
        /* Share menu item clicked */
        if (id == R.id.action_share_d) {
            Intent shareIntent = createShareMovieIntent();
            startActivity(shareIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = ShareCompat.IntentBuilder.from(this)
                .setType("text/plain")
                .setText(mMovieSummary + MDB_SHARE_HASHTAG)
                .getIntent();
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        return shareIntent;
    }

    @Override
    public void onStop() {
        super.onStop();
    }
}