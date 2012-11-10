package com.kimuchi1203.starlight;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import twitter4j.User;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.AsyncTaskLoader;

public class HttpDrawableLoader extends AsyncTaskLoader<Map<User, Drawable>> {

	private User user;
	
	public HttpDrawableLoader(Context context, User u) {
		super(context);
		user = u;
	}

	@Override
	public Map<User, Drawable> loadInBackground() {
		URL url = user.getProfileImageUrlHttps();
		Drawable d = loadImage(url);
		HashMap<User, Drawable> m = new HashMap<User, Drawable>();
		m.put(user, d);
		return m;
	}
	
	private Drawable loadImage(URL url) {
		Drawable d = null;
		try {
			HttpURLConnection http = (HttpURLConnection)url.openConnection();
			http.setRequestMethod("GET");
			http.connect();
			InputStream in = http.getInputStream();
			d = Drawable.createFromStream(in, "a");
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return d;
	}
}
