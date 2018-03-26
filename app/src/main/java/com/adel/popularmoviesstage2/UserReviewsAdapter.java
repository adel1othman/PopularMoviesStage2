package com.adel.popularmoviesstage2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class UserReviewsAdapter extends ArrayAdapter<String> {

    public UserReviewsAdapter(Context context, List<String> reviews) {
        super(context, 0, reviews);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item_reviews, parent, false);
        }

        String currentReviewAuthor = "";
        String currentReviewContent = "";

        if (position % 2 == 0){
            currentReviewAuthor = getItem(position);
            currentReviewContent = getItem(position + 1);

            TextView author = listItemView.findViewById(R.id.tv_author);
            TextView content = listItemView.findViewById(R.id.tv_content);

            author.setText(currentReviewAuthor);
            content.setText(currentReviewContent);
        }

        return listItemView;
    }
}