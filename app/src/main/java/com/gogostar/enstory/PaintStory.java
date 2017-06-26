package com.gogostar.enstory;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 主界面
 */
public class PaintStory extends BaseActivity implements View.OnClickListener, View.OnTouchListener {

	private ImageView btn_more, img_cover;
	private CircleImageView img_head;
	private TextView tv_userName, tv_coin;

	private IntentFilter intentFilter;
	private MyBroadcastReceiver mBroadcastReceiver;
	private LocalBroadcastManager localBroadcastManager;

	private PictureInfo pictureInfo;

	private int id, level, coin;
	private String account, password, name, contact, note, registerTime, lastAccess, imgString;
	private boolean valid, isFirstRun;
	private Bitmap bitmap, bitmap2;

	// 封面资源接口
	private final String FirstStory_URL = "http://123.57.60.247:802/PaintStory/api/GetFirstStory";

	private boolean isLogin = false;

	private MyUtil myUtil = new MyUtil();

	private AnimationSet animationSet;

	private MediaPlayer mediaPlayer;

	private Intent intent;

	private SharedPreferences pref;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					img_head.setImageBitmap(bitmap);
					break;
				case 2:
					pictureInfo = (PictureInfo) msg.obj;
					new Thread(new Runnable() {
						@Override
						public void run() {
							// 在子线程获取封面图片
							Bitmap bitmap = myUtil.getBitmap(pictureInfo.getVideo_img());
							mHandler.obtainMessage(3, bitmap).sendToTarget();
						}
					}).start();
					break;
				case 3:
					// 设置封面图片
					bitmap2 = (Bitmap) msg.obj;
					img_cover.setImageBitmap(bitmap2);
					img_cover.setOnClickListener(PaintStory.this);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_story);
		// 获取设备屏幕的高
		WindowManager wm = this.getWindowManager();
		int height = wm.getDefaultDisplay().getHeight();

		mediaPlayer = myUtil.getMediaPlayer();

		tv_userName = (TextView) findViewById(R.id.story_user_name);
		tv_coin = (TextView) findViewById(R.id.story_coin_tv);

		img_head = (CircleImageView) findViewById(R.id.user_image);
		img_head.setOnClickListener(this);

		btn_more = (ImageView) findViewById(R.id.more_imageBtn);
		btn_more.setOnTouchListener(this);

		img_cover = (ImageView) findViewById(R.id.image_fengmian);
		//img_cover.setOnClickListener(this);

		// 设置封面图片的宽高为设备屏幕高的五分之一
		ViewGroup.LayoutParams layoutParams = img_cover.getLayoutParams();
		layoutParams.width = height * 3 / 5;
		layoutParams.height = height * 3 / 5;
		img_cover.setLayoutParams(layoutParams);
		intent = new Intent(PaintStory.this, MusicServer.class);
//		img_mo = (ImageView) findViewById(R.id.img_moerduo);
//		img_mo.setOnTouchListener(this);

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 在子线程获取封面资源信息
				PictureInfo pictureInfo = getFirstStory();
				mHandler.obtainMessage(2, pictureInfo).sendToTarget();
			}
		}).start();

		// 从本地获取账号信息
		pref = getSharedPreferences("Login", MODE_PRIVATE);
		// 是否第一次登录
		isFirstRun = pref.getBoolean("isFirstRun", true);
		if (!isFirstRun) {
			login();
		}
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.user_image: // 用户头像按钮点击事件
				myUtil.playSound(PaintStory.this, R.raw.click);
				if (!isLogin) { // 如果未登录，打开登录界面
					Intent intent1 = new Intent(PaintStory.this, LoginActivity.class);
					startActivity(intent1);
				}
				if (isLogin) { // 如果已登录，打开用户信息列表
					Intent intent = new Intent(PaintStory.this, UserInfoActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.image_fengmian:   // 封面图片的点击事件
				// 停止播放背景音乐
				stopService(intent);
				// 播放特效音
				playMusic(R.raw.magic);
				animationSet = myUtil.ImgAnimation(img_cover);
				// 通过View的startAnimation方法将动画立即应用到View上
				img_cover.startAnimation(animationSet);
				// 动画监听事件
				animationSet.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// 动画结束打开PictureStoryActivity页面
						stopMediaPlayer();
						Intent intent2 = new Intent(PaintStory.this, PictureStoryActivity.class);
						Bundle bundle = new Bundle();
						bundle.putParcelable("PictureInfo", pictureInfo);
						intent2.putExtra("bundle", bundle);
						startActivityForResult(intent2, 0);
						stopService(intent);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {
					}
				});
			default:
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.more_imageBtn:    // 更多列表按钮的Touch事件
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(PaintStory.this, R.raw.click);
						btn_more.setImageResource(R.drawable.more_2);
						break;
					case MotionEvent.ACTION_UP:
						btn_more.setImageResource(R.drawable.more_1);
						Intent intent = new Intent(PaintStory.this, PictureListActivity.class);
						startActivity(intent);
						break;
				}
				break;
