package com.kimuchi1203.starlight;

import twitter4j.Status;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListAdapter;
import android.widget.ListView;

public class TweetListView extends ListView {
	private int overScrollDistance = 80;
	private MainActivity parent;
	
	public TweetListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		parent = (MainActivity) context;
		setOverScrollMode(OVER_SCROLL_ALWAYS);
	}

	protected boolean overScrollBy(int deltaX, int deltaY,  
	                               int scrollX, int scrollY,  
	                               int scrollRangeX, int scrollRangeY,  
	                               int maxOverScrollX, int maxOverScrollY,  
	                               boolean isTouchEvent) {  
	  
	    return super.overScrollBy(deltaX, deltaY,  
	                              scrollX, scrollY,  
	                              scrollRangeX, scrollRangeY,  
	                              maxOverScrollX, overScrollDistance, isTouchEvent);  
	}
	
	protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX,
	        boolean clampedY) {
	 
	    if(-80==scrollY){
	    	parent.showHeader();
	    }else if(80==scrollY){
	    	ListAdapter adapter = this.getAdapter();
	    	int count = adapter.getCount();
	    	if(count>0){
	    		Status st = (Status) adapter.getItem(count-1);
	    		parent.showFooter(st.getId()-1);
	    	}
	    }
	 
	    super.onOverScrolled(scrollX, scrollY, clampedX, clampedY);
	 
	}
}
