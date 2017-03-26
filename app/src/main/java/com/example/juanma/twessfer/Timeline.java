package com.example.juanma.twessfer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;

public abstract class Timeline extends AsyncTask<Twitter, Row, Boolean> {

    private TweetCallback tCallback;
    private ArrayList<Row> items = new ArrayList<>();

    public Timeline(TweetCallback tcb) {
        tCallback = tcb;
    }

    private Bitmap getAvatar(twitter4j.User user) {

        int w = 32, h = 32;
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap avatar = Bitmap.createBitmap(w, h, conf);

        String imgLocation =  user.getProfileImageURL();
        try {
            InputStream in = new java.net.URL(imgLocation).openStream();
            avatar = BitmapFactory.decodeStream(in);
        } catch ( Exception e)
        {
            Log.e("Error downloading", e.getMessage());
            e.printStackTrace();
        }

        return avatar;
    }

    public abstract List<twitter4j.Status> getStati(Twitter twitter, int page);

    public abstract int remainingGets(Twitter twitter);

    @Override
    protected Boolean doInBackground(Twitter... twitter) {
        Boolean res = false;

        int remaining = remainingGets(twitter[0]);
        if (remaining > 0 ) {

            List<twitter4j.Status> stati = getStati(twitter[0],tCallback.currentPage());

            for (twitter4j.Status status : stati) {
                Row newRow = new Row();
                newRow.status = status;
                newRow.avatar = getAvatar(status.getUser());
                publishProgress(newRow);
            }

            res = true;
        }

        return res;
    }

    @Override
    protected void onProgressUpdate(Row... rows) {

        items.add(rows[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {

        tCallback.postRows(items);
    }
}