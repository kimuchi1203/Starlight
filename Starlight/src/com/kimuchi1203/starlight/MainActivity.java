package com.kimuchi1203.starlight;

import twitter4j.Paging;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
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
	public static final int LOADER_ID_LOAD_ICON = 3;
	
	private Twitter twitter;
	private RequestToken requestToken;
	public TweetListAdapter adapter;
	private long lastId;
	private boolean loadingFlag;
	private VirtualStatus loadingStatus;
	private TwitterHomeTimelineLoaderCallbacks homeTimelineLoaderCallbacks;
	private UserManager userManager;
	
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
		loadingStatus = new VirtualStatus(-1);
		
		adapter = new TweetListAdapter(this, R.layout.tweet_list);
		ListView tweet_list = (ListView) this.findViewById(R.id.tweet_list);
		tweet_list.setAdapter(adapter);

		userManager = new UserManager(this, adapter);
		adapter.setUserManager(userManager);
		homeTimelineLoaderCallbacks = null;

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
		adapter.insert(loadingStatus, 0);
		if(0!=lastId){
			Paging p = new Paging();
			p.setSinceId(lastId);
			getHomeTimeline(p);
		}else{
			getHomeTimeline(null);
		}
	}

	public void hideHeader() {
		adapter.remove(loadingStatus);
	}

	public void showFooter(long maxid) {
		// if task running, return
		if(loadingFlag){
			return;
		}
		adapter.add(loadingStatus);
		/// TODO: show last item hidden by footer
		Paging p = new Paging();
		p.setMaxId(maxid);
		getHomeTimeline(p);
	}
	
	public void hideFooter() {
		adapter.remove(loadingStatus);
	}
	
	public void getHomeTimeline(final Paging p) {
		setLoadingFlag(true);
		Bundle arg = new Bundle();
		arg.putSerializable("paging", p);
		if(null==homeTimelineLoaderCallbacks) homeTimelineLoaderCallbacks = new TwitterHomeTimelineLoaderCallbacks(this, twitter, userManager);
		getSupportLoaderManager().restartLoader(LOADER_ID_HOME_TIMELINE, arg, homeTimelineLoaderCallbacks);
	}

	public void setLastId(long id) {
		lastId = id;
	}

	public void setLoadingFlag(boolean b) {
		loadingFlag = b;
	}

}
