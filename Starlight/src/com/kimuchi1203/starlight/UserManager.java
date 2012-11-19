package com.kimuchi1203.starlight;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.ImageView;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.User;

public class UserManager implements LoaderCallbacks<Map<User, Drawable>> {
	private HashMap<User, Drawable> userMap;
	private MainActivity parent;
	ArrayList<User> userQueue;

	public UserManager(MainActivity mainActivity) {
		userMap = new HashMap<User, Drawable>();
		parent = mainActivity;
		userQueue = new ArrayList<User>();
	}

	public void getIcon(User user, final ImageView view) {
		Drawable drawable = userMap.get(user);
		view.setImageDrawable(drawable);
	}

	public void loadIcon(ResponseList<Status> home) {
		for (Status st : home) {
			User u = st.getUser();
			if ((null == userMap.get(u)) && (userQueue.indexOf(u) == -1)) {
				userQueue.add(u);
			}
		}
		for (User u : userQueue) {
			Bundle arg = new Bundle();
			arg.putSerializable("user", u);
			parent.getSupportLoaderManager().restartLoader(
					MainActivity.LOADER_ID_LOAD_ICON + u.hashCode(), arg, this);
		}

	}

	@Override
	public Loader<Map<User, Drawable>> onCreateLoader(int arg0, Bundle arg1) {
		User u = (User) arg1.getSerializable("user");
		HttpDrawableLoader loader = new HttpDrawableLoader(parent, u);
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<Map<User, Drawable>> arg0,
			Map<User, Drawable> arg1) {
		for (User u : arg1.keySet()) {
			userQueue.remove(u);
		}
		userMap.putAll(arg1);
		if (userQueue.size() == 0) {
			parent.getCurrentAdapter().notifyDataSetChanged();
		}
	}

	@Override
	public void onLoaderReset(Loader<Map<User, Drawable>> arg0) {
		// TODO Auto-generated method stub

	}
}
