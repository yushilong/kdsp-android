package com.qizhu.rili.widget;

import android.content.Context;
import android.graphics.PointF;
import androidx.viewpager.widget.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ChildViewPager extends ViewPager {
	/** 触摸时按下的点 **/
	PointF downP = new PointF();
	/** 触摸时当前的点 **/
	PointF curP = new PointF();
	OnSingleTouchListener onSingleTouchListener;

	public ChildViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChildViewPager(Context context) {
		super(context);
	}

	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
//		getParent().requestDisallowInterceptTouchEvent(true); // 只需这句话，让父类不拦截触摸事件就可以了。
		return super.dispatchTouchEvent(ev);
	}
	


	/**
	 * 单击
	 */
	public void onSingleTouch() {
		if (onSingleTouchListener != null) {
			onSingleTouchListener.onSingleTouch();
		}
	}

	public void onTouchDown() {
		if (onSingleTouchListener != null) {
			onSingleTouchListener.onTouchDown();
		}
	}

	public void onTouchUp() {
		if (onSingleTouchListener != null) {
			onSingleTouchListener.onTouchUp();
		}
	}

	/**
	 * 创建点击事件接口
	 */
	public interface OnSingleTouchListener {
		public void onSingleTouch();

		public void onTouchDown();

		public void onTouchUp();
	}

	public void setOnSingleTouchListener(
			OnSingleTouchListener onSingleTouchListener) {
		this.onSingleTouchListener = onSingleTouchListener;
	}

}
