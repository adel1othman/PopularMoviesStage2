package com.adel.popularmoviesstage2;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;


public class MovieLoader extends AsyncTaskLoader<List<Movie>> {

    private String mUrl;

    public MovieLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<Movie> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<Movie> movies = null;
        if (MainActivity.opstionsID == R.id.action_favorite){
            movies = MovieAdapter.favoriteMovies;
        }else {
            movies = QueryUtils.fetchMovieData(mUrl);
        }

        return movies;
    }
}