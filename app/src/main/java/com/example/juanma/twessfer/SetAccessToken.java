package com.example.juanma.twessfer;

import android.os.AsyncTask;
import android.util.Log;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;

public class SetAccessToken extends AsyncTask<Twitter, Void, Boolean> {

    private TweetCallback tCallback;
    private RequestToken requestToken;
    private String accessPin;
    private AccessToken accessToken;

    public SetAccessToken(TweetCallback tcb, RequestToken token, String pin) {

        tCallback = tcb;
        requestToken = token;
        accessPin = pin;
        accessToken = new AccessToken("","");
    }

    @Override
    protected Boolean doInBackground(Twitter... twitter) {
        try {

            accessToken = twitter[0].getOAuthAccessToken(requestToken, accessPin);
            return true;

        } catch (TwitterException te) {
            Log.d("err:", te.getMessage());
            return false;
        }
    }

    @Override
    protected void onPostExecute(Boolean result) {
        if (result) {
            tCallback.postAccessToken(accessToken);
            tCallback.authenticationFinished();
        }
    }
}