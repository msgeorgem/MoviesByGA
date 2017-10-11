package com.example.android.moviesbyg.MovieClips;

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

public class MovieClipsAdapter extends RecyclerView.Adapter<MovieClipsAdapter.ClipsViewHolder> {


    private static final int IMG_LOADER = 23;
    private DetailActivity activity = new DetailActivity();
    private ArrayList<SingleMovieClip> mListAdapterC;
    private Context context;

    public MovieClipsAdapter(ArrayList<SingleMovieClip> listClips) {
        this.context = context;
        mListAdapterC = listClips;

    }

    @Override
    public int getItemCount() {
        return mListAdapterC.size();
    }

    @Override
    public ClipsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_videos, parent, false);
        ClipsViewHolder vh = new ClipsViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolder(final ClipsViewHolder viewHolder, int position) {
        // Get the {@link movieClip} object located at this position in the list
        final SingleMovieClip movieClip = mListAdapterC.get(position);

        viewHolder.nameTextView.setText(movieClip.getClipName());
        viewHolder.typeTextView.setText(movieClip.getClipType());
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                Context context = viewHolder.itemView.getContext();
                Uri clipUri = Uri.parse(movieClip.getClipUrl());
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, clipUri);
                context.startActivity(browserIntent);
            }
        });
    }


    public class ClipsViewHolder extends RecyclerView.ViewHolder {
        public String clipURL;
        private ImageView imageView;
        private TextView nameTextView;
        private TextView typeTextView;


        private ClipsViewHolder(View view) {
            super(view);
            this.imageView = view.findViewById(R.id.thumbnail_YT);
            this.nameTextView = view.findViewById(R.id.name);
            this.typeTextView = view.findViewById(R.id.type);

        }
    }
}