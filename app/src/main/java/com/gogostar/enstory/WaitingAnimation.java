package com.gogostar.enstory;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

public class WaitingAnimation extends BaseActivity {

	private ImageView imgView = null;
	private Animation animation = null;
	private TextView textView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_waiting_animation);

		imgView = (ImageView) findViewById(R.id.WaitingRoutate);
		textView = (TextView) findViewById(R.id.Description);

		animation = AnimationUtils.loadAnimation(this, R.anim.waiting);
		LinearInterpolator lin = new LinearInterpolator();
		animation.setInterpolator(lin);
	}

	@Override
	public void onResume() {
		super.onResume();

		if (animation != null) {
			Intent intent = getIntent();
			String desc = intent.getStringExtra("Description");

			textView.setText(desc);
			imgView.startAnimation(animation);
		}
	}

	@Override
	public void onPause() {
		super.onPause();

		if (animation != null) {
			imgView.clearAnimation();
		}
	}

	public static void Start(Context context, String description) {
		Intent intent = new Intent(context, WaitingAnimation.class);
		intent.putExtra("Description", description);
		context.startActivity(intent);
	}
}
