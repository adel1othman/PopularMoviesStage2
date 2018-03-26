package com.adel.popularmoviesstage2;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class TrailersAdapter extends ArrayAdapter<String>  {

    public TrailersAdapter(Context context, String[] trailersURL) {
        super(context, 0, trailersURL);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_trailer, parent, false);
        }

        String currentURL = "https://www.youtube.com/watch?v=" + getItem(position);

        ImageView trailerImageView = listItemView.findViewById(R.id.iv_trailer);
        final Uri uriYouTube = Uri.parse(currentURL);
        String url = "https://img.youtube.com/vi/"+getItem(position)+"/0.jpg";
        Picasso.with(getContext())
                .load(url)
                .into(trailerImageView);
        trailerImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getContext().startActivity(new Intent(Intent.ACTION_VIEW, uriYouTube));
            }
        });

        return listItemView;
    }
}