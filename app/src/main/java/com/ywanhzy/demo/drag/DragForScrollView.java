package com.ywanhzy.demo.drag;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.ScrollView;

public class DragForScrollView extends ScrollView {
    private boolean isDrag;
	public DragForScrollView(Context context) {
        super(context);
    }
    public DragForScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    public DragForScrollView(Context context, AttributeSet attrs,
        int defStyle) {
        super(context, attrs, defStyle);
    }
    
    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
    	Log.e("isDrag",ev.getAction()+"");
 		if (ev.getAction() == MotionEvent.ACTION_DOWN||ev.getAction() ==MotionEvent.ACTION_UP||ev.getAction() ==MotionEvent.ACTION_MOVE) {
 			if (isDrag) {
 				return false;
 			} else {
 			}
 		}
        return super.onInterceptTouchEvent(ev);
    }
    public void startDrag(int position) {
		isDrag = true;
	}
    public void endDrag(int position) {
		isDrag = false;
	}
}

