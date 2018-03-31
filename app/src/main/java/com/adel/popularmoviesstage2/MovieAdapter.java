package com.adel.popularmoviesstage2;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder> {

    private Context context;
    private List<Movie> mMovies;
    static ArrayList<Movie> favoriteMovies;

    public MovieAdapter(List<Movie> movies) {
        mMovies = movies;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        context = viewGroup.getContext();

        View view = View.inflate(context, R.layout.list_item, null);

        favoriteMovies = new ArrayList<>();
        String[] projection = {
                MovieContract.MovieEntry._ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_ID,
                MovieContract.MovieEntry.COLUMN_MOVIE_Movie_Poster,
                MovieContract.MovieEntry.COLUMN_MOVIE_Release_Date,
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
                MovieContract.MovieEntry.COLUMN_MOVIE_Overview,
                MovieContract.MovieEntry.COLUMN_MOVIE_Vote_Average };

        MovieProvider movieProvider = new MovieProvider();
        movieProvider.mDbHelper = new MovieDbHelper(context);
        Cursor cursor = movieProvider.query(MovieContract.MovieEntry.CONTENT_URI, projection,null,null,null);

        if (cursor != null) {

            if (cursor.moveToFirst()) {
                do {
                    favoriteMovies.add(new Movie(cursor.getInt(1), cursor.getString(4), cursor.getString(3),
                            cursor.getString(2), cursor.getDouble(6), cursor.getString(5)));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Log.d(TAG, "#" + position);
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return (mMovies!=null? mMovies.size():0);
    }


    public class MovieViewHolder extends RecyclerView.ViewHolder {

        LinearLayout container;
        ImageView movieImage;
        TextView title;
        TextView review;
        SeekBar SBReview;
        ImageView favorite;

        public MovieViewHolder(View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.item_container);
            movieImage = itemView.findViewById(R.id.img_movie_main);
            title = itemView.findViewById(R.id.tv_title);
            review = itemView.findViewById(R.id.tv_review);
            SBReview = itemView.findViewById(R.id.sb_review);
            favorite = itemView.findViewById(R.id.ico_favorite);
        }

        void bind(int listIndex) {
            for (Movie movie:favoriteMovies) {
                if (mMovies.get(listIndex).getmMovieID() == movie.getmMovieID()){
                    favorite.setImageResource(R.drawable.ic_star1);
                    break;
                }
            }

            container.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchDetailActivity(getAdapterPosition());
                }
            });

            Picasso.with(context)
                    .load(mMovies.get(listIndex).getmMoviePoster())
                    .into(movieImage);

            title.setText(mMovies.get(listIndex).getmTitle());
            review.setText(String.valueOf(mMovies.get(listIndex).getmVoteAverage()));
            SBReview.setEnabled(false);
            SBReview.setMax(100);
            SBReview.setProgress(mMovies.get(listIndex).getmVoteAverage().intValue() * 10);
            favorite.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MovieDbHelper mDbHelper = new MovieDbHelper(context);
                    SQLiteDatabase database = mDbHelper.getReadableDatabase();
                    String count = "SELECT count(*) FROM movies WHERE movieID LIKE '"+ mMovies.get(getAdapterPosition()).getmMovieID() +"'";
                    Cursor mcursor = database.rawQuery(count, null);
                    mcursor.moveToFirst();
                    int icount = mcursor.getInt(0);
                    mcursor.close();
                    if(icount == 0) {
                        favorite.setImageResource(R.drawable.ic_star1);

                        ContentValues values = new ContentValues();

                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovies.get(getAdapterPosition()).getmMovieID());
                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Movie_Poster, mMovies.get(getAdapterPosition()).getmMoviePoster());
                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Release_Date, mMovies.get(getAdapterPosition()).getmReleaseDate());
                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mMovies.get(getAdapterPosition()).getmTitle());
                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Vote_Average, mMovies.get(getAdapterPosition()).getmVoteAverage());
                        values.put(MovieContract.MovieEntry.COLUMN_MOVIE_Overview, mMovies.get(getAdapterPosition()).getmPlotSynopsis());

                        Uri newUri = context.getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, values);

                        favoriteMovies.add(new Movie(values.getAsLong(MovieContract.MovieEntry.COLUMN_MOVIE_ID), values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE),
                                values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_Release_Date), values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_Movie_Poster),
                                values.getAsDouble(MovieContract.MovieEntry.COLUMN_MOVIE_Vote_Average), values.getAsString(MovieContract.MovieEntry.COLUMN_MOVIE_Overview)));

                        Toast.makeText(context, context.getString(R.string.insert_movie_successful), Toast.LENGTH_SHORT).show();
                    }else {
                        String queryID = "SELECT _ID, movieID FROM movies WHERE movieID LIKE '"+ mMovies.get(getAdapterPosition()).getmMovieID() +"'";
                        Cursor cursor = database.rawQuery(queryID, null);
                        cursor.moveToFirst();
                        int idColumnIndex = cursor.getColumnIndex(MovieContract.MovieEntry._ID);
                        int movieIDIndex = cursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID);
                        final int myID = cursor.getInt(idColumnIndex);
                        final int movieID = cursor.getInt(movieIDIndex);
                        cursor.close();

                        Uri currentMovieUri = MovieContract.MovieEntry.currentMovieUri(myID);

                        int rowDeleted = context.getContentResolver().delete(currentMovieUri, null, null);

                        favorite.setImageResource(R.drawable.ic_star);

                        int movieIndex = -1;
                        for (Movie movie:favoriteMovies) {
                            if (movie.getmMovieID() == movieID){
                                movieIndex = favoriteMovies.indexOf(movie);
                                favoriteMovies.remove(movie);
                                break;
                            }
                        }

                        Toast.makeText(context, context.getString(R.string.delete_movie_successful),Toast.LENGTH_SHORT).show();

                        if (MainActivity.opstionsID == R.id.action_favorite){
                            delete(movieIndex);
                        }

                        if (favoriteMovies.size() == 0){
                            Intent intent = new Intent(context, MainActivity.class);
                            context.startActivity(intent);
                        }

                        //notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void delete(int position) {
        mMovies.remove(position);
        notifyItemRemoved(position);
    }

    private void launchDetailActivity(int position) {
        Intent intent = new Intent(context, DetailActivity.class);
        intent.putExtra(DetailActivity.EXTRA_POSITION, position);

        context.startActivity(intent);
    }
}
