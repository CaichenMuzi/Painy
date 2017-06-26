package com.gogostar.enstory;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.content.LocalBroadcastManager;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/2/14.
 */

/**
 * 用户信息界面
 */
public class UserInfoActivity extends BaseActivity {

	// 定义常量
	private List<Profiles> list = new ArrayList<Profiles>();
	private ListViewAdapter adapter;
	private ListView listView;
	private CircleImageView imageButton;
	private ImageView btn_back;

	private Button btn_back_login;

	private SharedPreferences pref;
	private Bitmap bitmap;
	private String imgHeaderUrl;

	private LocalBroadcastManager localBroadcastManager;

	private MyUtil myUtil = new MyUtil();

	private Handler mHandler = new Handler() {
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					imageButton.setImageBitmap(bitmap);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_userinfo);

		initData();
		initView();
	}

	@Override
	public void onResume() {
		super.onResume();
		imgHeaderUrl = pref.getString("ImgHeader", "");

		new Thread(new Runnable() {
			@Override
			public void run() {
				Looper.prepare();
				bitmap = myUtil.getBitmap(imgHeaderUrl);
				Message message = new Message();
				message.what = 1;
				mHandler.sendMessage(message);
				Looper.loop();
			}
		}).start();
	}

	/**
	 * 加载数据
	 */
	private void initData() {
		Profiles profiles0 = new Profiles("账号", 0);
		Profiles profiles1 = new Profiles("昵称", 1);
		Profiles profiles2 = new Profiles("联系方式", 2);
		Profiles profiles3 = new Profiles("级别", 3);
		Profiles profiles4 = new Profiles("星星币", 4);
		list.add(profiles0);
		list.add(profiles1);
		list.add(profiles2);
		list.add(profiles3);
		list.add(profiles4);
	}

	/**
	 * 加载UI
	 */
	private void initView() {
		pref = getSharedPreferences("Login", MODE_PRIVATE);
		adapter = new ListViewAdapter(UserInfoActivity.this, R.layout
				.list_view_item, list);
		listView = (ListView) findViewById(R.id.user_list_view);
		listView.setAdapter(adapter);

		// 头像按钮事件，打开修改账号信息页面
		imageButton = (CircleImageView) findViewById(R.id.userinfo_head_image);
		imageButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(UserInfoActivity.this, ModifyActivity.class);
				startActivity(intent);
			}
		});

		// listView的点击事件
		listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (list.get(position).getName().equals("星星币")) {
					Intent intent = new Intent(UserInfoActivity.this, GoMoneyActivity.class);
					startActivity(intent);
				}
			}
		});

		// 退出登录按钮点击事件
		btn_back_login = (Button) findViewById(R.id.back_login);
		btn_back_login.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				localBroadcastManager = LocalBroadcastManager.getInstance(UserInfoActivity.this);
				Intent intent = new Intent("com.gogostar.enstory.LOCAL_BROADCAST");
				intent.putExtra("isLogin", false);
				localBroadcastManager.sendBroadcast(intent);
				ActivityCollector.finishAll();
				Intent i = new Intent(UserInfoActivity.this, PaintStory.class);
				startActivity(i);
			}
		});

		// 返回按钮点击事件
		btn_back = (ImageView) findViewById(R.id.userInfo_back);
		btn_back.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(UserInfoActivity.this, R.raw.click);
						btn_back.setImageResource(R.drawable.fanhui_2);
						break;
					case MotionEvent.ACTION_UP:
						btn_back.setImageResource(R.drawable.fanhui_1);
						finish();
						break;
				}
				return true;
			}
		});
	}
}
