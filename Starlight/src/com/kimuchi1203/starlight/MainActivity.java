package com.kimuchi1203.starlight;

import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.net.Uri;
import android.os.Bundle;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

public class MainActivity extends FragmentActivity {

	private static final String CALLBACK_URL = "callback://host";

	private static final String OAUTH_VERIFIER = "oauth_verifier";
	private static final String KEY_TOKEN = "token";
	private static final String KEY_TOKEN_SECRET = "token_secret";

	private static final int LOADER_ID_REQUEST_TOKEN = 0;
	private static final int LOADER_ID_ACCESS_TOKEN = 1;
	public static final int LOADER_ID_HOME_TIMELINE = 2;
	public static final int LOADER_ID_LOAD_ICON = 3;

	public Twitter twitter;
	private RequestToken requestToken;
	public VirtualStatus loadingStatus;
	public UserManager userManager;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		loadingStatus = new VirtualStatus(-1);

		userManager = new UserManager(this);

		MainFragmentPagerAdapter fAdapter = new MainFragmentPagerAdapter(
				this.getSupportFragmentManager());
		ViewPager pager = (ViewPager) this.findViewById(R.id.pager);
		pager.setAdapter(fAdapter);

		// start OAuth button
		Button btn = (Button) this.findViewById(R.id.login);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				twitter = new TwitterFactory().getInstance();
				twitter.setOAuthConsumer(ConsumerKey.CONSUMER_KEY,
						ConsumerKey.CONSUMER_SECRET);
				doOAuth();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void doOAuth() {
		getSupportLoaderManager().restartLoader(
				LOADER_ID_REQUEST_TOKEN,
				null,
				new TwitterRequestTokenLoaderCallbacks(this, twitter,
						CALLBACK_URL));
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
			getSupportLoaderManager().restartLoader(
					LOADER_ID_ACCESS_TOKEN,
					null,
					new TwitterAccessTokenLoaderCallbacks(this, twitter,
							requestToken, verifier));
		}
	}

	public boolean loadToken() {
		SharedPreferences pref = getPreferences(MODE_PRIVATE);
		String key = pref.getString(KEY_TOKEN, null);
		String secret = pref.getString(KEY_TOKEN_SECRET, null);
		if ((key != null) && (secret != null)) {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(ConsumerKey.CONSUMER_KEY,
					ConsumerKey.CONSUMER_SECRET);
			twitter.setOAuthAccessToken(new AccessToken(key, secret));
			return true;
		} else {
			return false;
		}
	}

	public HomeTimelineFragment getCurrentFragment() {
		ViewPager pager = (ViewPager) findViewById(R.id.pager);
		FragmentStatePagerAdapter sAdapter = (FragmentStatePagerAdapter) pager
				.getAdapter();
		Object f1 = sAdapter.instantiateItem(pager, pager.getCurrentItem());
		if (f1 instanceof HomeTimelineFragment) {
			return (HomeTimelineFragment) f1;
		} else {
			return null;
		}
	}
}
