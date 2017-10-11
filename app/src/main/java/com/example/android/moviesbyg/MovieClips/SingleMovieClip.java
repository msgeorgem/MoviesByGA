package com.example.android.moviesbyg.MovieClips;

/**
 * Created by Marcin on 2017-09-15.
 */

public class SingleMovieClip {

    private String mClip;
    private String mClipName;
    private String mClipType;

    public SingleMovieClip(String clipUrl, String clipName, String clipType) {
        mClip = clipUrl;
        mClipName = clipName;
        mClipType = clipType;
    }

    public String getClipUrl() {
        return mClip;
    }

    public String getClipName() {
        return mClipName;
    }

    public String getClipType() {
        return mClipType;
    }

}
