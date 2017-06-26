package com.gogostar.gogostroydiy;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
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

public class MainActivity extends BaseActivity implements View.OnClickListener, View
		.OnTouchListener {

	private IntentFilter intentFilter;
	private MyBroadcastReceiver mBroadcastReceiver;
	private LocalBroadcastManager localBroadcastManager;
	private Intent intent;

	private ImageView mMoreImg, mCoverImg;
	private CircleImageView mHeaderImg;
	private TextView mUserNameTv, mCoinTv;

	//	private int mId, mLevel, mCoin;
//	private String mAccount, mPassword, mName, mContact, mNote, mRegisterTime, mLastAccess,
//			mImgHeader;
//	private boolean mValid;
	private Bitmap mBitmapHeader, mBitmapCover;

	private boolean isLogin = false, isFirstRun;

	private AnimationSet animationSet;
	private SharedPreferences pref;
	private UserInfo userInfo;

	private MyUtil myUtil = new MyUtil();

	private PictureInfo pictureInfo;

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					mHeaderImg.setImageBitmap(mBitmapHeader);
					break;
				case 2:
					pictureInfo = (PictureInfo) msg.obj;
					new Thread(new Runnable() {
						@Override
						public void run() {
							// 在子线程获取封面图片
							Bitmap bitmap = myUtil.getBitmap(pictureInfo.getPicture_img());
							mHandler.obtainMessage(3, bitmap).sendToTarget();
						}
					}).start();
					break;
				case 3:
					// 设置封面图片
					mBitmapCover = (Bitmap) msg.obj;
					mCoverImg.setImageBitmap(mBitmapCover);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		initView();

		new Thread(new Runnable() {
			@Override
			public void run() {
				// 在子线程获取封面资源信息
				PictureInfo pictureInfo = getFirstStory();
				mHandler.obtainMessage(2, pictureInfo).sendToTarget();
			}
		}).start();

		// 从本地获取账号信息
		pref = getSharedPreferences(Common.SP_USER_INFO, MODE_PRIVATE);
		// 是否第一次登录
		isFirstRun = pref.getBoolean(Common.IS_FIRST_RUN, true);
		if (!isFirstRun) {
			login();
		}
	}

	private void initView() {

		userInfo = new UserInfo();

		mHeaderImg = (CircleImageView) findViewById(R.id.iv_main_header);
		mMoreImg = (ImageView) findViewById(R.id.iv_main_more);
		mCoverImg = (ImageView) findViewById(R.id.iv_main_cover);

		mHeaderImg.setOnClickListener(this);
		mCoverImg.setOnClickListener(this);
		mMoreImg.setOnTouchListener(this);

		// 获取设备屏幕的高
		WindowManager wm = this.getWindowManager();
		int height = wm.getDefaultDisplay().getHeight();
		// 设置封面图片的宽高为设备屏幕高的五分之一
		ViewGroup.LayoutParams layoutParams = mCoverImg.getLayoutParams();
		layoutParams.width = height * 3 / 5;
		layoutParams.height = height * 3 / 5;
		mCoverImg.setLayoutParams(layoutParams);

		intent = new Intent(MainActivity.this, MusicServer.class);
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.iv_main_header: // 用户头像按钮点击事件
				myUtil.playSound(MainActivity.this, R.raw.click);
				if (isLogin) { // 如果已登录，打开用户信息列表
					Intent intent = new Intent(MainActivity.this, UserInfoActivity.class);
					startActivity(intent);
				} else if (!isLogin) { // 如果未登录，打开登录界面
					Intent intent = new Intent(MainActivity.this, LoginActivity.class);
					startActivity(intent);
				}
				break;
			case R.id.iv_main_cover: // 封面图片的点击事件
				// 停止播放背景音乐
				stopService(intent);
				// 播放特效音
				myUtil.playMusic(this, R.raw.magic);
				animationSet = myUtil.ImgAnimation(mCoverImg);
				// 通过View的startAnimation方法将动画立即应用到View上
				mCoverImg.startAnimation(animationSet);
				// 动画监听事件
				animationSet.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// 动画结束打开PictureStoryActivity页面
						Intent intent2 = new Intent(MainActivity.this, PictureStoryActivity.class);
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
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.iv_main_more:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(MainActivity.this, R.raw.click);
						mMoreImg.setImageResource(R.drawable.more_2);
						break;
					case MotionEvent.ACTION_UP:
						mMoreImg.setImageResource(R.drawable.more_1);
						Intent intent = new Intent(MainActivity.this, PictureListActivity.class);
						startActivity(intent);
						break;
				}
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
		intentFilter.addAction(Common.BROADCAST_STR);
		mBroadcastReceiver = new MyBroadcastReceiver();
		localBroadcastManager.registerReceiver(mBroadcastReceiver, intentFilter);
		if (isLogin) {
			// 如果已登录，调用登录方法
//			login();
//			// 更改UI界面
//			mHandler.post(new Runnable() {
//				@Override
//				public void run() {
//					tv_userName.setText(name);
//					tv_coin.setText(String.valueOf(coin));
//				}
//			});
		}
		startService(intent);
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
//		stopService(intent);
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
		JSONObject jsonObject = myUtil.sendByGet(Common.FIRST_STORY_URL, null);
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString(Common.RESULT);
				if (result.equals(Common.IS_RESULT)) {
					// 如果有返回数据，从json中取出封面资源信息
					JSONObject jsonObject1 = jsonObject.getJSONObject(Common.CONTENT);
					if (jsonObject1 != null) {
						int id = jsonObject1.getInt(Common.ID);
						int cId = jsonObject1.getInt(Common.CATEGORY_ID);
						String title = jsonObject1.getString(Common.TITLE);
						final String imgUrl = jsonObject1.getString(Common.IMG_URL);
						String srcUrl = jsonObject1.getString(Common.SRC_URL);
						float price = jsonObject1.getLong(Common.PRICE);
						int order = jsonObject1.getInt(Common.ORDER);

						// 封装资源信息
						pictureInfo.setId(id);
						pictureInfo.setCategory_id(cId);
						pictureInfo.setTitle(title);
						pictureInfo.setPicture_img(imgUrl);
						pictureInfo.setPicture_src(srcUrl);
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
	 * 定义登录方法
	 */
	public void login() {
		// 从本地获取账号信息
		SharedPreferences pref = getSharedPreferences("Login", MODE_PRIVATE);

		userInfo.setuId(pref.getInt(Common.ID, -1));
		userInfo.setuLevel(pref.getInt(Common.LEVEL, 0));
		userInfo.setuCoin(pref.getInt(Common.COIN, 0));
		userInfo.setuAccount(pref.getString(Common.ACCOUNT, ""));
		userInfo.setuPassword(pref.getString(Common.PASSWORD, ""));
		userInfo.setuName(pref.getString(Common.NAME, ""));
		userInfo.setuContact(pref.getString(Common.CONTACT, ""));
		userInfo.setuNote(pref.getString(Common.NOTE, ""));
		userInfo.setuRegisterTime(pref.getString(Common.REGISTER_TIME, ""));
		userInfo.setuLastAccess(pref.getString(Common.LAST_ACCESS, ""));
		userInfo.setuValid(pref.getBoolean(Common.VALID, false));
		userInfo.setuHeader(pref.getString(Common.HEAD_IMAGE, ""));
//		mId = pref.getInt("ID", -1);
//		mLevel = pref.getInt("Level", 0);
//		mCoin = pref.getInt("Coin", 0);
//		mAccount = pref.getString("Account", "");
//		mPassword = pref.getString("Password", "");
//		mName = pref.getString("Name", "");
//		mContact = pref.getString("Contact", "");
//		mNote = pref.getString("Note", "");
//		mRegisterTime = pref.getString("RegisterTime", "");
//		mLastAccess = pref.getString("LastAccess", "");
//		mValid = pref.getBoolean("Valid", false);
//		mImgHeader = pref.getString("ImgHeader", "");
		if (!userInfo.getuHeader().equals("")) {
			// 在子线程获取头像bitmap，并发送handler信息
			new Thread(new Runnable() {
				@Override
				public void run() {
					mBitmapHeader = myUtil.getBitmap(userInfo.getuHeader());
					Message message = new Message();
					message.what = 1;
					mHandler.sendMessage(message);
				}
			}).start();
		}
	}
}
