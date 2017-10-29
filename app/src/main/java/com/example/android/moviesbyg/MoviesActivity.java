package com.example.android.moviesbyg;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.ArrayList;

public class MoviesActivity extends AppCompatActivity {

    public static final String LOG_TAG = MoviesActivity.class.getName();
    public static final String EXTRA_POSTER = "EXTRA_POSTER";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_DESCRIPTION = "EXTRA_DESCRIPTION";
    private static final String BUNDLE_RECYCLER_LAYOUT = "MoviesActivity.moviesRecyclerView.activity_movies";
    /**
     * Constant value for the news loader ID. We can choose any integer.
     * This really only comes into play if you're using multiple loaders.
     */
    private static final int MOVIES_LOADER_ID = 1;
    private static final String URL =
            "https://api.themoviedb.org/3/discover/movie?";
    private static final String QUERY_BASE_URL = URL;
    /* API_KEY gained from themoviedb.org */
    private static final String API_KEY = "api_key";
    private static final String api_key = "1157007d8e3f7d5e0af6d7e4165e2730";
    private static final String SORT_BY = "sort_by";
    private static final String BY_VOTE = "vote_average.desc";
    private static final String BY_POPULARITY = "popularity.desc";
    public static ConnectivityManager cm;
    public static String MDB_CURRENT_MOVIE_ID;

    /**
     * Adapter for the list of movies
     */
    public MoviesAdapter mAdapter;
    Parcelable state;
    private GridLayoutManager mLayoutManager;
    private RecyclerView moviesRecyclerView;
    private ArrayList<SingleMovie> movieGrid = new ArrayList<>();
    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movies);

        // Find the view pager that will allow the user to swipe between fragments
        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);

        // Create an adapter that knows which fragment should be shown on each page
        CategoryAdapter adapter = new CategoryAdapter(this, getSupportFragmentManager());

        // Set the adapter onto the view pager
        viewPager.setAdapter(adapter);

        // Find the tab layout that shows the tabs
        final TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);

        // Connect the tab layout with the view pager. This will
        //   1. Update the tab layout when the view pager is swiped
        //   2. Update the view pager when a tab is selected
        //   3. Set the tab layout's tab names with the view pager's adapter's titles
        //      by calling onPageTitle()

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });


        cm = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings_m) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
