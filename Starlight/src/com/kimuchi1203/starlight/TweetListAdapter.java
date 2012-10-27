package com.kimuchi1203.starlight;

import twitter4j.Status;
import twitter4j.User;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetListAdapter extends ArrayAdapter<Status> {
	int resourceId;
	UserManager userManager;

	public TweetListAdapter(Context context, int resourceId) {
		super(context, resourceId);
		this.resourceId = resourceId;
		userManager = new UserManager();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		final Status status = getItem(position);
		if (convertView == null) {
			LayoutInflater inflater = (LayoutInflater) getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(resourceId, null);
		}
		User user = status.getUser();
		Drawable d = userManager.getIcon(user);
		if(null!=d){
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
			icon.setImageDrawable(d);
		}
		TextView text = (TextView) convertView.findViewById(R.id.text);
		text.setText(status.getText());
		return convertView;
	}
}
