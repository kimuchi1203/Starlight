package com.kimuchi1203.starlight;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.widget.ImageView;
import twitter4j.User;

public class UserManager {
	HashMap<User, Drawable> userMap;
	
	public UserManager(){
		userMap = new HashMap<User, Drawable>();
	}
	
	public void getIcon(User user, final ImageView view){
		Drawable drawable = userMap.get(user);
		if(null!=drawable){
			view.setImageDrawable(drawable);
			return;
		}
		
		new AsyncTask<User, Void, Drawable>() {
			@Override
			protected Drawable doInBackground(User... params) {
				User user = params[0];
				URL url = user.getProfileImageUrlHttps();
				Drawable d = loadImage(url);
				if(null!=d){
					userMap.put(user, d);
				}
				return d;
			}
			protected void onPostExecute(Drawable result) {
				view.setImageDrawable(result);
			}
		}.execute(user);
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
