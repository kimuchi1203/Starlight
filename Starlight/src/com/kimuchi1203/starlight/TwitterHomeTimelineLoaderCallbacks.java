package com.kimuchi1203.starlight;

import twitter4j.Paging;
import twitter4j.ResponseList;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.Loader;
import android.widget.Toast;

public class TwitterHomeTimelineLoaderCallbacks implements
		LoaderCallbacks<ResponseList<twitter4j.Status>> {

	private TweetViewFragment parent;
	private Paging paging;
	private UserManager userManager;

	public TwitterHomeTimelineLoaderCallbacks(
			TweetViewFragment homeTimelineFragment, UserManager userManager2) {
		parent = homeTimelineFragment;
		userManager = userManager2;
		if (!userManager.adapterList.contains(parent.adapter)) {
			userManager.adapterList.add(parent.adapter);
		}
	}

	@Override
	public Loader<ResponseList<twitter4j.Status>> onCreateLoader(int arg0,
			Bundle arg1) {
		paging = (Paging) arg1.getSerializable("paging");
		TwitterHomeTimelineLoader loader = new TwitterHomeTimelineLoader(
				parent.getActivity(), paging, arg0);
		loader.forceLoad();
		return loader;
	}

	@Override
	public void onLoadFinished(Loader<ResponseList<twitter4j.Status>> arg0,
			ResponseList<twitter4j.Status> arg1) {
		ResponseList<twitter4j.Status> home = arg1;
		if ((null != home) && (home.size() > 0)) {
			userManager.loadIcon(home);
			if (null != paging) {
				long sinceId = paging.getSinceId();
				long maxId = paging.getMaxId();
				if ((-1 != sinceId) && (-1 != maxId)) {
					// [.. sinceId] <tailId ..home.. headId> [maxId+1 .. lastId]
					int cnt;
					for (cnt = 0; cnt < parent.adapter.getCount(); ++cnt) {
						twitter4j.Status st = parent.adapter.getItem(cnt);
						if (st.getId() < maxId + 1) {
							break;
						}
					}
					for (int i = 0; i < home.size(); ++i) {
						parent.adapter.insert(home.get(i), cnt + i);
					}
					// from sinceId to tailId
					Paging p2 = new Paging();
					p2.setSinceId(sinceId);
					p2.setMaxId(home.get(home.size() - 1).getId() - 1);
					parent.getTweets(p2);
				} else if (-1 != sinceId) {
					// [.. sinceId=lastId] <tailId ..home.. headId>
					for (int i = 0; i < home.size(); ++i) {
						parent.adapter.insert(home.get(i), i);
					}
					// update lastId <-- headId
					parent.lastId = home.get(0).getId();
					// from sinceId to tailId
					Paging p2 = new Paging();
					p2.setSinceId(sinceId);
					p2.setMaxId(home.get(home.size() - 1).getId() - 1);
					parent.getTweets(p2);
				} else {
					// <tailId .. home.. headId> [maxId+1 .. lastId]
					for (int i = 0; i < home.size(); ++i) {
						// Log.v("twitter", home.get(i).getText());
						parent.adapter.add(home.get(i));
					}
				}
			} else {
				// <tailId .. home.. headId>
				for (int i = 0; i < home.size(); ++i) {
					parent.adapter.add(home.get(i));
				}
				// update lastId <-- headId
				parent.lastId = home.get(0).getId();
			}
		}
		parent.adapter.notifyDataSetChanged();
		parent.hideHeader();
		parent.hideFooter();
		parent.loadingFlag = false;
	}

	@Override
	public void onLoaderReset(Loader<ResponseList<twitter4j.Status>> arg0) {
		if (null == parent.getActivity())
			return;
		parent.adapter.notifyDataSetChanged();
		parent.hideHeader();
		parent.hideFooter();
		parent.loadingFlag = false;
		Toast.makeText(parent.getActivity(), "stop loading", Toast.LENGTH_LONG)
				.show();
	}

}