//			case R.id.img_moerduo:
//				switch (event.getAction()) {
//					case MotionEvent.ACTION_DOWN:
//						img_mo.setImageResource(R.drawable.mo_1);
//						break;
//					case MotionEvent.ACTION_UP:
//						img_mo.setImageResource(R.drawable.mo_0);
//				}
//				break;
			default:
				break;
		}
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();

		// 注册本地广播
		localBroadcastManager = LocalBroadcastManager.getInstance(this);
		intentFilter = new IntentFilter();
		intentFilter.addAction("com.gogostar.enstory.LOCAL_BROADCAST");
		mBroadcastReceiver = new MyBroadcastReceiver();
		localBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
		if (isLogin) {
			// 如果已登录，调用登录方法
			login();
			// 更改UI界面
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					tv_userName.setText(name);
					tv_coin.setText(String.valueOf(coin));
				}
			});
		}
		startService(intent);
//		bindService(intent,)
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			switch (requestCode) {
				case 0:
					// 去除动画效果
					animationSet.setFillAfter(false);
					break;
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 解绑本地广播
		localBroadcastManager.unregisterReceiver(mBroadcastReceiver);
		// 停止背景音乐
		stopService(intent);
		stopMediaPlayer();
	}

	/**
	 * 定义登录方法
	 */
	public void login() {
		// 从本地获取账号信息
		SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);
		id = pref.getInt("ID", -1);
		level = pref.getInt("Level", 0);
		coin = pref.getInt("Coin", 0);
		account = pref.getString("Account", "");
		password = pref.getString("Password", "");
		name = pref.getString("Name", "");
		contact = pref.getString("Contact", "");
		note = pref.getString("Note", "");
		registerTime = pref.getString("RegisterTime", "");
		lastAccess = pref.getString("LastAccess", "");
		valid = pref.getBoolean("Valid", false);
		imgString = pref.getString("ImgHeader", "");
		if (!imgString.equals("")) {
			// 在子线程获取头像bitmap，并发送handler信息
			new Thread(new Runnable() {
				@Override
				public void run() {
					bitmap = myUtil.getBitmap(imgString);
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
			}).start();
		}
	}

	/**
	 * 自定义登录广播
	 */
	class MyBroadcastReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			// 从本地广播接收isLogin
			isLogin = intent.getBooleanExtra("isLogin", false);
			if (isLogin) {
				Toast.makeText(context, "登录成功", Toast.LENGTH_SHORT).show();
			}
		}
	}

	/**
	 * 获取封面故事
	 *
	 * @return
	 */
	private PictureInfo getFirstStory() {
		MyUtil myUtil = new MyUtil();
		PictureInfo pictureInfo = new PictureInfo();
		// 获取从接口返回的JSON数据
		JSONObject jsonObject = myUtil.sendByGet(FirstStory_URL, null);
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString("Result");
				if (result.equals("True")) {
					// 如果有返回数据，从json中取出封面资源信息
					JSONObject jsonObject1 = jsonObject.getJSONObject("Content");
					if (jsonObject1 != null) {
						int id = jsonObject1.getInt("ID");
						int cId = jsonObject1.getInt("CategoryID");
						String title = jsonObject1.getString("Title");
						final String imgUrl = jsonObject1.getString("ImgURL");
						String srcUrl = jsonObject1.getString("SrcURL");
						float price = jsonObject1.getLong("Price");
						int order = jsonObject1.getInt("Order");

						// 封装资源信息
						pictureInfo.setId(id);
						pictureInfo.setCategory_id(cId);
						pictureInfo.setTitle(title);
						pictureInfo.setVideo_img(imgUrl);
						pictureInfo.setVideo_src(srcUrl);
						pictureInfo.setPrice(price);
						pictureInfo.setOrder(order);
					}
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return pictureInfo;
	}

	/**
	 * 播放音效
	 *
	 * @param musicId 音效id
	 */
	private void playMusic(int musicId) {
		mediaPlayer = MediaPlayer.create(this, musicId);
		mediaPlayer.start();
	}

	/**
	 * 停止播放音频
	 */
	private void stopMediaPlayer() {
		if (mediaPlayer != null) {
			mediaPlayer.stop();
			mediaPlayer.release();// 释放
			mediaPlayer = null;
		}
	}
}
