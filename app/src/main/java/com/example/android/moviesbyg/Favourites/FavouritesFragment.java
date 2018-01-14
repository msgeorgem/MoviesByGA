package com.example.android.moviesbyg.Favourites;

import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.moviesbyg.DataFavs.FavouritesContract;
import com.example.android.moviesbyg.DetailActivity;
import com.example.android.moviesbyg.DividerItemDecoration;
import com.example.android.moviesbyg.R;

import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_MOVIE_ID;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_OVERVIEW;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_POSTER;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_RELEASE_DATE;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_TILE;
import static com.example.android.moviesbyg.DataFavs.FavouritesContract.FavouritesEntry.COLUMN_VOTE;

/**
 * Created by Marcin on 2017-10-25.
 */

public class FavouritesFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = FavouritesFragment.class.getName();
    public static final String EXTRA_MOVIE_ID = "EXTRA_MOVIE_ID";
    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_RELEASE_DATE = "EXTRA_RELEASE_DATE";
    public static final String EXTRA_VOTE = "EXTRA_VOTE";
    public static final String EXTRA_OVERVIEW = "EXTRA_OVERVIEW";
    public static final String EXTRA_POSTER = "EXTRA_POSTER";

    private static final int FAV_LOADER = 0;
    private static final String[] PROJECTION1 = {
            FavouritesContract.FavouritesEntry._ID,
            COLUMN_MOVIE_ID,
            FavouritesContract.FavouritesEntry.COLUMN_TILE,
            FavouritesContract.FavouritesEntry.COLUMN_RELEASE_DATE,
            FavouritesContract.FavouritesEntry.COLUMN_VOTE,
            FavouritesContract.FavouritesEntry.COLUMN_OVERVIEW,
            FavouritesContract.FavouritesEntry.COLUMN_POSTER
    };
    private static final String[] PROJECTION = {
            FavouritesContract.FavouritesEntry._ID,
            COLUMN_MOVIE_ID,
    };

    //   Just a rough idea how to sort in query
    private static final String SORT_ORDER_ID = FavouritesContract.FavouritesEntry._ID + " DESC";
    private static final String BUNDLE_RECYCLER_LAYOUT = "FavouritesFragment.moviesRecyclerView";
    public FavouritesCursorAdapter mFavsAdapter;
    Parcelable state;
    private View mloadingIndicator;
    private TextView mEmptyStateTextView;
    private View view;
    //    private static final String SELECTION = FavouritesContract.FavouritesEntry.getGreaterThanZero();
    private RecyclerView favouritesRecyclerView;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favourites, container, false);
        Log.i(LOG_TAG, "initLoader");

        // Find a reference to the {@link ListView} in the layout
        favouritesRecyclerView = view.findViewById(R.id.list_favourites);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            favouritesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 1));
        } else {
            favouritesRecyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2));
        }

        mFavsAdapter = new FavouritesCursorAdapter(this, null);

        // Set the adapter on the {@link ListView}
        // so the list can be populated in the user interface
        favouritesRecyclerView.setAdapter(mFavsAdapter);
        favouritesRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));
        favouritesRecyclerView.addItemDecoration(new android.support.v7.widget.DividerItemDecoration(getActivity(), DividerItemDecoration.HORIZONTAL_LIST));
        favouritesRecyclerView.setItemAnimator(new DefaultItemAnimator());

        mEmptyStateTextView = view.findViewById(R.id.empty_view_favs);
        mEmptyStateTextView.setText(R.string.no_favourites);

        mloadingIndicator = view.findViewById(R.id.loading_indicator_favs);
        //kick off the loader
        getLoaderManager().initLoader(FAV_LOADER, null, this);
        return view;
    }

    public Cursor querY() {
        return getActivity().getContentResolver().query(FavouritesContract.FavouritesEntry.CONTENT_URI, null, null, null, SORT_ORDER_ID);
    }

    void deleteOneItem(long id) {
        int rowDeleted = getActivity().getContentResolver().delete(FavouritesContract.FavouritesEntry.CONTENT_URI, FavouritesContract.FavouritesEntry._ID + "=" + id, null);
        Toast.makeText(getActivity(), rowDeleted + " " + getString(R.string.delete_one_item), Toast.LENGTH_SHORT).show();

    }

    public void showDeleteConfirmationDialogOneItem(final RecyclerView.ViewHolder viewHolder) {
        //Inside, get the viewHolder's itemView's tag and store in a long variable id
        //get the iD of the item being swiped
        final long iD = (long) viewHolder.itemView.getTag();

        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
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
                mFavsAdapter.swapCursor(querY());
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        alertDialog.setCanceledOnTouchOutside(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        // save RecyclerView state
        state = favouritesRecyclerView.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore RecyclerView state
        if (state != null) {
            favouritesRecyclerView.getLayoutManager().onRestoreInstanceState(state);
        }
    }
    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

        String COLUMN = sharedPrefs.getString(
                getString(R.string.fav_settings_order_by_key),
                getString(R.string.fav_settings_order_by_recent_value)

        );

        String SORT_ORDER = COLUMN + " DESC";

        // Perform a query using CursorLoader
        return new CursorLoader(getActivity(),    // Parent activity context
                FavouritesContract.FavouritesEntry.CONTENT_URI, // Provider content URI to query
                PROJECTION1,            // The columns to include in the resulting Cursor
                null,         // The values for the WHERE clause
                null,      // No SELECTION arguments
                SORT_ORDER);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback called when the data needs to be deleted
        mFavsAdapter.swapCursor(null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link ItemCursor Adapter with this new cursor containing updated item data
        if (!data.moveToFirst()) {
            mEmptyStateTextView.setVisibility(View.VISIBLE);
            mloadingIndicator.setVisibility(View.GONE);
        } else {
            mEmptyStateTextView.setVisibility(View.GONE);
            mloadingIndicator.setVisibility(View.GONE);
        }
        mFavsAdapter.swapCursor(data);
    }

    @Override
    public void onViewStateRestored(Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            Parcelable savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            favouritesRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, favouritesRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    public void onItemClick(long id) {
        Intent intent = new Intent(getActivity(), DetailActivity.class);

        String specificID = String.valueOf(id);
        String mSelectionClause = FavouritesContract.FavouritesEntry._ID;
        try {
            Cursor cursor = getActivity().getContentResolver().query(FavouritesContract.FavouritesEntry.CONTENT_URI, PROJECTION1, mSelectionClause + " = '" + specificID + "'", null, null);
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    int movieColumnIndex = cursor.getColumnIndex(COLUMN_MOVIE_ID);
                    int titleColumnIndex = cursor.getColumnIndex(COLUMN_TILE);
                    int releaseColumnIndex = cursor.getColumnIndex(COLUMN_RELEASE_DATE);
                    int voteColumnIndex = cursor.getColumnIndex(COLUMN_VOTE);
                    int overviewColumnIndex = cursor.getColumnIndex(COLUMN_OVERVIEW);
                    int posterColumnIndex = cursor.getColumnIndex(COLUMN_POSTER);

                    String movieID = cursor.getString(movieColumnIndex);
                    String movieTitle = cursor.getString(titleColumnIndex);
                    String movieRelease = cursor.getString(releaseColumnIndex);
                    String movieVote = cursor.getString(voteColumnIndex);
                    String movieOverview = cursor.getString(overviewColumnIndex);
                    String moviePoster = cursor.getString(posterColumnIndex);

                    intent.putExtra(EXTRA_MOVIE_ID, movieID);
                    intent.putExtra(EXTRA_TITLE, movieTitle);
                    intent.putExtra(EXTRA_RELEASE_DATE, movieRelease);
                    intent.putExtra(EXTRA_VOTE, movieVote);
                    intent.putExtra(EXTRA_OVERVIEW, movieOverview);
                    intent.putExtra(EXTRA_POSTER, moviePoster);

                } while (cursor.moveToNext());
            }
            if (cursor != null) {
                cursor.close();
            }

        } catch (Exception e) {
            Log.e("Path Error", e.toString());
        }

        Uri currentProductUri = ContentUris.withAppendedId(FavouritesContract.FavouritesEntry.CONTENT_URI, id);
        intent.setData(currentProductUri);
        startActivity(intent);
    }
}
