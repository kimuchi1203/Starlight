package com.kimuchi1203.starlight;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

public class MainFragmentPagerAdapter extends FragmentStatePagerAdapter {

	public MainFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int position) {
		Fragment fragment = null;
		if (0 == position) {
			fragment = new HomeTimelineFragment();
		}
		return fragment;
	}

	@Override
	public int getCount() {
		return 1;
	}

}
