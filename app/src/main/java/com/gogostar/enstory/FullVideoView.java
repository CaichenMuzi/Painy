package com.gogostar.enstory;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/3/3.
 * <p>
 * 自定义全屏播放器
 */

public class FullVideoView extends VideoView {

	/**
	 * 构造函数
	 *
	 * @param context  当前页面
	 * @param attrs    属性
	 * @param defStyle 样式
	 */
	public FullVideoView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/**
	 * 构造函数
	 *
	 * @param context 当前页面
	 * @param attrs   属性
	 */
	public FullVideoView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	/**
	 * 构造函数
	 *
	 * @param context 当前页面
	 */
	public FullVideoView(Context context) {
		super(context);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = getDefaultSize(0, widthMeasureSpec);
		int height = getDefaultSize(0, heightMeasureSpec);
		setMeasuredDimension(width, height);
	}
}
