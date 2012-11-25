package com.kimuchi1203.starlight;

import twitter4j.Paging;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public abstract class TweetViewFragment extends Fragment {
	public TweetListAdapter adapter;
	public long lastId;
	public boolean loadingFlag;
	protected TwitterHomeTimelineLoaderCallbacks tweetLoaderCallbacks;
	protected int id;
	
	protected abstract void setId();
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_timeline_fragment, null);
		setId();
		lastId = 0;
		adapter = new TweetListAdapter(this.getActivity(), R.layout.tweet_list);
		adapter.setUserManager(((MainActivity) this.getActivity()).userManager);
		ListView tweet_list = (ListView) view.findViewById(R.id.home_list_view);
		tweet_list.setAdapter(adapter);

		if (((MainActivity) this.getActivity()).loadToken()) {
			getTweets(null);
		}
		return view;
	}

	public void getTweets() {
		if (0 != lastId) {
			Paging p = new Paging();
			p.setSinceId(lastId);
			getTweets(p);
		} else {
			getTweets(null);
		}
	}

	public void getTweets(final Paging p){
		loadingFlag = true;
		Bundle arg = new Bundle();
		arg.putSerializable("paging", p);
		if (null == getActivity()) { // TODO : make UserManager Singleton, then
										// remove this null check.
			return;
		}
		if (null == tweetLoaderCallbacks)
			tweetLoaderCallbacks = new TwitterHomeTimelineLoaderCallbacks(this,
					((MainActivity) this.getActivity()).userManager);
		this.getActivity()
				.getSupportLoaderManager()
				.restartLoader(id, arg,
						tweetLoaderCallbacks);
	}

	public TweetListAdapter getAdapter() {
		return adapter;
	}

	public void resetTweets() {
		adapter.clear();
		if (null == getActivity()) {
			return;
		}
		this.getActivity().getSupportLoaderManager()
				.destroyLoader(id);
	}

	public void showHeader() {
		// if task running, return
		if (loadingFlag) {
			return;
		}
		if (null == getActivity()) {
			return;
		}
		adapter.insert(((MainActivity) this.getActivity()).loadingStatus, 0);
		if (0 != lastId) {
			Paging p = new Paging();
			p.setSinceId(lastId);
			getTweets(p);
		} else {
			getTweets(null);
		}
	}

	public void hideHeader() {
		if (null == getActivity()) {
			return;
		}
		adapter.remove(((MainActivity) this.getActivity()).loadingStatus);
	}

	public void showFooter(long maxid) {
		// if task running, return
		if (loadingFlag) {
			return;
		}
		if (null == getActivity()) {
			return;
		}
		adapter.add(((MainActivity) this.getActivity()).loadingStatus);
		// / TODO: show last item hidden by footer
		Paging p = new Paging();
		p.setMaxId(maxid);
		getTweets(p);
	}

	public void hideFooter() {
		if (null == getActivity()) {
			return;
		}
		adapter.remove(((MainActivity) this.getActivity()).loadingStatus);
	}
}
