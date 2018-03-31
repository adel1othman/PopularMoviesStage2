package com.adel.popularmoviesstage2;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

import static com.adel.popularmoviesstage2.MainActivity.allMovies;
import static com.adel.popularmoviesstage2.MovieAdapter.favoriteMovies;

public class DetailActivity extends AppCompatActivity {
    public static final String EXTRA_POSITION = "extra_position";
    private static final int DEFAULT_POSITION = -1;
    static int position = -1;
    List<Movie> movies;
    private String REQUEST_URL_TRAILERS;
    private String REQUEST_URL_REVIEWS;
    String myApiKey = "";
    String[] trailers;
    LoaderManager.LoaderCallbacks<String[]> myCallbacksTrailers;
    LoaderManager.LoaderCallbacks<List<String>> myCallbacksReviews;

    ScrollView scrollView;
    ImageView ivPoster;
    TextView tvTitle;
    TextView tvReleaseDate;
    TextView tvPlotSynopsis;
    TextView tvVoteAverage;
    GridView gridViewTrailers;
    ListView listViewReviews;
    ImageView imageViewFavorite;

    @Override
    protected void onCreate( Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            myApiKey = bundle.getString("POPULAR_MOVIES_API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        movies = allMovies;

        scrollView = findViewById(R.id.sc_container);
        ivPoster = findViewById(R.id.iv_poster);
        tvTitle = findViewById(R.id.tv_title);
        tvReleaseDate = findViewById(R.id.tv_release);
        tvPlotSynopsis = findViewById(R.id.tv_overview);
        tvVoteAverage = findViewById(R.id.tv_vote);
        gridViewTrailers = findViewById(R.id.gv_trailers_container);
        listViewReviews = findViewById(R.id.lv_user_reviews);
        imageViewFavorite = findViewById(R.id.ico_favorite_details);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
        }else {
            position = intent.getIntExtra(EXTRA_POSITION, DEFAULT_POSITION);
        }

        REQUEST_URL_TRAILERS = "https://api.themoviedb.org/3/movie/" + allMovies.get(position).getmMovieID() + "/videos?api_key=" + myApiKey;
        REQUEST_URL_REVIEWS = "https://api.themoviedb.org/3/movie/" + allMovies.get(position).getmMovieID() + "/reviews?api_key=" + myApiKey;

        myCallbacksTrailers = new LoaderManager.LoaderCallbacks<String[]>() {
            @Override
            public Loader<String[]> onCreateLoader(int id, Bundle args) {
                return new TrailersLoader(getBaseContext(), REQUEST_URL_TRAILERS);
            }

            @Override
            public void onLoadFinished(Loader<String[]> loader, String[] data) {
                if (data != null) {
                    trailers = data;

                    populateUI();
                }
            }

            @Override
            public void onLoaderReset(Loader<String[]> loader) {

            }
        };

        myCallbacksReviews = new LoaderManager.LoaderCallbacks<List<String>>() {
            @Override
            public Loader<List<String>> onCreateLoader(int id, Bundle args) {
                return new UserReviewsLoader(getBaseContext(), REQUEST_URL_REVIEWS);
            }

            @Override
            public void onLoadFinished(Loader<List<String>> loader, List<String> data) {
                if (data != null){
                    UserReviewsAdapter userReviewsAdapter = new UserReviewsAdapter(getBaseContext(), data);
                    listViewReviews.setAdapter(userReviewsAdapter);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<String>> loader) {

            }
        };

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(2, null, myCallbacksTrailers);
            loaderManager.restartLoader(3, null, myCallbacksReviews);
        }

        setTitle(allMovies.get(position).getmTitle());

        imageViewFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String[] projection = { MovieContract.MovieEntry._ID };
                MovieProvider movieProvider = new MovieProvider();
                movieProvider.mDbHelper = new MovieDbHelper(getBaseContext());
                Cursor cursor = movieProvider.query(MovieContract.MovieEntry.CONTENT_URI, projection,
                        MovieContract.MovieEntry.COLUMN_MOVIE_ID + "=" + allMovies.get(position).getmMovieID(), null,null);

                int myID = -1;
                try {
                    cursor.moveToFirst();
                    myID = cursor.getInt(cursor.getColumnIndex(MovieContract.MovieEntry._ID));
                    cursor.close();
                }catch (Exception ex){

                }

                if (myID != -1){
                    int rowDeleted = getBaseContext().getContentResolver().delete(MovieContract.MovieEntry.currentMovieUri(myID), null, null);

                    favoriteMovies.remove(allMovies.get(position));
                    imageViewFavorite.setImageResource(R.drawable.ic_star);
                    Toast.makeText(getBaseContext(), getString(R.string.delete_movie_successful),Toast.LENGTH_SHORT).show();
                }else {
                    ContentValues values = new ContentValues();

                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, allMovies.get(position).getmMovieID());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Movie_Poster, allMovies.get(position).getmMoviePoster());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Release_Date, allMovies.get(position).getmReleaseDate());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, allMovies.get(position).getmTitle());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Vote_Average, allMovies.get(position).getmVoteAverage());
                    values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Overview, allMovies.get(position).getmPlotSynopsis());

                    Uri newUri = getBaseContext().getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

                    favoriteMovies.add(allMovies.get(position));
                    imageViewFavorite.setImageResource(R.drawable.ic_star1);
                    Toast.makeText(getBaseContext(), getString(R.string.insert_movie_successful),Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, "Error", Toast.LENGTH_SHORT).show();
    }

    private void populateUI() {
        Picasso.with(this)
                .load(allMovies.get(position).getmMoviePoster())
                .into(ivPoster);

        tvTitle.setText(allMovies.get(position).getmTitle());
        tvReleaseDate.setText(allMovies.get(position).getmReleaseDate());
        tvPlotSynopsis.setText(allMovies.get(position).getmPlotSynopsis());
        tvVoteAverage.setText(String.valueOf(allMovies.get(position).getmVoteAverage()));

        TrailersAdapter trailersAdapter = new TrailersAdapter(getBaseContext(), trailers);
        gridViewTrailers.setAdapter(trailersAdapter);

        for (Movie movie:favoriteMovies) {
            if (allMovies.get(position).getmMovieID() == movie.getmMovieID()){
                imageViewFavorite.setImageResource(R.drawable.ic_star1);
                break;
            }
        }
    }
}
