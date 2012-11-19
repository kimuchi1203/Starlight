package com.kimuchi1203.starlight;

import twitter4j.Status;
import twitter4j.User;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class TweetListAdapter extends ArrayAdapter<Status> {
	private int resourceId;
	private UserManager userManager;
	private LayoutInflater inflater;

	static class ViewHolder {
		TextView screenName;
		TextView text;
		ImageView icon;
	}

	public TweetListAdapter(Context context, int resourceId) {
		super(context, resourceId);
		this.resourceId = resourceId;
		inflater = (LayoutInflater) getContext().getSystemService(
				Context.LAYOUT_INFLATER_SERVICE);

	}

	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = inflater.inflate(resourceId, null);
			TextView text = (TextView) convertView.findViewById(R.id.text);
			ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
			holder = new ViewHolder();
			holder.text = text;
			holder.icon = icon;
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		final Status status = getItem(position);
		if (status instanceof VirtualStatus) {
			holder.icon.setImageDrawable(null);
			holder.text.setText("...now loading...");
		} else {
			User user = status.getUser();
			userManager.getIcon(user, holder.icon);
			holder.text.setText(status.getText());
		}
		return convertView;
	}

	public void setUserManager(UserManager userManager2) {
		userManager = userManager2;
	}
}
