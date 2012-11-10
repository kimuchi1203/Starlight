package com.kimuchi1203.starlight;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class TwitterHomeTimelineLoader extends AsyncTaskLoader<ResponseList<twitter4j.Status>> {
	Twitter twitter;
	Paging paging;
	
	public TwitterHomeTimelineLoader(Context context, Twitter twitter2, Paging paging2) {
		super(context);
		twitter = twitter2;
		paging = paging2;
	}

	@Override
	public ResponseList<Status> loadInBackground() {
		ResponseList<twitter4j.Status> home = null;
		try {
			if(null!=paging){
				home = twitter.getHomeTimeline(paging);
			} else {
				home = twitter.getHomeTimeline();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return home;
	}
	
	public Paging getPaging() {
		return paging;
	}
}
