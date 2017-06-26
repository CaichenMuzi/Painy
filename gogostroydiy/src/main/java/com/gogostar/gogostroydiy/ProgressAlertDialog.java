package com.gogostar.gogostroydiy;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

/**
 * Created by Administrator on 2017/4/10.
 * <p>
 * 自定义Dialog进度条
 */

public class ProgressAlertDialog extends AlertDialog {

	private ImageView progressImg;
	private Animation animation;

	/**
	 * 构造函数
	 *
	 * @param context 当前页面
	 */
	public ProgressAlertDialog(Context context) {
		super(context, R.style.MyDialog);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.progress_dialog_layout);

		// 点击页面其他部分无效
		setCanceledOnTouchOutside(false);

		progressImg = (ImageView) findViewById(R.id.refreshong_img);

		// 加载动画
		animation = AnimationUtils.loadAnimation(getContext(), R.anim.progress_rotate);

		// 保持动画结束时状态
		animation.setFillAfter(true);
	}

	@Override
	protected void onStart() {
		super.onStart();
		if (animation != null) {
			// 开始动画
			progressImg.startAnimation(animation);
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		// 清楚动画
		progressImg.clearAnimation();
	}
}
