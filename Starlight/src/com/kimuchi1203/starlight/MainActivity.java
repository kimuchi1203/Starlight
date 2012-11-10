package com.kimuchi1203.starlight;

import twitter4j.Paging;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends FragmentActivity {

	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";
	private static final String CALLBACK_URL = "callback://host";

	private static final String OAUTH_VERIFIER = "oauth_verifier";
	private static final String KEY_TOKEN = "token";
	private static final String KEY_TOKEN_SECRET = "token_secret";

	private static final int LOADER_ID_REQUEST_TOKEN = 0;
	private static final int LOADER_ID_ACCESS_TOKEN = 1;
	private static final int LOADER_ID_HOME_TIMELINE = 2;
	
	private Twitter twitter;
	private RequestToken requestToken;
	public TweetListAdapter adapter;
	private long lastId;
	private boolean loadingFlag;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		twitter = new TwitterFactory().getInstance();
		twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);

		
		
		lastId = 0;
		loadingFlag = false;
		adapter = new TweetListAdapter(this, R.layout.tweet_list);
		ListView tweet_list = (ListView) this.findViewById(R.id.tweet_list);
		tweet_list.setAdapter(adapter);

		// start OAuth button
		Button btn = (Button) this.findViewById(R.id.login);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doOAuth();
			}
		});

		if (loadToken()) {
			getHomeTimeline(null);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void doOAuth() {
		getSupportLoaderManager().restartLoader(LOADER_ID_REQUEST_TOKEN, null, new TwitterRequestTokenLoaderCallbacks(this, twitter, CALLBACK_URL));
	}

	public void setRequestToken(RequestToken r) {
		requestToken = r;
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
			String verifier = uri.getQueryParameter(OAUTH_VERIFIER);
			getSupportLoaderManager().restartLoader(LOADER_ID_ACCESS_TOKEN, null, new TwitterAccessTokenLoaderCallbacks(this, twitter, requestToken, verifier));
		}
	}

	private boolean loadToken() {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		String key = pref.getString(KEY_TOKEN, null);
		String secret = pref.getString(KEY_TOKEN_SECRET, null);
		if ((key != null) && (secret != null)) {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			twitter.setOAuthAccessToken(new AccessToken(key, secret));
			return true;
		} else {
			return false;
		}
	}

	public void showHeader() {
		// if task running, return
		if(loadingFlag){
			return;
		}
		View header = this.findViewById(R.id.update_progress);
		header.setVisibility(View.VISIBLE);
		if(0!=lastId){
			Paging p = new Paging();
			p.setSinceId(lastId);
			getHomeTimeline(p);
		}else{
			getHomeTimeline(null);
		}
	}

	public void hideHeader() {
		View header = this.findViewById(R.id.update_progress);
		header.setVisibility(View.GONE);
	}

	public void showFooter(long maxid) {
		// if task running, return
		if(loadingFlag){
			return;
		}
		View footer = this.findViewById(R.id.older_progress);
		footer.setVisibility(View.VISIBLE);
		/// TODO: show last item hidden by footer
		Paging p = new Paging();
		p.setMaxId(maxid);
		getHomeTimeline(p);
	}
	
	public void hideFooter() {
		View footer = this.findViewById(R.id.older_progress);
		footer.setVisibility(View.GONE);
	}
	
	public void getHomeTimeline(final Paging p) {
		setLoadingFlag(true);
		getSupportLoaderManager().restartLoader(LOADER_ID_HOME_TIMELINE, null, new TwitterHomeTimelineLoaderCallbacks(this, twitter, p));
	}

	public void setLastId(long id) {
		lastId = id;
	}

	public void setLoadingFlag(boolean b) {
		loadingFlag = b;
	}

}
