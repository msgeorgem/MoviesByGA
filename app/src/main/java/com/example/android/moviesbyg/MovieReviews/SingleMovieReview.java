package com.example.android.moviesbyg.MovieReviews;

/**
 * Created by Marcin on 2017-09-15.
 */

public class SingleMovieReview {

    private String mReviewUrl;
    private String mReviewAuthor;
    private String mReviewContent;

    public SingleMovieReview(String reviewUrl, String reviewAuthor, String reviewContent) {
        mReviewUrl = reviewUrl;
        mReviewAuthor = reviewAuthor;
        mReviewContent = reviewContent;
    }

    public String getReviewUrl() {
        return mReviewUrl;
    }

    public String getReviewAuthor() {
        return mReviewAuthor;
    }

    public String getReviewContent() {
        return mReviewContent;
    }

}
