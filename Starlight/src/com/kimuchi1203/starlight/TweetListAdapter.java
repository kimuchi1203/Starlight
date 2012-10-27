package com.kimuchi1203.starlight;

import twitter4j.Status;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetListAdapter extends ArrayAdapter<Status> {
	int resourceId;

	public TweetListAdapter(Context context, int resourceId) {
		super(context, resourceId);
		this.resourceId = resourceId;
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final Status status = getItem(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resourceId, null);
		}
		//ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
		TextView text = (TextView) convertView.findViewById(R.id.text);
		text.setText(status.getText());
		return convertView;
	}
}
