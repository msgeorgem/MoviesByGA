package com.example.android.moviesbyg.MovieReviews;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesbyg.DetailActivity;
import com.example.android.moviesbyg.R;

import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-15.
 */

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.ClipsViewHolder> {


    private static final int IMG_LOADER = 24;
    private DetailActivity activity = new DetailActivity();
    private ArrayList<SingleMovieReview> mListAdapterR;
    private Context context;

    public MovieReviewsAdapter(ArrayList<SingleMovieReview> listReviews) {
        mListAdapterR = listReviews;

    }

    @Override
    public int getItemCount() {
        return mListAdapterR.size();
    }

    @Override
    public ClipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_reviews, parent, false);
        ClipsViewHolder vh = new ClipsViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ClipsViewHolder viewHolder, int position) {
        // Get the {@link movieClip} object located at this position in the list
        final SingleMovieReview movieReview = mListAdapterR.get(position);

        viewHolder.authorTextView.setText(movieReview.getReviewAuthor());
        viewHolder.contentTextView.setText(movieReview.getReviewContent());
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Context context = viewHolder.itemView.getContext();
                Uri reviewUri = Uri.parse(movieReview.getReviewUrl());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, reviewUri);
                context.startActivity(browserIntent);
            }
        });
    }


    public class ClipsViewHolder extends RecyclerView.ViewHolder {
        public String reviewURL;
        private ImageView imageView;
        private TextView authorTextView;
        private TextView contentTextView;


        private ClipsViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.thumbnail_review);
            this.authorTextView = view.findViewById(R.id.author);
            this.contentTextView = view.findViewById(R.id.content);

        }
    }
}