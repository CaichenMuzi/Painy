package com.gogostar.enstory;

/**
 * Created by Administrator on 2017/2/16.
 */

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.ScaleAnimation;
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
 * 绘本列表界面
 */
public class PictureListActivity extends BaseActivity {

	private ViewPager viewPager;
	private ViewPagerAdapter viewPagerAdapter;
	private List<View> viewList;

	private GridViewAdapter adapter1;
	private ArrayList<List<PictureInfo>> arrayList;

	private List<CategoryInfo> categoryInfoList;

	private ImageView img_back;

	private final String CATEGORY_URL = "http://123.57.60.247:802/PaintStory/api/GetCategoryList";
	private final String STORY_URL = "http://123.57.60.247:802/PaintStory/api/GetStoryList/";

	private HorizontalListView hListView;
	private HorizontalListViewAdapter hAdapter;

	private MyUtil myUtil = new MyUtil();
	private MediaPlayer mediaPlayer = new MediaPlayer();
	private AnimationSet animationSet;

	private Intent intent;

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
					hListView.setAdapter(hAdapter);
					hAdapter.notifyDataSetChanged();
					new Thread(new Runnable() {
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
					}).start();
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
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picture);
		new Thread(runnable).start();
		intent = new Intent(PictureListActivity.this, MusicServer.class);
		// 加载UI
		initUI();
	}

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
	 * 加载UI
	 */
	private void initUI() {

		viewPager = (ViewPager) findViewById(R.id.picture_viewpager);
		img_back = (ImageView) findViewById(R.id.picture_img_back);
		hListView = (HorizontalListView) findViewById(R.id.picture_horizontal);

		/**
		 * HorizontalListView点击事件
		 */
		hListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// 点击音效
				myUtil.playSound(PictureListActivity.this, R.raw.click);
				// 设置被选中的下标
				hAdapter.setSelectIndex(position);
				// 更新数据
				hAdapter.notifyDataSetChanged();
				// 根据position选择viewPager显示的view
				viewPager.setCurrentItem(position);
			}
		});

		/**
		 * ViewPager的滑动监听事件
		 */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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

		/**
		 * 返回按钮点击事件
		 **/
		img_back.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						img_back.setImageResource(R.drawable.fanhui_2);
						myUtil.playSound(PictureListActivity.this, R.raw.click);
						break;
					case MotionEvent.ACTION_UP:
						img_back.setImageResource(R.drawable.fanhui_1);
						finish();
						break;
				}
				return true;
			}
		});
	}

	/**
	 * 加载ViewPager
	 *
	 * @param list      分类信息集合
	 * @param arrayList 资源信息集合的集合
	 */
	public void initViewPager(List<CategoryInfo> list, final ArrayList<List<PictureInfo>>
			arrayList) {
		if (list != null) {
			viewList = new ArrayList<>();
			GridView gridView = null;
			// 根据分类信息集合的长度动态创建ViewPager的View
			for (int i = 0; i < list.size(); i++) {
				final List<PictureInfo> pictureInfos = arrayList.get(i);
				View view = LayoutInflater.from(this).inflate(R.layout.picture_view1, null);
				gridView = (GridView) view.findViewById(R.id.picture_gridView1);
				adapter1 = new GridViewAdapter(this, pictureInfos);
				gridView.setAdapter(adapter1);
				// GridView的点击事件
				gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long
							id) {
						// 获取点击的ImageView
						RelativeLayout relativeLayout = (RelativeLayout) ((LinearLayout) view)
								.getChildAt(0);
						ImageView imageView = (ImageView) relativeLayout.getChildAt(0);
						// 点击的ImageView的资源信息
						PictureInfo pictureInfo = pictureInfos.get(position);
						if (pictureInfo.getPrice() == 0) {  // 资源价格为0，打开播放画面
							stopService(intent);
							playMusic(R.raw.magic);
							animationSet = getAnimation(pictureInfo, imageView);
							imageView.startAnimation(animationSet);
						} else if (pictureInfo.getPrice() != 0) {
							// 资源价格不为0提示信息
							Toast toast = Toast.makeText(PictureListActivity.this, "当前课程未解锁！",
									Toast.LENGTH_SHORT);
							toast.setGravity(Gravity.CENTER, 0, 0);
							toast.show();
						}
					}
				});
				viewList.add(view);
			}
			viewPagerAdapter = new ViewPagerAdapter(viewList);
			viewPager.setAdapter(viewPagerAdapter);
			viewPager.setCurrentItem(0);
		}
	}

	/**
	 * 获取分类列表的子线程
	 */
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			List<CategoryInfo> list1 = getCategory();
			mHandler.obtainMessage(1, list1).sendToTarget();
		}
	};

	/**
	 * 获取分类信息列表
	 *
	 * @return
	 */
	private List<CategoryInfo> getCategory() {
		List<CategoryInfo> list = new ArrayList<CategoryInfo>();
		// 从分类接口获取返回的JSON
		JSONObject jsonObject = myUtil.sendByGet(CATEGORY_URL, null);
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString("Result");
				if (result.equals("True")) {
					// 如果有返回信息，从JSON中取出分类信息
					JSONArray contentJson = jsonObject.getJSONArray("Content");
					int length = contentJson.length();
					for (int i = 0; i < length; i++) {
						JSONObject jsonObject1 = contentJson.getJSONObject(i);
						int id = jsonObject1.getInt("ID");
						String name = jsonObject1.getString("Name");
						String description = jsonObject1.getString("Description");
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
	 * 获取PictureInfo列表
	 *
	 * @return
	 */
	private List<PictureInfo> getPictureList(int categoryId) {
		List<PictureInfo> list1 = new ArrayList<PictureInfo>();
		// 从资源接口获取返回的JSON
		JSONObject jsonObject = myUtil.sendByGet(STORY_URL, String.valueOf(categoryId));
		if (jsonObject != null) {
			try {
				String result = jsonObject.getString("Result");
				if (result.equals("True")) {
					// 如果有返回信息，从JSON中取出资源信息
					JSONArray jsonArray = jsonObject.getJSONArray("Content");
					for (int i = 0; i < jsonArray.length(); i++) {

						PictureInfo pictureInfo = new PictureInfo();
						JSONObject jsonObject1 = jsonArray.getJSONObject(i);

						int id = jsonObject1.getInt("ID");
						int cId = jsonObject1.getInt("CategoryID");
						String title = jsonObject1.getString("Title");
						final String imgUrl = jsonObject1.getString("ImgURL");
						String srcUrl = jsonObject1.getString("SrcURL");
						float price = jsonObject1.getLong("Price");
						int order = jsonObject1.getInt("Order");

						pictureInfo.setId(id);
						pictureInfo.setCategory_id(cId);
						pictureInfo.setTitle(title);
						pictureInfo.setVideo_img(imgUrl);
						pictureInfo.setVideo_src(srcUrl);
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

	/**
	 * 定义图片的动画
	 *
	 * @param imageView 需要添加动画的Image
	 * @return AnimationSet
	 */
	private AnimationSet getAnimation(final PictureInfo pictureInfo, ImageView imageView) {
		AnimationSet animationSet = new AnimationSet(false);
		// 以View中心点作为缩放中心，水平方向和垂直方向都缩小
		float fromXScale = 1.0f;
		float toScaleX = 0.0f;
		float fromYScale = 1.0f;
		float toScaleY = 0.0f;
		float pivotX = imageView.getWidth() / 2;
		float pivotY = imageView.getHeight() / 2;
		Animation animation = new ScaleAnimation(fromXScale, toScaleX, fromYScale,
				toScaleY, pivotX, pivotY);
		// 1.0表示完全不透明，0.0表示完全透明
		float fromAlpha = 1.0f;
		float toAlpha = 0.0f;
		// 1.0 => 0.0表示View从完全不透明渐变到完全透明
		// 设置动画集
		Animation animation1 = new AlphaAnimation(fromAlpha, toAlpha);
		animationSet.addAnimation(animation);
		animationSet.addAnimation(animation1);
		// 设置动画持续时间
		animationSet.setDuration(2000);
		// 动画执行完毕后是否停在结束时的角度上
		animationSet.setFillAfter(true);
		// 动画集的监听事件
		animationSet.setAnimationListener(new Animation.AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {

			}

			@Override
			public void onAnimationEnd(Animation animation) {
				// 动画结束后打开播放资源界面，并传递资源信息
				stopMediaPlayer();
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
		return animationSet;
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
