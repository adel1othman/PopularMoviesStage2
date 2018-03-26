package com.adel.popularmoviesstage2;

import android.content.AsyncTaskLoader;
import android.content.Context;
import java.util.List;

public class UserReviewsLoader extends AsyncTaskLoader<List<String>> {

    private String mUrl;

    public UserReviewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    @Override
    public List<String> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        List<String> userReviews = QueryUtils.fetchUserReviewsData(mUrl);
        return userReviews;
    }
}