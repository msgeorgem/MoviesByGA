package com.example.android.moviesbyg.MovieClips;

/**
 * Created by Marcin on 2017-09-15.
 */

public class SingleMovieClip {

    private String mClipUrl;
    private String mClipId;
    private String mClipName;
    private String mClipType;

    public SingleMovieClip(String clipUrl, String clipId, String clipName, String clipType) {
        mClipUrl = clipUrl;
        mClipName = clipName;
        mClipType = clipType;
        mClipId = clipId;
    }

    public String getClipUrl() {
        return mClipUrl;
    }

    public String getClipName() {
        return mClipName;
    }

    public String getClipType() {
        return mClipType;
    }

    public String getClipId() {
        return mClipId;
    }

}
