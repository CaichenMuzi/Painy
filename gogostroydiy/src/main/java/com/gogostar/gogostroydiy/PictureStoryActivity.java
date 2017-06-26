package com.gogostar.gogostroydiy;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.File;

/**
 * Created by Administrator on 2017/4/25.
 */

public class PictureStoryActivity extends BaseActivity implements View.OnTouchListener {

	private Bundle bundle;
	private PictureInfo pictureInfo;
	private MyUtil myUtil;
	private File file;
	private File[] files;

	private ImageView mBackImg, mMakeVideoImg, mPlayImg;
	private GridView mGridView;

	private AnimationSet animationSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_picturestory);

		initData();
	}

	/**
	 * 加载数据
	 */
	private void initData() {
		myUtil = new MyUtil();
		bundle = (Bundle) getIntent().getExtras().get("bundle");
		pictureInfo = (PictureInfo) bundle.getParcelable("PictureInfo");

		mBackImg = (ImageView) findViewById(R.id.iv_pictureStory_back);
		mMakeVideoImg = (ImageView) findViewById(R.id.iv_pictureStory_makeVideo);
		mPlayImg = (ImageView) findViewById(R.id.iv_pictureStory_playVideo);
		mGridView = (GridView) findViewById(R.id.gv_picture_story);

		mBackImg.setOnTouchListener(this);
		mMakeVideoImg.setOnTouchListener(this);
		mPlayImg.setOnTouchListener(this);

		// 下载资源zip
		file = new File(getCacheDir().toString() + Common.PATH + File.separator + pictureInfo
				.getTitle().toLowerCase().replace(" ", ""));
		if (!file.exists()) {
			doDownloadWork(pictureInfo.getPicture_src(), getCacheDir().toString() + Common.PATH);
		} else {
			// 资源已存在，播放视频
			initGridView();
		}
	}

	@Override
	public boolean onTouch(View view, MotionEvent event) {
		switch (view.getId()) {
			case R.id.iv_pictureStory_makeVideo:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(this, R.raw.click);
						mMakeVideoImg.setImageResource(R.drawable.hcsp_1);
						break;
					case MotionEvent.ACTION_UP:
						mMakeVideoImg.setImageResource(R.drawable.hcsp_0);
						break;
				}
				break;
			case R.id.iv_pictureStory_playVideo:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(this, R.raw.click);
						mPlayImg.setImageResource(R.drawable.bfsp_1);
						break;
					case MotionEvent.ACTION_UP:
						mPlayImg.setImageResource(R.drawable.bfsp_0);
						break;
				}
				break;
			case R.id.iv_pictureStory_back:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(this, R.raw.click);
						mBackImg.setImageResource(R.drawable.fanhui_2);
						break;
					case MotionEvent.ACTION_UP:
						mBackImg.setImageResource(R.drawable.fanhui_1);
						setResult(0);
						finish();
						break;
				}
				break;
		}
		return true;
	}

	public void initGridView() {

		files = file.listFiles();
		MyGridViewAdapter gAdapter = new MyGridViewAdapter();
		mGridView.setAdapter(gAdapter);
		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, final int position, long
					id) {
				myUtil.playMusic(PictureStoryActivity.this, R.raw.magic);
				ImageView image = (ImageView) ((LinearLayout) view).getChildAt(0);
				animationSet = myUtil.getAnimation(image);
				image.setAnimation(animationSet);
				// 动画集的监听事件
				animationSet.setAnimationListener(new Animation.AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {

					}

					@Override
					public void onAnimationEnd(Animation animation) {
						// 动画结束后打开播放资源界面，并传递资源信息
						Intent intent = new Intent(PictureStoryActivity.this,
								RecordActivity.class);
						intent.putExtra("ImgPath", files[position].getAbsolutePath());
						intent.putExtra("Position", position);
						startActivityForResult(intent, 0);
					}

					@Override
					public void onAnimationRepeat(Animation animation) {

					}
				});
			}
		});
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
	 * 下载ZIP
	 *
	 * @param url zip服务器地址
	 * @param out 保存地址
	 */
	private void doDownloadWork(String url, String out) {
		// 异步下载
		DownLoaderTask task = new DownLoaderTask(url, out, this);
		task.execute();
	}

	/**
	 * 解压ZIP
	 *
	 * @param url zip本地地址
	 * @param out 解压后的文件地址
	 */
	public void doZipExtractorWork(String url, String out) {
		File file = new File(url);
		if (!file.exists()) {
			Toast.makeText(this, "压缩文件不存在！", Toast.LENGTH_SHORT).show();
			return;
		}
		// 异步解压
		ZipExtractorTask task = new ZipExtractorTask(url, out, PictureStoryActivity.this);
		task.execute();
	}

	/**
	 * GridView适配器
	 */
	class MyGridViewAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return files.length;
		}

		@Override
		public Object getItem(int position) {
			return files[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, final ViewGroup parent) {

			File file = files[position];
			ViewHolder holder = new ViewHolder();
			if (convertView == null) {
				convertView = LayoutInflater.from(PictureStoryActivity.this).inflate(R.layout
						.gridview1_item, null);
				holder.iv = (ImageView) convertView.findViewById(R.id.iv_gridView1_item);
				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
			holder.iv.setImageBitmap(bitmap);

			return convertView;
		}

		/**
		 * 自定义ViewHolder
		 */
		class ViewHolder {
			ImageView iv;
		}
	}
}
