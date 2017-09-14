package com.example.android.moviesbyg;

/**
 * Created by Marcin on 2017-09-12.
 */

public class SingleMovie {
    private final String mPoster;
    private String mTitle;
    private String mOverview;
    private String mReleaseDate;
    private String mVoting;

    /**
     * Create a new SingleMovieobject.
     * @param title     is the title ot the movie
     * @param overview is the overview of the movie
     * @param releaseDate  is the publish date of the movie
     * @param voting  is the voting of the movie
     * @param poster is the url of the thumbnail about specific movie
     */

    public SingleMovie(String title, String overview, String releaseDate, String voting,
                       String poster) {
        mPoster = poster;
        mTitle = title;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mVoting = voting;
    }
    /**
     * Get the thumbnail of the article.
     */
    public String getPoster() {
        return mPoster;
    }
    /**
     * Get the title of the article.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the shorttext of the article.
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * Get the date of the article.
     */
    public String getmReleaseDate(){
        return mReleaseDate;
    }

    /**
     * Get the date of the article.
     */
    public String getVoting(){
        return mVoting;
    }


}
