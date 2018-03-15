package com.example.android.moviesbyg.Favourites;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.bumptech.glide.Glide;
import com.example.android.moviesbyg.DataFavs.FavouritesContract;
import com.example.android.moviesbyg.DetailActivity;
import com.example.android.moviesbyg.R;

/**
 * Created by Marcin on 2017-10-28.
 */

public class FavouritesCursorAdapter extends CursorRecyclerAdapter<FavouritesCursorAdapter.ViewHolder> {

    private FavouritesFragment fragment = new FavouritesFragment();

    public FavouritesCursorAdapter(FavouritesFragment context, Cursor c) {
        super(context, c);
        this.fragment = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_favourites, parent, false);
        ViewHolder vh = new ViewHolder(itemView);
        return vh;


    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, Cursor cursor) {

        final long id;

        // Find the columns of item attributes that we're interested in
        id = cursor.getLong(cursor.getColumnIndex(FavouritesContract.FavouritesEntry._ID));
        int titleColumnIndex = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_TILE);
        int overviewColumnIndex = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_OVERVIEW);
        int posterColumnIndex = cursor.getColumnIndex(FavouritesContract.FavouritesEntry.COLUMN_POSTER);


        // Read the item attributes from the Cursor for the current item
        final String itemTitle = cursor.getString(titleColumnIndex);
        String itemOverview = cursor.getString(overviewColumnIndex);
        String itemPicture = cursor.getString(posterColumnIndex);

        final Context context = viewHolder.itemView.getContext();
//        Picasso.with(context).load(itemPicture).resizeDimen(60, 120).into(viewHolder.posterImageView);

        Glide.with(context)
                .load(itemPicture)
                .fitCenter()
                .crossFade()
                .thumbnail(0.1f)
                .into(viewHolder.posterImageView);

        viewHolder.titleTextView.setText(itemTitle);
        viewHolder.overviewTextView.setText(itemOverview);
        viewHolder.itemView.setTag(id);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fragment.onItemClick(id);
            }
        });

        DetailActivity.favPrefs = context.getSharedPreferences("favourites", Context.MODE_PRIVATE);
        Boolean a = DetailActivity.favPrefs.getBoolean("On" + context, true);
        if (a) {
            viewHolder.favToggle.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_yellow));
            viewHolder.favToggle.setChecked(true);
        } else {
//            !!!IMPORTANT!!! THESE LINES ARE NOT NECESSARY IN THE FOLLOWING CODE SINCE FAVOURITES ARE ALWAYS STAR YELLOW
//            AND TRUE UNLESS DELETED. IF DELETED THEY DO NOT APPEAR IN THE FAVOURITES TABLE ANYWAY
//            viewHolder.favToggle.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_grey));
//            viewHolder.favToggle.setChecked(false);
        }
        viewHolder.favToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    viewHolder.favToggle.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_yellow));
                    SharedPreferences.Editor editor = DetailActivity.favPrefs.edit();
                    editor.putBoolean("On" + context, true);
                    editor.apply();
                } else {
                    viewHolder.favToggle.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.star_grey));
//                    STAR GREY IS ONLY TEMPORARY STATE BETWEEN CLICK AND SHOWDELETECONFIRMATIONDIALOG AND CANCEL BUTTON
//                    SO WE DO NOT SAVE GREY STAR STATE IN THIS CASE
//                    SharedPreferences.Editor editor = DetailActivity.favPrefs.edit();
//                    editor.putBoolean("On"+ context, false);
//                    editor.apply();
                    fragment.showDeleteConfirmationDialogOneItem(viewHolder);
                }
            }
        });
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView titleTextView;
        public TextView overviewTextView;
        public ImageView posterImageView;
        public ToggleButton favToggle;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.title_fav);
            overviewTextView = view.findViewById(R.id.overview_fav);
            posterImageView = view.findViewById(R.id.thumbnail_fav);
            favToggle = view.findViewById(R.id.favListToggleButton);
        }
    }
}