package com.example.juanma.twessfer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import twitter4j.Twitter;

/** \brief Class to retrieve tweets.
 *
 *  This class provides functionality to retrieve
 *  states form a Timeline.
 */

public abstract class Timeline extends AsyncTask<Twitter, Tweet, Boolean> {

    private TweetCallback tCallback;
    private ArrayList<Tweet> items = new ArrayList<>();

    /**
     * Constructor
     * @param tcb Instance to the Main activity to notify actions.
     */
    public Timeline(TweetCallback tcb) {
        tCallback = tcb;
    }

    /**
     * Read the avatar of the status line.
     * @param user avatar
     * @return avatar as Bitmap
     */
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
                Tweet tweet = new Tweet();
                tweet.status = status;
                tweet.avatar = getAvatar(status.getUser());
                publishProgress(tweet);
            }

            res = true;
        }

        return res;
    }

    @Override
    protected void onProgressUpdate(Tweet... rows) {

        items.add(rows[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {

        tCallback.postRows(items);
    }
}