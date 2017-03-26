package com.example.juanma.twessfer;


import java.util.Collections;
import java.util.List;
import java.util.Map;

import twitter4j.Paging;
import twitter4j.RateLimitStatus;
import twitter4j.Twitter;
import twitter4j.TwitterException;

public class ListTimeline extends Timeline {

    private Long myListId = 0L;

    public ListTimeline(Long listId,TweetCallback tcb) {
        super(tcb);
        myListId = listId;
    }

    @Override
    public List<twitter4j.Status> getStati(Twitter twitter, int page) {
        try {
            return twitter.getUserListStatuses(myListId, new Paging(page,20));
        }
        catch (TwitterException ex) {
            return Collections.emptyList();
        }
    }

    @Override
    public int remainingGets(Twitter twitter) {
        if (myListId <= 0L)
            return 0;

        try {
            Map<String, RateLimitStatus> rateLimitStatus = twitter.getRateLimitStatus("lists");
            RateLimitStatus rls = rateLimitStatus.get("/lists/list");
            return rls.getRemaining();
        } catch (TwitterException ex) {
            return 0;
        }
    }
}
