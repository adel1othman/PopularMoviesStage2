package com.adel.popularmoviesstage2;

import android.text.TextUtils;
import android.util.Log;

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
import java.util.List;

public final class QueryUtils {

    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    private QueryUtils() {
    }

    public static List<Movie> fetchMovieData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<Movie> movies = extractFeatureFromJson(jsonResponse);

        return movies;
    }

    public static String[] fetchTrailersData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        String[] trailers = getTrailers(jsonResponse);

        return trailers;
    }

    public static List<String> fetchUserReviewsData(String requestUrl) {
        URL url = createUrl(requestUrl);

        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        List<String> userReviews = getUserReviews(jsonResponse);

        return userReviews;
    }

    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

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

            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the movie JSON results.", e);
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

    private static List<Movie> extractFeatureFromJson(String movieJSON) {
        if (TextUtils.isEmpty(movieJSON)) {
            return null;
        }

        List<Movie> movies = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(movieJSON);

            if (!baseJsonResponse.isNull("results")){
                JSONArray moviesArray = baseJsonResponse.getJSONArray("results");

                for (int i = 0; i < moviesArray.length(); i++) {

                    JSONObject currentMovie = moviesArray.getJSONObject(i);
                    long id = currentMovie.getLong("id");
                    String title = currentMovie.getString("title");
                    String posterPath = currentMovie.getString("poster_path");
                    String overview = currentMovie.getString("overview");
                    Double voteAverage = currentMovie.getDouble("vote_average");
                    String releaseDate = currentMovie.getString("release_date");

                    Movie movie = new Movie(id, title, releaseDate, "http://image.tmdb.org/t/p/w185/" + posterPath.substring(1, posterPath.length()), voteAverage, overview);

                    movies.add(movie);
                }
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        return movies;
    }

    private static String[] getTrailers(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        String[] trailers = new String[0];

        try {
            JSONObject baseJsonResponse = new JSONObject(url);

            if (!baseJsonResponse.isNull("results")){
                JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

                trailers = new String[resultsArray.length()];
                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject currentTrailer = resultsArray.getJSONObject(i);
                    String key = currentTrailer.getString("key");

                    trailers[i] =  key;
                }
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        return trailers;
    }

    private static List<String> getUserReviews(String url) {
        if (TextUtils.isEmpty(url)) {
            return null;
        }

        ArrayList<String> userReviews = new ArrayList<>();

        try {
            JSONObject baseJsonResponse = new JSONObject(url);

            if (!baseJsonResponse.isNull("results")){
                JSONArray resultsArray = baseJsonResponse.getJSONArray("results");

                for (int i = 0; i < resultsArray.length(); i++) {

                    JSONObject currentreview = resultsArray.getJSONObject(i);
                    String author = currentreview.getString("author");
                    String content = currentreview.getString("content");

                    userReviews.add(author);
                    userReviews.add(content);
                }
            }
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the book JSON results", e);
        }

        return userReviews;
    }
}