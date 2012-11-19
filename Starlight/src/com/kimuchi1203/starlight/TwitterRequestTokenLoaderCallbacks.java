package com.kimuchi1203.starlight;

import twitter4j.Twitter;
import twitter4j.auth.RequestToken;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;

public class TwitterRequestTokenLoaderCallbacks implements
		LoaderCallbacks<RequestToken> {

	private MainActivity parent;
	private Twitter twitter;
	private String callbackUrl;

	public TwitterRequestTokenLoaderCallbacks(MainActivity mainActivity,
			Twitter twitter2, String callbackUrl2) {
		parent = mainActivity;
		twitter = twitter2;
		callbackUrl = callbackUrl2;
	}

	@Override
	public Loader<RequestToken> onCreateLoader(int arg0, Bundle arg1) {
		TwitterRequestTokenLoader loader = new TwitterRequestTokenLoader(
				parent, twitter, callbackUrl);
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<RequestToken> arg0, RequestToken arg1) {
		RequestToken requestToken = arg1;
		parent.setRequestToken(requestToken);
		String url = requestToken.getAuthorizationURL();
		parent.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
	}

	@Override
	public void onLoaderReset(Loader<RequestToken> arg0) {
		// TODO Auto-generated method stub

	}

}
