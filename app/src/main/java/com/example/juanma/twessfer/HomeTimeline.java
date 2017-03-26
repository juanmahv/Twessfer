package com.example.juanma.twessfer;

import java.util.Collections;
import java.util.List;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class HomeTimeline extends Timeline {

    public HomeTimeline(TweetCallback tcb) {
        super(tcb);
    }

    @Override
    public List<twitter4j.Status> getStati(Twitter twitter, int page) {
        try {
            return twitter.getHomeTimeline(new Paging(page++));
        }
        catch (TwitterException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public int remainingGets(Twitter twitter) {
        return 15; //TBD calculate proper value
    }
}
