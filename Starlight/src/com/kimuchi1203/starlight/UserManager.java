package com.kimuchi1203.starlight;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;

import android.graphics.drawable.Drawable;
import twitter4j.User;

public class UserManager {
	HashMap<User, Drawable> userMap;
	
	public UserManager(){
		userMap = new HashMap<User, Drawable>();
	}
	
	public Drawable getIcon(User user){
		Drawable drawable;
		drawable = userMap.get(user);
		if(null==drawable){
			URL url = user.getProfileImageUrlHttps();
			drawable = loadImage(url);
			if(null!=drawable){
				userMap.put(user, drawable);
			}
		}
		return drawable;
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
