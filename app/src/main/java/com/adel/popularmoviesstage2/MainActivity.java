package com.adel.popularmoviesstage2;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import java.util.List;

import static com.adel.popularmoviesstage2.MovieAdapter.favoriteMovies;

public class MainActivity extends AppCompatActivity {

    private String REQUEST_URL;

    private static final int Movie_LOADER_ID = 1;

    private MovieAdapter mAdapter;
    static List<Movie> allMovies;
    static int opstionsID;

    RecyclerView movieRecyclerView;
    GridLayoutManager gridLayoutManager;
    LoaderManager.LoaderCallbacks<List<Movie>> myCallbacks;
    ProgressBar loadingIndicator;
    String myApiKey = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loadingIndicator = findViewById(R.id.loading_indicator);
        movieRecyclerView = findViewById(R.id.rv_movies);
        gridLayoutManager = new GridLayoutManager(this, 2);

        try {
            ApplicationInfo ai = getPackageManager().getApplicationInfo(this.getPackageName(), PackageManager.GET_META_DATA);
            Bundle bundle = ai.metaData;
            myApiKey = bundle.getString("POPULAR_MOVIES_API_KEY");
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        REQUEST_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + myApiKey;

        myCallbacks = new LoaderManager.LoaderCallbacks<List<Movie>>() {
            @Override
            public Loader<List<Movie>> onCreateLoader(int id, Bundle args) {
                loadingIndicator.setVisibility(View.VISIBLE);
                return new MovieLoader(getBaseContext(), REQUEST_URL);
            }

            @Override
            public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
                loadingIndicator.setVisibility(View.GONE);

                if (movies != null && !movies.isEmpty()) {
                    allMovies = movies;

                    movieRecyclerView.setLayoutManager(gridLayoutManager);
                    mAdapter = new MovieAdapter(allMovies);
                    movieRecyclerView.setAdapter(mAdapter);
                }
            }

            @Override
            public void onLoaderReset(Loader<List<Movie>> loader) {
                loadingIndicator.setVisibility(View.GONE);
            }
        };

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        if (networkInfo != null && networkInfo.isConnected()) {
            LoaderManager loaderManager = getLoaderManager();
            loaderManager.restartLoader(Movie_LOADER_ID, null, myCallbacks);
        } else {
            loadingIndicator.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (favoriteMovies != null && favoriteMovies.size() == 0){
            menu.findItem(R.id.action_popular).setChecked(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        opstionsID = item.getItemId();

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }

        switch (id){
            case R.id.action_favorite:

                if (favoriteMovies.size() > 0){
                    allMovies = favoriteMovies;
                    mAdapter = new MovieAdapter(allMovies);
                    movieRecyclerView.setAdapter(mAdapter);
                }else {
                    Toast.makeText(this, getString(R.string.no_favorite_movies),Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.action_popular:
                REQUEST_URL = "https://api.themoviedb.org/3/movie/popular?api_key=" + myApiKey;

                if (networkInfo != null && networkInfo.isConnected()) {
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(Movie_LOADER_ID, null, myCallbacks);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                }
                break;
            case R.id.action_top_rated:
                REQUEST_URL = "https://api.themoviedb.org/3/movie/top_rated?api_key=" + myApiKey;

                if (networkInfo != null && networkInfo.isConnected()) {
                    LoaderManager loaderManager = getLoaderManager();
                    loaderManager.restartLoader(Movie_LOADER_ID, null, myCallbacks);
                } else {
                    loadingIndicator.setVisibility(View.GONE);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
