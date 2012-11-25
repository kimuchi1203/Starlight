package com.kimuchi1203.starlight;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFragmentPagerAdapter extends FragmentStatePagerAdapter {
	public ArrayList<TweetViewFragment> fragmentList;

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		fragmentList = new ArrayList<TweetViewFragment>();
		fragmentList.add(new HomeTimelineFragment());
		fragmentList.add(new MentionFragment());
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		if (position < fragmentList.size()) {
			fragment = fragmentList.get(position);
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return fragmentList.size();
	}
}
