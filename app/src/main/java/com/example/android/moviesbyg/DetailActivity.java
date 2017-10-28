package com.example.android.moviesbyg;

import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.NavUtils;
import android.support.v4.app.ShareCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.moviesbyg.DataFavs.FavouritesContract;
import com.example.android.moviesbyg.MovieClips.ClipsFragment;
import com.example.android.moviesbyg.MovieReviews.MovieReviewsAdapter;
import com.example.android.moviesbyg.MovieReviews.ReviewsFragment;
import com.example.android.moviesbyg.MovieReviews.SingleMovieReview;
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


    public static final String MDB_MOVIE_PATH1 = "https://api.themoviedb.org/3/movie/";
    public static final String TEST_MDB_MOVIE_PATH = "https://api.themoviedb.org/3/movie/321612/videos?api_key=1157007d8e3f7d5e0af6d7e4165e2730";
    public static final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final String BUNDLE_RECYCLER_LAYOUT = "DetailActivity.clipsRecyclerView.activity_detail";
    private static final int CLIPS_LOADER_ID = 222;
    private static final int REVIEWS_LOADER_ID = 333;
    private static final String api_key = "1157007d8e3f7d5e0af6d7e4165e2730";
    public static final String MDB_MOVIE_PATH2 = "/videos?api_key=" + api_key;
    public static final String MDB_REVIEWS_PATH2 = "/reviews?api_key=" + api_key;
    public static String MDB_CURRENT_MOVIE_ID;
    public static final String MDB_MOVIE_ID = MDB_CURRENT_MOVIE_ID;
    public static String QUERY_BASE_URL_C;
    public static String QUERY_BASE_URL_R;
    public static ConnectivityManager cm;
    public static SharedPreferences favPrefs;
    private final String MDB_SHARE_HASHTAG = "IMDB Source";
    Parcelable state;
    SQLiteDatabase mDb;
    private String mMovieSummary;
    private MovieReviewsAdapter mAdapterR;
    private RecyclerView reviewsRecyclerView;
    private ArrayList<SingleMovieReview> reviewsList = new ArrayList<>();
    private Context context;
    private ClipsFragment mClipsFragment;
    private ReviewsFragment mReviewsFragment;
    private ToggleButton FAVtoggleButton;
    private boolean mMovieFav = false;
    private String title, releaseDate, vote, overview, poster;
    private int movieId;
    /**
     * TextView that is displayed when the list is empty
     */

    private TextView mEmptyStateTextView2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityDetailBinding mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        title = getIntent().getStringExtra(MoviesAdapter.EXTRA_TITLE);
        mDetailBinding.part2.title.setText(title);

        releaseDate = getIntent().getStringExtra(MoviesAdapter.EXTRA_RELEASE_DATE);
        mDetailBinding.part2.releaseDate.setText(releaseDate);

        vote = getIntent().getStringExtra(MoviesAdapter.EXTRA_VOTE);
        mDetailBinding.part2.rating.setText(vote);

        overview = getIntent().getStringExtra(MoviesAdapter.EXTRA_OVERVIEW);
        mDetailBinding.part3.overview2.setText(overview);

        poster = getIntent().getStringExtra(MoviesAdapter.EXTRA_POSTER);
        context = mDetailBinding.part1.poster.getContext();
        Picasso.with(context).load(poster).into(mDetailBinding.part1.poster);

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


        //TODO: fix shared prefs

        FAVtoggleButton = mDetailBinding.part2.favDetToggleButton;
        favPrefs = getSharedPreferences("favourites", MODE_PRIVATE);
        FAVtoggleButton.setChecked(favPrefs.getBoolean("Off", false));
//        favPrefs = getPreferences(MODE_PRIVATE);
        FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_grey));
        FAVtoggleButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {

                if (isChecked) {
                    FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_yellow));
                    SharedPreferences.Editor editor = favPrefs.edit();
                    editor.putBoolean("On", true);
                    editor.apply();
                    try {
                        saveItem();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    FAVtoggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_grey));
                    SharedPreferences.Editor editor = favPrefs.edit();
                    editor.putBoolean("Off", false);
                    editor.apply();
                }
            }
        });

        MDB_CURRENT_MOVIE_ID = getIntent().getStringExtra(MoviesAdapter.EXTRA_ID);
        QUERY_BASE_URL_C = MDB_MOVIE_PATH1 + MDB_CURRENT_MOVIE_ID + MDB_MOVIE_PATH2;
        QUERY_BASE_URL_R = MDB_MOVIE_PATH1 + MDB_CURRENT_MOVIE_ID + MDB_REVIEWS_PATH2;
        mMovieSummary = title + "" + releaseDate + "" + overview;
        Log.i(LOG_TAG, "initClipsLoader");

    }

    // Get user input from editor and save item into database.
    private void saveItem() throws IOException {

        //TODO: add condition if added movie exists in favorites table, if exists change BOOLEAN to true
        // Read from input getintent
        //       String movieIdInt = getString(movieId);

        if (mMovieFav) {
            Toast.makeText(this, R.string.editor_update_item_failed, Toast.LENGTH_SHORT).show();
            // Since no fields were modified, we can return early without creating a new item.
            // No need to create ContentValues and no need to do any ContentProvider operations.

            return;

        } else {
            // if (TextUtils.isEmpty(movieIdInt)) {
            //     Toast.makeText(this, getString(R.string.movieId_required), Toast.LENGTH_SHORT).show();
            //    return;
            //}
            ContentValues values = new ContentValues();
            values.put(COLUMN_MOVIE_ID, movieId);
            values.put(COLUMN_TILE, title);
            values.put(COLUMN_RELEASE_DATE, releaseDate);
            values.put(COLUMN_VOTE, vote);
            values.put(COLUMN_OVERVIEW, overview);
            values.put(COLUMN_POSTER, poster);

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
//                finish();
            }
        }
    }

    private void deleteOneItem(long id) {
        int rowDeleted = getContentResolver().delete(FavouritesContract.FavouritesEntry.CONTENT_URI, FavouritesContract.FavouritesEntry._ID + "=" + id, null);
        Toast.makeText(this, rowDeleted + " " + getString(R.string.delete_one_item), Toast.LENGTH_SHORT).show();
    }

    private void showDeleteConfirmationDialogOneItem(final RecyclerView.ViewHolder viewHolder) {
        //Inside, get the viewHolder's itemView's tag and store in a long variable id
        //get the iD of the item being swiped
        final long iD = (long) viewHolder.itemView.getTag();

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_oneitem_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Delete" button, so delete the item.
                //remove from DB
                deleteOneItem(iD);
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                // User clicked the "Cancel" button, so dismiss the dialog
                if (dialog != null) {
                    dialog.dismiss();
                }
                //call swapCursor on mAdapter passing in null as the argument
                //update the list
//                mCursorAdapter.swapCursor(querY());
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
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