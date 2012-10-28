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
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;

public class MainActivity extends Activity {
	private static final String CONSUMER_KEY = "";
	private static final String CONSUMER_SECRET = "";
	private static final String CALLBACK_URL = "callback://host";
	private static final String OAUTH_VERIFIER = "oauth_verifier";
	private static final String KEY_TOKEN = "token";
	private static final String KEY_TOKEN_SECRET = "token_secret";

	private Twitter twitter;
	private RequestToken requestToken;
	private TweetListAdapter adapter;
	private long lastId;
	private AsyncTask<Void, Void, ResponseList<Status>> loadTask;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_CUSTOM_TITLE);
		setContentView(R.layout.activity_main);
		getWindow().setFeatureInt(Window.FEATURE_CUSTOM_TITLE,
				R.layout.custom_title);

		lastId = 0;
		loadTask = null;
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
			updateHomeTimeline();
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

				updateHomeTimeline();
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

	public void showHeader() {
		if ((loadTask != null)
				&& (loadTask.getStatus() == AsyncTask.Status.RUNNING)) {
			return;
		}
		View header = this.findViewById(R.id.update_progress);
		header.setVisibility(View.VISIBLE);
		updateHomeTimeline();
	}

	public void hideHeader() {
		View header = this.findViewById(R.id.update_progress);
		header.setVisibility(View.GONE);
	}

	public void showFooter(long maxid) {
		if ((loadTask != null)
				&& (loadTask.getStatus() == AsyncTask.Status.RUNNING)) {
			return;
		}
		View footer = this.findViewById(R.id.older_progress);
		footer.setVisibility(View.VISIBLE);
		/// TODO: show last item hidden by footer
		getOlderHomeTimeline(maxid);
	}
	
	public void hideFooter() {
		View footer = this.findViewById(R.id.older_progress);
		footer.setVisibility(View.GONE);
	}
	
	private void updateHomeTimeline() {
		loadTask = new AsyncTask<Void, Void, ResponseList<twitter4j.Status>>() {
			@Override
			protected ResponseList<twitter4j.Status> doInBackground(
					Void... params) {
				ResponseList<twitter4j.Status> home = null;
				try {
					if (0 != lastId) {
						Paging p = new Paging();
						p.setSinceId(lastId);
						home = twitter.getHomeTimeline(p);
					} else {
						home = twitter.getHomeTimeline();
					}
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				return home;
			}

			protected void onPostExecute(ResponseList<twitter4j.Status> home) {
				Log.v("twitter", "load complete");
				if ((null != home) && (home.size() > 0)) {
					for (int i = 0; i < home.size(); ++i) {
						Log.v("twitter", home.get(i).getText());
						adapter.insert(home.get(i), i);
					}
					lastId = home.get(0).getId();
					if (home.size() > 20) {
						// ŠÔ‚ª”²‚¯‚Ä‚¢‚é
					}
				}
				hideHeader();
			}

		}.execute();
	}

	public void getOlderHomeTimeline(final long id) {
		if (id <= 1) {
			return;
		}

		loadTask = new AsyncTask<Void, Void, ResponseList<twitter4j.Status>>() {
			@Override
			protected ResponseList<twitter4j.Status> doInBackground(
					Void... params) {
				ResponseList<twitter4j.Status> home = null;
				try {
					Paging p = new Paging();
					p.setMaxId(id - 1);
					home = twitter.getHomeTimeline(p);
				} catch (TwitterException e) {
					e.printStackTrace();
				}
				return home;
			}

			protected void onPostExecute(ResponseList<twitter4j.Status> home) {
				Log.v("twitter", "load complete");
				if ((null != home) && (home.size() > 0)) {
					for (int i = 0; i < home.size(); ++i) {
						Log.v("twitter", home.get(i).getText());
						adapter.add(home.get(i));
					}
				}
				hideFooter();
			}
		}.execute();
	}
}
