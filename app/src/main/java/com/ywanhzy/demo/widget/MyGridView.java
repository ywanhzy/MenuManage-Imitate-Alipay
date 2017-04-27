package com.ywanhzy.demo.widget;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.widget.GridView;

public class MyGridView extends GridView
{
	public MyGridView(android.content.Context context,
			android.util.AttributeSet attrs)
	{
		super(context, attrs);
	}

	protected void dispatchDraw(Canvas canvas) {
		super.dispatchDraw(canvas);
		if (getChildAt(0) != null) {
			View localView1 = getChildAt(0);
			int column = getWidth() / localView1.getWidth();
			int childCount = getChildCount();
			int row = 0;
			if (childCount % column == 0) {
				row = childCount / column;
			} else {
				row = childCount / column + 1;
			}
			int endAllcolumn = (row - 1) * column;
			Paint localPaint, localPaint2;
			localPaint = new Paint();
			localPaint2 = new Paint();
			localPaint.setStyle(Paint.Style.STROKE);
			localPaint2.setStyle(Paint.Style.STROKE);
			localPaint.setStrokeWidth(1);
			localPaint2.setStrokeWidth(1);
			localPaint.setColor(Color.parseColor("#f0f0ed"));
			localPaint2.setColor(Color.parseColor("#f0f0ed"));
			for (int i = 0; i < childCount; i++) {
				View cellView = getChildAt(i);
				if ((i + 1) % column != 0) {
					canvas.drawLine(cellView.getRight(), cellView.getTop(),
							cellView.getRight(), cellView.getBottom(),
							localPaint);
					canvas.drawLine(cellView.getRight() + 1, cellView.getTop(),
							cellView.getRight() + 1, cellView.getBottom(),
							localPaint2);
				}
				if ((i + 1) <= endAllcolumn) {
					canvas.drawLine(cellView.getLeft(), cellView.getBottom(),
							cellView.getRight(), cellView.getBottom(),
							localPaint);
					canvas.drawLine(cellView.getLeft(),
							cellView.getBottom() + 1, cellView.getRight(),
							cellView.getBottom() + 1, localPaint2);
				}
			}
			if (childCount % column != 0) {
				for (int j = 0; j < (column - childCount % column); j++) {
					View lastView = getChildAt(childCount - 1);
					canvas.drawLine(lastView.getRight() + lastView.getWidth()
							* j, lastView.getTop(), lastView.getRight()
							+ lastView.getWidth() * j, lastView.getBottom(),
							localPaint);
					canvas.drawLine(lastView.getRight() + lastView.getWidth()
							* j + 1, lastView.getTop(), lastView.getRight()
							+ lastView.getWidth() * j + 1,
							lastView.getBottom(), localPaint2);
				}
			}
		}
	}
	
	/**
	 * 设置不滚动
	 */
	public void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
	{
		int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
				MeasureSpec.AT_MOST);
		super.onMeasure(widthMeasureSpec, expandSpec);

	}
}
