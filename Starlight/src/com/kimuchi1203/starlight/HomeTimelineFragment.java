package com.kimuchi1203.starlight;

import twitter4j.Paging;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

public class HomeTimelineFragment extends Fragment {
	public TweetListAdapter adapter;
	public long lastId;
	public boolean loadingFlag;
	private TwitterHomeTimelineLoaderCallbacks homeTimelineLoaderCallbacks;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.home_timeline_fragment, null);

		lastId = 0;
		adapter = new TweetListAdapter(this.getActivity(), R.layout.tweet_list);
		adapter.setUserManager(((MainActivity) this.getActivity()).userManager);
		ListView tweet_list = (ListView) view.findViewById(R.id.home_list_view);
		tweet_list.setAdapter(adapter);

		if (((MainActivity) this.getActivity()).loadToken()) {
			getHomeTimeline(null);
		}
		return view;
	}

	public void updateHomeTimeline() {
		if (0 != lastId) {
			Paging p = new Paging();
			p.setSinceId(lastId);
			getHomeTimeline(p);
		} else {
			getHomeTimeline(null);
		}
	}

	public void getHomeTimeline(final Paging p) {
		loadingFlag = true;
		Bundle arg = new Bundle();
		arg.putSerializable("paging", p);
		if (null == getActivity()) {
			return;
		}
		if (null == homeTimelineLoaderCallbacks)
			homeTimelineLoaderCallbacks = new TwitterHomeTimelineLoaderCallbacks(
					this, ((MainActivity) this.getActivity()).twitter,
					((MainActivity) this.getActivity()).userManager);
		this.getActivity()
				.getSupportLoaderManager()
				.restartLoader(MainActivity.LOADER_ID_HOME_TIMELINE, arg,
						homeTimelineLoaderCallbacks);
	}

	public TweetListAdapter getAdapter() {
		return adapter;
	}

	public void resetHomeTimeline() {
		adapter.clear();
		if (null == getActivity()) {
			return;
		}
		this.getActivity().getSupportLoaderManager()
				.destroyLoader(MainActivity.LOADER_ID_HOME_TIMELINE);
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
			getHomeTimeline(p);
		} else {
			getHomeTimeline(null);
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
		getHomeTimeline(p);
	}

	public void hideFooter() {
		if (null == getActivity()) {
			return;
		}
		adapter.remove(((MainActivity) this.getActivity()).loadingStatus);
	}

}
