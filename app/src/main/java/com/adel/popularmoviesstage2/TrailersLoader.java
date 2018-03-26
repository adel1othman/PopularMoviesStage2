package com.adel.popularmoviesstage2;

import android.content.AsyncTaskLoader;
import android.content.Context;

public class TrailersLoader extends AsyncTaskLoader<String[]> {

    private String mUrl;

    public TrailersLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public String[] loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        String[] trailers = QueryUtils.fetchTrailersData(mUrl);
        return trailers;
    }
}