package com.kimuchi1203.starlight;

import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";
	private static final String CALLBACK_URL = "callback://host";
	private static final String OAUTH_VERIFIER = "oauth_verifier";
	private static final String KEY_TOKEN = "token";
	private static final String KEY_TOKEN_SECRET = "token_secret";

	private Twitter twitter;
	private RequestToken requestToken;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE, R.layout.custom_title);
		
		

		// start OAuth button
		Button btn = (Button) this.findViewById(R.id.login);
		btn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				doOAuth();
			}
		});

		if (loadToken()) {
			showHomeTimeline();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}

	private void doOAuth() {
		try {
			twitter = new TwitterFactory().getInstance();
			twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
			requestToken = twitter.getOAuthRequestToken(CALLBACK_URL);
			String url = requestToken.getAuthorizationURL();
			this.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url)));
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		Uri uri = intent.getData();
		if (uri != null && uri.toString().startsWith(CALLBACK_URL)) {
			try {
				String verifier = uri.getQueryParameter(OAUTH_VERIFIER);
				AccessToken accessToken = twitter.getOAuthAccessToken(
						requestToken, verifier);

				// store to SharedPreferences
				// (/data/data/com.kimuchi1203.starlight/shared_prefs)
				SharedPreferences pref = getPreferences(MODE_PRIVATE);
				SharedPreferences.Editor editor = pref.edit();
				editor.putString(KEY_TOKEN, accessToken.getToken());
				editor.putString(KEY_TOKEN_SECRET, accessToken.getTokenSecret());
				editor.commit();

				showHomeTimeline();
			} catch (TwitterException e) {
				android.util.Log.e("TwitterException", e.toString());
			}
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

	private void showHomeTimeline() {
		try {
			ResponseList<Status> home = twitter.getHomeTimeline();
			LinearLayout tweet_list = (LinearLayout) this
					.findViewById(R.id.tweet_list);
			for (int i = 0; i < home.size(); ++i) {
				tweet_list.addView(createTweetView(home.get(i)));
			}
		} catch (TwitterException e) {
			e.printStackTrace();
		}

	}

	private View createTweetView(Status status) {
		LinearLayout tweet = new LinearLayout(this);
		tweet.setOrientation(LinearLayout.HORIZONTAL);

		TextView text = new TextView(this);
		text.setText(status.getText());
		tweet.addView(text);
		return tweet;
	}
}
