package com.example.android.moviesbyg.MovieClips;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.moviesbyg.SingleMovie;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Created by Marcin on 2017-09-15.
 */

public class QueryClipsUtils {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryClipsUtils.class.getSimpleName();

    private static final String API_KEY = "api_key";


    private static final String ERROR_MESSAGEC = "Problem parsing the clip JSON results";
    private static final String MDB_RESULTSC = "results";
    private static final String MDB_NAMEC = "name";
    private static final String MDB_TYPEC = "type";
    private static final String MDB_IDC = "id";
    private static final String MDB_KEYC = "key";
    private static final String YT_BASE = "https://www.youtube.com/watch?v=";

    private static final String MDB_BACKDROP_PATH = "backdrop_path";

    /**
     * Create a private constructor because no one should ever create a {@link QueryClipsUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryClipsUtils() {
    }


    /**
     * Query the USGS dataset and return an {@link ArrayList <SingleMovieClip>} object to represent a single news.
     */
    public static ArrayList<SingleMovieClip> fetchMoviesData(String requestUrl) {
        Log.i(LOG_TAG, "fetchClips");

        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }
        // Extract relevant fields from the JSON response and create an {@link Event} object
        ArrayList<SingleMovieClip> singleMovieClip = extractMovieClips(jsonResponse);

        // Return the {@link Event}
        return singleMovieClip;

    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        Log.i(LOG_TAG, "parsingClipsJson");
        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return a list of {@link SingleMovie} objects that has been built up from
     * parsing a JSON response.
     */
    public static ArrayList<SingleMovieClip> extractMovieClips(String singleMovieClipJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(singleMovieClipJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding singleMovieClip to
        ArrayList<SingleMovieClip> singleMovieClip = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(singleMovieClipJSON);
            JSONArray jsonResultsArray = root.getJSONArray(MDB_RESULTSC);

            for (int i = 0; i < jsonResultsArray.length(); i++) {
                JSONObject movie = jsonResultsArray.getJSONObject(i);
                String clipName = movie.getString(MDB_NAMEC);
                String clipType = movie.getString(MDB_TYPEC);

                String clipId = movie.getString(MDB_KEYC);
                String ytclipId = YT_BASE + clipId;
//                    String reviewIdPath = MDB_MOVIE_PATH1+movieId+MDB_REVIEWS_PATH2;
//
//
//                    String clipPath = MDB_POSTER_PATH+moviePoster;

                singleMovieClip.add(new SingleMovieClip(ytclipId, clipName, clipType));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of news
        return singleMovieClip;
    }

}