package com.gogostar.gogostroydiy;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/4/24.
 */

public class PictureListActivity extends BaseActivity {

	private MyUtil myUtil = new MyUtil();
	private Intent intent;
	private AnimationSet animationSet;

	private HorizontalListView mHorizontal;
	private ViewPager mViewPager;
	private ImageView mBackImg;

	private List<CategoryInfo> categoryInfoList;
	private List<PictureInfo> pictureInfoList;
	private ArrayList<List<PictureInfo>> arrayList;
	private List<View> viewList;

	private HorizontalListViewAdapter hAdapter;
	private GridViewAdapter gAdapter;

	private Handler mHandler = new Handler() {
		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
				case 1:
					// 获取分类信息
					categoryInfoList = (List<CategoryInfo>) msg.obj;
					// 分类信息适配器
					hAdapter = new HorizontalListViewAdapter(PictureListActivity.this,
							categoryInfoList);
					mHorizontal.setAdapter(hAdapter);
					hAdapter.notifyDataSetChanged();
					new Thread(runnable2).start();
					break;
				case 2:
					// 获取资源信息集合的集合
					arrayList = (ArrayList<List<PictureInfo>>) msg.obj;
					// 加载布局
					initViewPager(categoryInfoList, arrayList);
					break;
			}
		}
	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picturelist);
		initData();
	}

	/**
	 * 加载View
	 */
	private void initData() {

		new Thread(runnable1).start();
		intent = new Intent(PictureListActivity.this, MusicServer.class);

		mHorizontal = (HorizontalListView) findViewById(R.id.hlv_pictureList_category);
		mViewPager = (ViewPager) findViewById(R.id.vp_pictureList_picture);
		mBackImg = (ImageView) findViewById(R.id.iv_pictureList_back);

		mBackImg.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mBackImg.setImageResource(R.drawable.fanhui_2);
						myUtil.playSound(PictureListActivity.this, R.raw.click);
						break;
					case MotionEvent.ACTION_UP:
						mBackImg.setImageResource(R.drawable.fanhui_1);
						finish();
						break;
				}
				return true;
			}
		});

		mHorizontal.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击音效
				myUtil.playSound(PictureListActivity.this, R.raw.click);
				// 设置被选中的下标
				hAdapter.setSelectIndex(position);
				// 更新数据
				hAdapter.notifyDataSetChanged();
				// 根据position选择viewPager显示的view
				mViewPager.setCurrentItem(position);
			}
		});

		mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
			@Override
			public void onPageScrolled(int position, float positionOffset, int
					positionOffsetPixels) {

			}

			@Override
			public void onPageSelected(int position) {
				// 设置被选中的下标
				hAdapter.setSelectIndex(position);
				hAdapter.notifyDataSetChanged();
			}

			@Override
			public void onPageScrollStateChanged(int state) {

			}
		});
	}

	private void initViewPager(List<CategoryInfo> categoryInfoList, ArrayList<List<PictureInfo>>
			arrayList) {
		if (categoryInfoList != null) {
			viewList = new ArrayList<View>();
			GridView gridView = null;
			for (int i = 0; i < categoryInfoList.size(); i++) {
				final List<PictureInfo> pictureInfoList = arrayList.get(i);
				View view = LayoutInflater.from(this).inflate(R.layout.viewpager_item, null);
				gridView = (GridView) view.findViewById(R.id.gv_viewpager_item);
				gAdapter = new GridViewAdapter(this, pictureInfoList);
				gridView.setAdapter(gAdapter);
				gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long
							id) {
						RelativeLayout relativeLayout = (RelativeLayout) ((LinearLayout) view)
								.getChildAt(0);
						ImageView imageView = (ImageView) relativeLayout.getChildAt(0);
						final PictureInfo pictureInfo = pictureInfoList.get(position);
						if (pictureInfo.getPrice() == 0) {  // 资源价格为0，打开播放画面
							stopService(intent);
							myUtil.playMusic(PictureListActivity.this, R.raw.magic);
							animationSet = myUtil.getAnimation(imageView);
							imageView.startAnimation(animationSet);
							// 动画集的监听事件
							animationSet.setAnimationListener(new Animation.AnimationListener() {
								@Override
								public void onAnimationStart(Animation animation) {

								}

								@Override
								public void onAnimationEnd(Animation animation) {
									// 动画结束后打开播放资源界面，并传递资源信息
									Intent intent = new Intent(PictureListActivity.this,
											PictureStoryActivity.class);
									Bundle bundle = new Bundle();
									bundle.putParcelable("PictureInfo", pictureInfo);
									intent.putExtra("bundle", bundle);
									startActivityForResult(intent, 0);
								}

								@Override
								public void onAnimationRepeat(Animation animation) {

								}
							});
						} else if (pictureInfo.getPrice() != 0) {
							// 资源价格不为0提示信息
							Toast toast = Toast.makeText(PictureListActivity.this, "当前课程未解锁！",
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
				});
			}
		}
	}

	/**
	 * 获取分类列表的子线程
	 */
	Runnable runnable1 = new Runnable() {
		@Override
		public void run() {
			List<CategoryInfo> list1 = getCategoryList();
			mHandler.obtainMessage(1, list1).sendToTarget();
		}
	};

	Runnable runnable2 = new Runnable() {
		@Override
		public void run() {
			ArrayList<List<PictureInfo>> arrayList1 = new
					ArrayList<List<PictureInfo>>();
			// 获取每个分类信息下的资源信息，并添加到arrayList1
			for (int i = 0; i < categoryInfoList.size(); i++) {
				List<PictureInfo> list = getPictureList(categoryInfoList.get(i)
						.getId());
				arrayList1.add(list);
			}
			mHandler.obtainMessage(2, arrayList1).sendToTarget();
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		// 播放背景音乐
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

	/**
	 * 获取分类信息列表
	 *
	 * @return
	 */
	private List<CategoryInfo> getCategoryList() {
		List<CategoryInfo> list = new ArrayList<CategoryInfo>();
		// 从分类接口获取返回的JSON
		JSONObject jsonObject = myUtil.sendByGet(Common.CATEGORY_URL, null);
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString(Common.RESULT);
				if (result.equals(Common.IS_RESULT)) {
					// 如果有返回信息，从JSON中取出分类信息
					JSONArray contentJson = jsonObject.getJSONArray(Common.CONTENT);
					int length = contentJson.length();
					for (int i = 0; i < length; i++) {
						JSONObject jsonObject1 = contentJson.getJSONObject(i);
						int id = jsonObject1.getInt(Common.ID);
						String name = jsonObject1.getString(Common.NAME);
						String description = jsonObject1.getString(Common.DESCRIPTION);
						CategoryInfo categoryInfo = new CategoryInfo(id, name, description);
						list.add(categoryInfo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	/**
	 * 获取Picture信息列表
	 *
	 * @param categoryId 分类ID
	 * @return
	 */
	public List<PictureInfo> getPictureList(int categoryId) {
		List<PictureInfo> list1 = new ArrayList<PictureInfo>();
		// 从资源接口获取返回的JSON
		JSONObject jsonObject = myUtil.sendByGet(Common.PICTURE_URL, String.valueOf(categoryId));
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString(Common.RESULT);
				if (result.equals(Common.IS_RESULT)) {
					// 如果有返回信息，从JSON中取出资源信息
					JSONArray jsonArray = jsonObject.getJSONArray(Common.CONTENT);
					for (int i = 0; i < jsonArray.length(); i++) {

						PictureInfo pictureInfo = new PictureInfo();
						JSONObject jsonObject1 = jsonArray.getJSONObject(i);

						int id = jsonObject1.getInt(Common.ID);
						int cId = jsonObject1.getInt(Common.CATEGORY_ID);
						String title = jsonObject1.getString(Common.TITLE);
						final String imgUrl = jsonObject1.getString(Common.IMG_URL);
						String srcUrl = jsonObject1.getString(Common.SRC_URL);
						float price = jsonObject1.getLong(Common.PRICE);
						int order = jsonObject1.getInt(Common.ORDER);

						pictureInfo.setId(id);
						pictureInfo.setCategory_id(cId);
						pictureInfo.setTitle(title);
						pictureInfo.setPicture_img(imgUrl);
						pictureInfo.setPicture_src(srcUrl);
						pictureInfo.setPrice(price);
						pictureInfo.setOrder(order);

						list1.add(pictureInfo);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return list1;
	}
}
