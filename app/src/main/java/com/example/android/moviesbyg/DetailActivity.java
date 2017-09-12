package com.example.android.moviesbyg;

import android.content.Context;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.example.android.moviesbyg.databinding.ActivityDetailBinding;
import com.squareup.picasso.Picasso;

/**
 * Created by Marcin on 2017-09-10.
 */

public class DetailActivity extends AppCompatActivity {


    private ActivityDetailBinding mDetailBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDetailBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        String title = getIntent().getStringExtra(MoviesAdapter.EXTRA_TITLE);
        mDetailBinding.part2.title.setText(title);

        String releaseDate = getIntent().getStringExtra(MoviesAdapter.EXTRA_RELEASE_DATE);
        mDetailBinding.part2.releaseDate.setText(releaseDate);

        String vote = getIntent().getStringExtra(MoviesAdapter.EXTRA_VOTE);
        mDetailBinding.part2.rating.setText(vote);

        String overview = getIntent().getStringExtra(MoviesAdapter.EXTRA_OVERVIEW);
        mDetailBinding.part3.overview.setText(overview);

        String poster = getIntent().getStringExtra(MoviesAdapter.EXTRA_POSTER);
        Context context = mDetailBinding.part1.poster.getContext();
        Picasso.with(context).load(poster).into(mDetailBinding.part1.poster);

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}