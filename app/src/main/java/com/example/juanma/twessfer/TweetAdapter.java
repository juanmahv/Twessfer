package com.example.juanma.twessfer;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * \brief TweetAdapter implements a specific ArrayAdapter to
 * display Tweets.
 */

public class TweetAdapter extends ArrayAdapter<Tweet> {

    public TweetAdapter(Context context, ArrayList<Tweet> rows) {
        super(context, R.layout.activity_listview, rows);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        // Get the data item for this position
        Tweet row = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.activity_listview, parent, false);
        }

        String statusText = row.status.getText();
        TextView label = (TextView) convertView.findViewById(R.id.label);
        label.setText(statusText);

        twitter4j.User user = row.status.getUser();
        String name = user.getName();
        String sName = "@"+user.getScreenName();

        Date date = row.status.getCreatedAt();
        SimpleDateFormat ft = new SimpleDateFormat("[E yyyy.MM.dd 'at' hh:mm:ss]");

        String when = ft.format(date);
        String info= sName +" - "+name+" "+when;
        TextView title = (TextView) convertView.findViewById(R.id.title);
        title.setText(info);

        ImageView imageView = (ImageView) convertView.findViewById(R.id.image);
        imageView.setImageBitmap(row.avatar);

        // Return the completed view to render on screen
        return convertView;
    }
}

