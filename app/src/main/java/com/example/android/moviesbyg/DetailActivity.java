package com.example.android.moviesbyg;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.android.moviesbyg.DataFavs.FavouritesContract;
import com.example.android.moviesbyg.Favourites.FavouritesFragment;
import com.example.android.moviesbyg.MovieClips.ClipsFragment;
import com.example.android.moviesbyg.MovieReviews.ReviewsFragment;
import com.example.android.moviesbyg.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_OVERVIEW;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_POSTER;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_RELEASE_DATE;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_TILE;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_VOTE;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.CONTENT_URI;

/**
 * Created by Marcin on 2017-09-10.
 */

public class DetailActivity extends AppCompatActivity implements ClipsFragment.OnFragmentInteractionListenerC, ReviewsFragment.OnFragmentInteractionListenerR {


    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    public static final String[] PROJECTION = {
            FavouritesContract.FavouritesEntry._ID,
            FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID,
    };
    private static final String BUNDLE_RECYCLER_LAYOUT = "DetailActivity.clipsRecyclerView.activity_detail";
    public static String MDB_CURRENT_MOVIE_ID;
    public static SharedPreferences favPrefs;
    private final String MDB_SHARE_HASHTAG = "IMDB Source";
    private String mMovieSummary;

    private Context context;
    private ClipsFragment mClipsFragment;
    private ReviewsFragment mReviewsFragment;
    private ToggleButton FAVtoggleButton;
    private String currentTitle, currentReleaseDate, currentVote, currentOverview, currentPoster;
    private long currentMovieId;
    private String movieId;
    private Uri mCurrentItemUri;
    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        // Find the toolbar view inside the activity layout
        Toolbar toolbar = findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);

        // Examine the intent that was used to launch this activity,
        // in order to figure out if we're creating a new item or editing an existing one.
        Intent intent = getIntent();
        mCurrentItemUri = intent.getData();
        favPrefs = getSharedPreferences("", Context.MODE_PRIVATE);


        if (mCurrentItemUri == null) {
            currentTitle = getIntent().getStringExtra(IMDBOnlineFragment.EXTRA_TITLE);
            mDetailBinding.part2.title.setText(currentTitle);

            currentReleaseDate = getIntent().getStringExtra(IMDBOnlineFragment.EXTRA_RELEASE_DATE);
            mDetailBinding.part2.releaseDate.setText(currentReleaseDate);

            currentVote = getIntent().getStringExtra(IMDBOnlineFragment.EXTRA_VOTE);
            mDetailBinding.part2.rating.setText(currentVote);

            currentOverview = getIntent().getStringExtra(IMDBOnlineFragment.EXTRA_OVERVIEW);
            mDetailBinding.part3.overview2.setText(currentOverview);

            currentPoster = getIntent().getStringExtra(IMDBOnlineFragment.EXTRA_POSTER);
            context = mDetailBinding.part1.poster.getContext();
//            Picasso.with(context).load(currentPoster).into(mDetailBinding.part1.poster);

            Glide.with(context)
                    .load(currentPoster)
                    .fitCenter()
                    .thumbnail(0.1f)
                    .crossFade()
                    .into(mDetailBinding.part1.poster);

            MDB_CURRENT_MOVIE_ID = getIntent().getStringExtra(IMDBOnlineFragment.EXTRA_ID);
            movieId = MDB_CURRENT_MOVIE_ID;
            currentMovieId = Long.parseLong(movieId);
            FAVtoggleButton = mDetailBinding.part2.favDetToggleButton;
            FAVtoggleButton.setChecked(false);

            Boolean a = checkIfInFavorites(movieId);

            if (a) {
                FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_yellow));
                FAVtoggleButton.setChecked(true);

            } else {
                FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_grey));
                FAVtoggleButton.setChecked(false);
            }

            FAVtoggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        try {
                            saveItem();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_yellow));
                        SharedPreferences.Editor editor = favPrefs.edit();
                        editor.putBoolean("On", true);
                        editor.apply();

                    } else {
                        FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_grey));
                        SharedPreferences.Editor editor = favPrefs.edit();
                        editor.putBoolean("On", false);
                        editor.apply();
                        delete(currentMovieId);
                    }
                }
            });
        } else {

            MDB_CURRENT_MOVIE_ID = intent.getStringExtra(FavouritesFragment.EXTRA_MOVIE_ID);
            movieId = MDB_CURRENT_MOVIE_ID;
            currentTitle = intent.getStringExtra(FavouritesFragment.EXTRA_TITLE);
            currentReleaseDate = intent.getStringExtra(FavouritesFragment.EXTRA_RELEASE_DATE);
            currentVote = intent.getStringExtra(FavouritesFragment.EXTRA_VOTE);
            currentOverview = intent.getStringExtra(FavouritesFragment.EXTRA_OVERVIEW);
            currentPoster = intent.getStringExtra(FavouritesFragment.EXTRA_POSTER);
            mDetailBinding.part2.title.setText(currentTitle);
            mDetailBinding.part2.releaseDate.setText(currentReleaseDate);
            mDetailBinding.part2.rating.setText(currentVote);
            mDetailBinding.part3.overview2.setText(currentOverview);
            context = mDetailBinding.part1.poster.getContext();
            Picasso.with(context).load(currentPoster).into(mDetailBinding.part1.poster);

            currentMovieId = Long.parseLong(movieId);

            context = mDetailBinding.part2.favDetToggleButton.getContext();
            FAVtoggleButton = mDetailBinding.part2.favDetToggleButton;
            FAVtoggleButton.setChecked(true);
            // wrong favPrefs = context.getSharedPreferences("favourites", Context.MODE_PRIVATE);
            // Boolean aa = DetailActivity.favPrefs.getBoolean("On"+context, false);
            Boolean a = checkIfInFavorites(movieId);
//            Boolean a = true;
            if (a) {
                FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_yellow));
                FAVtoggleButton.setChecked(true);
            } else {
                FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_grey));
                FAVtoggleButton.setChecked(false);
            }
            FAVtoggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                    if (isChecked) {
                        try {
                            saveItem();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_yellow));
                        SharedPreferences.Editor editor = favPrefs.edit();
                        editor.putBoolean("On", true);
                        editor.apply();

                    } else {
                        FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_grey));
                        SharedPreferences.Editor editor = favPrefs.edit();
                        editor.putBoolean("On", false);
                        editor.apply();
                        delete(currentMovieId);
                    }
                }
            });
        }

        mClipsFragment = new ClipsFragment();
        FragmentManager managerC = getFragmentManager();
        managerC.beginTransaction()
                .replace(mDetailBinding.part4.clipsContainer.getId(), mClipsFragment, mClipsFragment.getTag())
                .commit();

        mReviewsFragment = new ReviewsFragment();
        FragmentManager managerR = getFragmentManager();
        managerR.beginTransaction()
                .replace(mDetailBinding.part5.reviewsContainer.getId(), mReviewsFragment, mReviewsFragment.getTag())
                .commit();

        mMovieSummary = currentTitle + "" + currentReleaseDate + "" + currentOverview;
        Log.i(LOG_TAG, "initClipsLoader");
    }

    private boolean checkIfInFavorites(String movieID) {
        Cursor cur = getContentResolver().query(FavouritesContract.FavouritesEntry.CONTENT_URI, PROJECTION, null, null, null);

        ArrayList<String> favsTempList = new ArrayList<>();
        boolean favourite;
        if (cur != null) {
            while (cur.moveToNext()) {
                String i = cur.getString(cur.getColumnIndex(COLUMN_MOVIE_ID));
                favsTempList.add(i);
            }
        }
        favourite = favsTempList.contains(movieID);

        if (cur != null) {
            cur.close();
        }
        return favourite;
    }


    public void delete(long id) {
        int rowDeleted = getContentResolver().delete(FavouritesContract.FavouritesEntry.CONTENT_URI, FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID + "=" + id, null);
        Toast.makeText(this, rowDeleted + " " + getString(R.string.delete_one_item), Toast.LENGTH_SHORT).show();
    }

    // Get user input from editor and save item into database.
    private void saveItem() throws IOException {

        ContentValues values = new ContentValues();
        values.put(COLUMN_MOVIE_ID, movieId);
        values.put(COLUMN_TILE, currentTitle);
        values.put(COLUMN_RELEASE_DATE, currentReleaseDate);
        values.put(COLUMN_VOTE, currentVote);
        values.put(COLUMN_OVERVIEW, currentOverview);
        values.put(COLUMN_POSTER, currentPoster);

        // This is a NEW item, so insert a new item into the provider,
        // returning the content URI for the item item.
        Uri newUri = getContentResolver().insert(CONTENT_URI, values);

        // Show a toast message depending on whether or not the insertion was successful.
        if (newUri == null) {
            // If the new content URI is null, then there was an error with insertion.
            Toast.makeText(this, getString(R.string.editor_insert_item_failed), Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the insertion was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_insert_item_success), Toast.LENGTH_SHORT).show();
        }
        currentMovieId = Long.parseLong(movieId);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            ClipsFragment.clipsRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, ClipsFragment.clipsRecyclerView.getLayoutManager().onSaveInstanceState());
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        return shareIntent;
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onFragmentInteraction1(Uri uri) {
    }

    @Override
    public void onFragmentInteractionR(Uri uri) {
    }


}