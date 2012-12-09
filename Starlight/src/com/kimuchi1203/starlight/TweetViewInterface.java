package com.kimuchi1203.starlight;

import twitter4j.Paging;

public interface TweetViewInterface {
	public void getTweets(final Paging p);

	public void resetTweets();

	public void showHeader();

	public void showFooter(long maxid);
}
