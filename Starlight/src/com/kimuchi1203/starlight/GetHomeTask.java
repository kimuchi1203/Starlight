package com.kimuchi1203.starlight;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.os.AsyncTask;

public class GetHomeTask extends AsyncTask<Paging, Void, ResponseList<twitter4j.Status>> {
	Twitter twitter;
	
	public GetHomeTask(Twitter twitter2) {
		twitter = twitter2;
	}

	@Override
	protected ResponseList<twitter4j.Status> doInBackground(Paging... params) {
		ResponseList<twitter4j.Status> home = null;
		try {
			if(null!=params[0]){
				home = twitter.getHomeTimeline(params[0]);
			} else {
				home = twitter.getHomeTimeline();
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return home;
	}
}
