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
    private String mVideosPath;
    private String mReviewsPath;
    private String mMovieID;

    /**
     * Create a new SingleMovieobject.
     * @param title     is the title ot the movie
     * @param overview is the overview of the movie
     * @param releaseDate  is the publish date of the movie
     * @param voting  is the voting of the movie
     * @param poster is the url of the poster about specific movie
     */

    public SingleMovie(String title, String overview, String releaseDate, String voting,
                       String poster, String videosPath, String reviewsPath, String movieID) {
        mPoster = poster;
        mTitle = title;
        mOverview = overview;
        mReleaseDate = releaseDate;
        mVoting = voting;
        mVideosPath = videosPath;
        mReviewsPath = reviewsPath;
        mMovieID = movieID;
    }
    /**
     * Get the poster of the movie.
     */
    public String getPoster() {
        return mPoster;
    }
    /**
     * Get the poster of the movie.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the overview of the movie.
     */
    public String getOverview() {
        return mOverview;
    }

    /**
     * Get the date of the movie.
     */
    public String getmReleaseDate(){
        return mReleaseDate;
    }

    /**
     * Get the VideosPath of the movie.
     */
    public String getVideosPath() {
        return mVideosPath;
    }

    /**
     * Get the VideosPath of the movie.
     */
    public String getReviewsPath() {
        return mReviewsPath;
    }

    /**
     * Get the rating of the movie.
     */
    public String getVoting(){
        return mVoting;
    }

    /**
     * Get the ID of the movie.
     */
    public String getMovieID() {
        return mMovieID;
    }



}
