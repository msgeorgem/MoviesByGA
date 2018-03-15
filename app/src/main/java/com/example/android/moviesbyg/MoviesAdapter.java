package com.example.android.moviesbyg;

import android.content.Context;
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


    private final OnItemClickListener listener;
    private ArrayList<SingleMovie> mListAdapter;


    public MoviesAdapter(ArrayList<SingleMovie> listMovies, OnItemClickListener listener) {
        this.mListAdapter = listMovies;
        this.listener = listener;
    }

    @Override
    public int getItemCount() {
        return mListAdapter.size();
    }

    @Override
    public MoviesViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_movies, parent, false);
        MoviesViewHolder vh = new MoviesViewHolder(itemView);
        return vh;
    }


    @Override
    public void onBindViewHolder(final MoviesViewHolder viewHolder, int position) {
        viewHolder.bind(mListAdapter.get(position), listener);
        // Get the {@link News} object located at this position in the list
        final SingleMovie currentMovie = mListAdapter.get(position);

        viewHolder.imageURL = currentMovie.getPoster();
        Context context = viewHolder.itemView.getContext();
        Picasso.with(context).load(viewHolder.imageURL).fit().into(viewHolder.imageView);

    }

    public interface OnItemClickListener {
        void onItemClick(SingleMovie item);
    }

    static class MoviesViewHolder extends RecyclerView.ViewHolder {
        private String imageURL;
        private ImageView imageView;
        private ImageView mFavGage;


        private MoviesViewHolder(View view) {
            super(view);
            this.imageView = view
                    .findViewById(R.id.thumbnail);
        }


        public void bind(final SingleMovie item, final OnItemClickListener listener) {

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listener.onItemClick(item);
                }
            });
        }

    }

}