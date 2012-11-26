package com.kimuchi1203.starlight;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;

public class TwitterAccessTokenLoader extends AsyncTaskLoader<AccessToken> {
	private RequestToken requestToken;
	private String verifier;
	private Twitter twitter;

	public TwitterAccessTokenLoader(Context context,
			RequestToken requestToken2, String verifier2) {
		super(context);
		requestToken = requestToken2;
		verifier = verifier2;
		twitter = ((MainActivity) context).twitter;
	}

	@Override
	public AccessToken loadInBackground() {
		AccessToken accessToken = null;
		try {
			accessToken = twitter.getOAuthAccessToken(requestToken, verifier);
		} catch (TwitterException e) {
			e.printStackTrace();
		}
		return accessToken;
	}
}
