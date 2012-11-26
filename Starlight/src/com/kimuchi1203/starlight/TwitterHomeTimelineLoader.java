package com.kimuchi1203.starlight;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class TwitterHomeTimelineLoader extends
		AsyncTaskLoader<ResponseList<twitter4j.Status>> {
	private Paging paging;
	private int id;
	private Twitter twitter;

	public TwitterHomeTimelineLoader(Context context, Paging paging2, int id2) {
		super(context);
		paging = paging2;
		id = id2;
		twitter = ((MainActivity) context).twitter;
	}

	@Override
	public ResponseList<Status> loadInBackground() {
		ResponseList<twitter4j.Status> home = null;
		if (MainActivity.LOADER_ID_HOME_TIMELINE == id) {
			if (null != paging) {
				try {
					home = twitter.getHomeTimeline(paging);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {
				try {
					home = twitter.getHomeTimeline();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		} else if (MainActivity.LOADER_ID_MENTION == id) {
			if (null != paging) {
				try {
					home = twitter.getMentions(paging);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			} else {
				try {
					home = twitter.getMentions();
				} catch (TwitterException e) {
					e.printStackTrace();
				}
			}
		}
		return home;
	}
}
