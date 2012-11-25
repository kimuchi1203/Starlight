package com.kimuchi1203.starlight;

import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class TwitterAccessTokenLoaderCallbacks implements
		LoaderCallbacks<AccessToken> {

	private static final String KEY_TOKEN = "token";
	private static final String KEY_TOKEN_SECRET = "token_secret";

	private MainActivity parent;
	private RequestToken requestToken;
	private String verifier;

	public TwitterAccessTokenLoaderCallbacks(MainActivity mainActivity,
			RequestToken requestToken2, String verifier2) {
		parent = mainActivity;
		requestToken = requestToken2;
		verifier = verifier2;
	}

	@Override
	public Loader<AccessToken> onCreateLoader(int arg0, Bundle arg1) {
		TwitterAccessTokenLoader loader = new TwitterAccessTokenLoader(parent,
				requestToken, verifier);
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<AccessToken> arg0, AccessToken arg1) {
		AccessToken accessToken = arg1;
		// store to SharedPreferences
		// (/data/data/com.kimuchi1203.starlight/shared_prefs)
		SharedPreferences pref = parent.getPreferences(Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = pref.edit();
		editor.putString(KEY_TOKEN, accessToken.getToken());
		editor.putString(KEY_TOKEN_SECRET, accessToken.getTokenSecret());
		editor.commit();

		// reset all fragment.listview.adapter
		MainFragmentPagerAdapter adapter = (MainFragmentPagerAdapter) parent.pager
				.getAdapter();
		for (TweetViewFragment f : adapter.fragmentList) {
			f.resetTweets();
			f.getTweets(null);
		}
	}

	@Override
	public void onLoaderReset(Loader<AccessToken> arg0) {
		// TODO Auto-generated method stub

	}

}
