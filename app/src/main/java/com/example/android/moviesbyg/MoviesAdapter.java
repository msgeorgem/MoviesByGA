package com.example.android.moviesbyg;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-12.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesViewHolder> {



    public static final String EXTRA_TITLE = "EXTRA_TITLE";
    public static final String EXTRA_OVERVIEW = "EXTRA_OVERVIEW";
    public static final String EXTRA_RELEASE_DATE = "EXTRA_RELEASE_DATE";
    public static final String EXTRA_VOTE = "EXTRA_VOTE";
    public static final String EXTRA_ID = "EXTRA_ID";
    public static final String EXTRA_POSTER = "EXTRA_POSTER";

    private static final int IMG_LOADER = 22;
    private static final String IMG_URL_EXTRA = "img";
    private MoviesActivity activity = new MoviesActivity();
    private ArrayList<SingleMovie> mListAdapter;


    public MoviesAdapter(ArrayList<SingleMovie> listMovies) {
        mListAdapter = listMovies;
    }

    @Override
    public int getItemCount() {
        return mListAdapter.size();
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_item, parent, false);
        MoviesViewHolder vh = new MoviesViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final MoviesViewHolder viewHolder, int position) {
        // Get the {@link News} object located at this position in the list
        final SingleMovie currentMovie = mListAdapter.get(position);

        viewHolder.imageURL = currentMovie.getPoster();
        Context context = viewHolder.itemView.getContext();
        Picasso.with(context).load(viewHolder.imageURL).fit().into(viewHolder.imageView);

//        viewHolder.titleTextView.setText(currentMovie.getTitle());
//        viewHolder.shortTextView.setText(currentMovie.getShorttext());
//
//
//        String formattedDate = (currentMovie.getDateTime()).substring(0, 10);
//        viewHolder.dateView.setText(formattedDate);
//        viewHolder.typeTextView.setText(currentMovie.getSectionName());
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Context context = viewHolder.itemView.getContext();
                String currentMovieTitleString = currentMovie.getTitle();
                String currentMovieOverviewString = currentMovie.getOverview();
                String currentMovieReleaseDateString = currentMovie.getmReleaseDate();
                String currentMovieVotingString = currentMovie.getVoting();
                String currentMoviePosterString = currentMovie.getPoster();
                String currentMovieID = currentMovie.getMovieID();

                Intent intent1 = new Intent(view.getContext(), DetailActivity.class);
                intent1.putExtra(EXTRA_TITLE, currentMovieTitleString);
                intent1.putExtra(EXTRA_OVERVIEW, currentMovieOverviewString);
                intent1.putExtra(EXTRA_RELEASE_DATE, currentMovieReleaseDateString);
                intent1.putExtra(EXTRA_VOTE, currentMovieVotingString);
                intent1.putExtra(EXTRA_POSTER, currentMoviePosterString);
                intent1.putExtra(EXTRA_ID, currentMovieID);

                context.startActivity(intent1);
            }
        });
    }


    public class MoviesViewHolder extends RecyclerView.ViewHolder {
        public String imageURL;
        public ImageView imageView;
//        private TextView titleTextView;
//        private TextView shortTextView;
//        private TextView dateView;
//        private TextView typeTextView;


        public MoviesViewHolder(View view) {
            super(view);
            this.imageView = view
                    .findViewById(R.id.thumbnail);
//            this.titleTextView = (TextView) view
//                    .findViewById(R.id.poster);
//            this.shortTextView = (TextView) view
//                    .findViewById(R.id.shorttext);
//            this.dateView = (TextView) view
//                    .findViewById(date);
//            this.typeTextView = (TextView) view
//                    .findViewById(R.id.SectionName);

        }
    }
}