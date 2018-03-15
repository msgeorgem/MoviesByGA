package com.example.android.moviesbyg.MovieReviews;

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

public class QueryReviewsUtils {
    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryReviewsUtils.class.getSimpleName();

    private static final String ERROR_MESSAGEC = "Problem parsing the clip JSON results";
    private static final String MDB_RESULTSR = "results";
    private static final String MDB_AUTHORR = "author";
    private static final String MDB_CONTENTR = "content";
    private static final String MDB_REVIEW_URL = "url";

    /**
     * Create a private constructor because no one should ever create a {@link QueryReviewsUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryReviewsUtils() {
    }

    /**
     * Query the USGS dataset and return an {@link ArrayList <SingleMovieReview>} object to represent a single news.
     */
    public static ArrayList<SingleMovieReview> fetchReviesData(String requestUrl) {
        Log.i(LOG_TAG, "fetchReviews");

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
        ArrayList<SingleMovieReview> singleMovieReview = extractMovieReviews(jsonResponse);

        // Return the {@link Event}
        return singleMovieReview;

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
            Log.e(LOG_TAG, ERROR_MESSAGEC, e);
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
    public static ArrayList<SingleMovieReview> extractMovieReviews(String singleMovieReviewJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(singleMovieReviewJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding singleMovieClip to
        ArrayList<SingleMovieReview> singleMovieReview = new ArrayList<>();

        // Try to parse the SAMPLE_JSON_RESPONSE. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {
            JSONObject root = new JSONObject(singleMovieReviewJSON);
            JSONArray jsonResultsArray = root.getJSONArray(MDB_RESULTSR);

            for (int i = 0; i < jsonResultsArray.length(); i++) {
                JSONObject movie = jsonResultsArray.getJSONObject(i);
                String reviewAuthor = movie.getString(MDB_AUTHORR);
                String reviewContent = movie.getString(MDB_CONTENTR);

                String reviewURL = movie.getString(MDB_REVIEW_URL);

                singleMovieReview.add(new SingleMovieReview(reviewURL, reviewAuthor, reviewContent));
            }

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", ERROR_MESSAGEC, e);
        }

        // Return the list of news
        return singleMovieReview;
    }

}