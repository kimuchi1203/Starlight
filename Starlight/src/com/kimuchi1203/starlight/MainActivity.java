package com.kimuchi1203.starlight;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MainActivity extends Activity {
	private static final String CONSUMER_KEY = null;
	private static final String CONSUMER_SECRET = null;
	private static final String CALLBACK_URL = "callback://host";
	private static final String OAUTH_VERIFIER = "oauth_verifier";
	
	private Twitter twitter;
	private RequestToken requestToken;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        twitter = new TwitterFactory().getInstance();
        twitter.setOAuthConsumer(CONSUMER_KEY, CONSUMER_SECRET);
        
        // start OAuth button
        Button btn = (Button) this.findViewById(R.id.login);
        btn.setOnClickListener(new OnClickListener(){
        	public void onClick(View v){
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
		try {
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

				Log.v("twitter", "token " + accessToken.getToken() + " secret "
						+ accessToken.getTokenSecret());
			} catch (TwitterException e) {
				android.util.Log.e("TwitterException", e.toString());
			}
		}
	}

}
