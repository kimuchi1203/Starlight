package com.kimuchi1203.starlight;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.RequestToken;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class TwitterRequestTokenLoader extends AsyncTaskLoader<RequestToken> {
	private Twitter twitter;
	private String callbackUrl;
	
	public TwitterRequestTokenLoader(Context context, Twitter twitter2, String callbackUrl2) {
		super(context);
		twitter = twitter2;
		callbackUrl = callbackUrl2;
	}

	@Override
	public RequestToken loadInBackground() {
		RequestToken requestToken = null;
		try {
			requestToken = twitter.getOAuthRequestToken(callbackUrl);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return requestToken;
	}

}
