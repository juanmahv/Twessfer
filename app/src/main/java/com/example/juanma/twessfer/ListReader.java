package com.example.juanma.twessfer;


import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Map;

import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.UserList;

public class ListReader extends AsyncTask<Twitter, UserList, Boolean> {

    private ArrayList<UserList> lists = new ArrayList<>();
    private TweetCallback tCallback;

    public ListReader(TweetCallback tcb) {
        tCallback = tcb;
    }

    @Override
    protected Boolean doInBackground(twitter4j.Twitter... twitter) {

        Boolean res = false;
        try {
            Map<String,RateLimitStatus> rateLImitStatus =  twitter[0].getRateLimitStatus("lists");
            RateLimitStatus rls = rateLImitStatus.get("/lists/list");
            int remaining = rls.getRemaining();
            if (remaining > 1) {
                String user = twitter[0].getScreenName();
                twitter4j.ResponseList<UserList> ul = twitter[0].list().getUserLists(user);
                for (twitter4j.UserList list : ul) {
                    publishProgress(list);
                }
                res = true;
            }
        } catch (TwitterException ex) {
            Log.d("MyApp","Exception:"+ex.getErrorMessage());
        }
        return res;
    }

    @Override
    protected void onProgressUpdate(twitter4j.UserList... list) {

        lists.add(list[0]);
    }

    @Override
    protected void onPostExecute(Boolean result) {

        tCallback.onUserLists(lists);
    }
}
