package com.example.juanma.twessfer;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;
import twitter4j.Twitter;

public class URLRequester extends AsyncTask<Twitter, Void, Boolean> {

    private TweetCallback tCallback;

    public URLRequester(TweetCallback tcb) {
        tCallback = tcb;
    }

    @Override
    protected Boolean doInBackground(Twitter... twitter) {
        try {

            RequestToken requestToken = twitter[0].getOAuthRequestToken();
            String url = requestToken.getAuthorizationURL();
            tCallback.postRequestToken(requestToken);
            tCallback.postAuthenticationUrl(url);
            return true;

        } catch (TwitterException te) {
            Log.d("err:",te.getMessage());
            return false;
        }
    }
}