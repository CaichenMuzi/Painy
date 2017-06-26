package com.gogostar.enstory;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.gogostar.PronunceEvaluation.PronunceEvaluation;
import com.gogostar.gogomedia.GogoMp4Builder;
import com.gogostar.gogomedia.IGogoBuilder;
import com.gogostar.gogomedia.Wav2Mp3Builder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.view.Gravity.CENTER;
import static android.widget.Toast.makeText;
import static com.gogostar.enstory.R.drawable.record;

/**
 * Created by Administrator on 2017/3/6.
 */

public class PictureStoryActivity extends BaseActivity implements View.OnTouchListener, View
		.OnClickListener, com.gogostar.PronunceEvaluation.IGogoPronunceEvaluation {

	// watch按钮
	private ImageView img_watch;
	// speak按钮
	private ImageView img_speak;
	// 播放视频的背景图片
	private ImageView replay_img;
	// 底部视图栏
	private RelativeLayout bottom_relative;
	// 下一页
	private ImageView img_speak_next;
	// 上一页
	private ImageView img_speak_pre;

	private ImageView img_speak_img;
	// 播放录音按钮
	private ImageView img_speak_play;
	// 录音按钮
	private ImageView img_speak_record;
	// 返回键
	private ImageView img_back;

	private ImageView img_star1, img_star2, img_star3, img_star4, img_star5;

	private ImageView img_make_video, img_play_video;
	private LinearLayout video_relative_makeVideo;
	// watch视图
	private RelativeLayout watch_relative;
	// speak视图
	private RelativeLayout speak_relative;

	private FullVideoView video_VideoView;

	// 屏幕宽度
	private int width;
	// 屏幕高度
	private int height;

	private static final int handKey = 123;
	private boolean isGo = false;

	private MediaPlayer mediaPlayer = null;

	private List<File> audioList = new ArrayList<File>();
	private ArrayList<String> imageList = new ArrayList<String>();
	private List<String> stringList = new ArrayList<String>();
	private List<String> recordList = new ArrayList<String>();
	private ArrayList<String> mp3List = new ArrayList<String>();

	private int audioNum, nextNUM, preNUM;

	// 手指按下的点为(x1, y1)手指离开屏幕的点为(x2, y2)
	private float x1 = 0, x2 = 0, y1 = 0, y2 = 0;

	private Context context;
	// 视频路径
	private String Path;

	// 录音文件地址
	private String recordFile;

	private PictureInfo pictureInfo;

	private Bundle bundle;

	private MyUtil myUtil = new MyUtil();

	private PronunceEvaluation pe;
	private GogoMp4Builder gogoMp4Builder;
	private Wav2Mp3Builder wav2Mp3Builder;

	private AnimationDrawable animationDrawable;

	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 讯飞语音评分
		pe = new PronunceEvaluation();
		pe.initialize(PictureStoryActivity.this, PictureStoryActivity.this);

		setContentView(R.layout.activity_video_list);
		context = this;
		Path = context.getCacheDir().toString() + "/gogostar/enstory/myCourse";

		findViews();
		init();

		// 下载资源zip
		File file = new File(Path + File.separator + pictureInfo.getTitle().toLowerCase().replace
				(" ", ""));
		if (!file.exists()) {
			doDownloadWork(pictureInfo.getVideo_src(), Path);
		} else {
			// 资源已存在，播放视频
			startVideo();
		}
	}

	/**
	 * 查找控件
	 */
	private void findViews() {

		img_watch = (ImageView) findViewById(R.id.video_img_watch);
		img_speak = (ImageView) findViewById(R.id.video_img_speak);
		img_back = (ImageView) findViewById(R.id.video_img_back);
		replay_img = (ImageView) findViewById(R.id.video_img);

		video_VideoView = (FullVideoView) findViewById(R.id.video_VideoView);

		// 获取屏幕宽度
		width = getWindowManager().getDefaultDisplay().getWidth();
		// 获取屏幕高度
		height = getWindowManager().getDefaultDisplay().getHeight();

		watch_relative = (RelativeLayout) findViewById(R.id.video_watch_relative);
		speak_relative = (RelativeLayout) findViewById(R.id.video_speak_relative);
		bottom_relative = (RelativeLayout) findViewById(R.id.video_relative_bottom);
		img_speak_next = (ImageView) findViewById(R.id.video_speak_next);
		img_speak_pre = (ImageView) findViewById(R.id.video_speak_pre);
		img_speak_img = (ImageView) findViewById(R.id.video_speak_img);
		img_speak_play = (ImageView) findViewById(R.id.video_speak_playRecord);
		img_speak_record = (ImageView) findViewById(R.id.video_speak_record);

		img_star1 = (ImageView) findViewById(R.id.video_img_star1);
		img_star2 = (ImageView) findViewById(R.id.video_img_star2);
		img_star3 = (ImageView) findViewById(R.id.video_img_star3);
		img_star4 = (ImageView) findViewById(R.id.video_img_star4);
		img_star5 = (ImageView) findViewById(R.id.video_img_star5);

		// 视频播放时屏幕常亮
		video_VideoView.setKeepScreenOn(true);

		bundle = (Bundle) getIntent().getExtras().get("bundle");
		pictureInfo = (PictureInfo) bundle.getParcelable("PictureInfo");

		mediaPlayer = new MediaPlayer();

		video_relative_makeVideo = (LinearLayout) findViewById(R.id.video_relative_makeVideo);
		img_make_video = (ImageView) findViewById(R.id.img_make_video);
		img_play_video = (ImageView) findViewById(R.id.img_play_video);

		gogoMp4Builder = new GogoMp4Builder();
		wav2Mp3Builder = new Wav2Mp3Builder();
	}

	/**
	 * 加载UI
	 */
	private void init() {

		File file = new File(Path);
		if (!file.exists()) {
			file.mkdirs();
		}

		img_speak_record.setBackgroundResource(record);

		replay_img.setOnClickListener(this);
		img_watch.setOnClickListener(this);
		img_speak.setOnClickListener(this);
		img_speak_play.setOnClickListener(this);
		img_speak_next.setOnTouchListener(this);
		img_speak_pre.setOnTouchListener(this);
		img_speak_record.setOnClickListener(this);
		img_back.setOnTouchListener(this);
		img_make_video.setOnTouchListener(this);
		img_play_video.setOnTouchListener(this);
		video_relative_makeVideo.setVisibility(View.GONE);

		// 视频播放结束
		video_VideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				replay_img.setVisibility(View.VISIBLE);
			}
		});


		progressDialog = new ProgressDialog(PictureStoryActivity.this);
		progressDialog.setTitle("加载中...");
		progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		progressDialog.setIndeterminate(false);
		progressDialog.setCanceledOnTouchOutside(false);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {

		// speak视图显示时，滑动屏幕，改变显示的图片并播放相应的音频
		if (speak_relative.getVisibility() == View.VISIBLE) {
			if (event.getAction() == MotionEvent.ACTION_DOWN) {
				x1 = event.getX();
				y1 = event.getY();
			}
			if (event.getAction() == MotionEvent.ACTION_UP) {
				x2 = event.getX();
				y2 = event.getY();
				if (x2 - x1 > 20) {
					if (preNUM == audioNum - 1) {
						setAudioImg(preNUM);
					}
					if (preNUM < audioNum - 1 && preNUM >= 0) {
						setAudioImg(preNUM);
						img_speak_next.setVisibility(View.VISIBLE);
					}
					if (preNUM == 0) {
						img_speak_pre.setVisibility(View.INVISIBLE);
					}
					nextNUM = preNUM;
					preNUM--;
				} else if (x1 - x2 > 20) {
					preNUM = nextNUM;
					nextNUM++;
					if (nextNUM < audioNum && nextNUM > 0) {
						setAudioImg(nextNUM);
						img_speak_pre.setVisibility(View.VISIBLE);
					}
					if (nextNUM == audioNum - 1) {
						img_speak_next.setVisibility(View.INVISIBLE);
					}
				}
			}
			setImgStar();
		}
		return super.onTouchEvent(event);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.video_img:    // 重播视频按钮事件
				myUtil.playSound(PictureStoryActivity.this, R.raw.click);
				replay_img.setVisibility(View.GONE);
				video_VideoView.setVideoPath(pictureInfo.getVideo_path());
				video_VideoView.requestFocus();
				video_VideoView.start();
				break;
			case R.id.video_img_watch:  // watch按钮点击事件
				myUtil.playSound(PictureStoryActivity.this, R.raw.click);
				video_relative_makeVideo.setVisibility(View.GONE);
				mediaPlayer.stop();
				if (!video_VideoView.isPlaying()) {
					bottom_relative.setVisibility(View.GONE);
					img_watch.setImageResource(R.drawable.watch_1);
					img_speak.setImageResource(R.drawable.speak_0);
					watch_relative.setVisibility(View.VISIBLE);
					speak_relative.setVisibility(View.GONE);
					if (pictureInfo.getVideo_path().equals("")) {
						finish();
					} else {
						video_VideoView.setVideoPath(pictureInfo.getVideo_path());
						video_VideoView.requestFocus();
						video_VideoView.start();
					}
				}
				break;
			case R.id.video_img_speak:  // speak按钮点击事件
				myUtil.playSound(PictureStoryActivity.this, R.raw.click);
				video_relative_makeVideo.setVisibility(View.VISIBLE);
				video_VideoView.stopPlayback();
				if (!mediaPlayer.isPlaying()) {
					bottom_relative.setVisibility(View.VISIBLE);
					img_watch.setImageResource(R.drawable.watch_0);
					img_speak.setImageResource(R.drawable.speak_1);
					watch_relative.setVisibility(View.GONE);
					replay_img.setVisibility(View.GONE);
					speak_relative.setVisibility(View.VISIBLE);
					img_speak_pre.setVisibility(View.INVISIBLE);
					setAudioImg(0);
				}
				break;
			case R.id.video_speak_playRecord:   // 播放录音按钮点击事件
				myUtil.playSound(PictureStoryActivity.this, R.raw.click);
				recordFile = Path + "/record" + File.separator +
						"record_" + nextNUM + ".wav";
				File file = new File(recordFile);
				Log.d("朗读句子", recordFile);
				if (file.exists()) {
					myUtil.initMediaPlayer(recordFile);
				} else {
					Toast toast = makeText(PictureStoryActivity.this, "请先朗读句子...", Toast
							.LENGTH_SHORT);
					toast.setGravity(CENTER, 0, 0);
					toast.show();
				}
				break;
			case R.id.video_speak_record:   // 录音按钮点击事件
				myUtil.playSound(PictureStoryActivity.this, R.raw.click);
				img_speak_record.setBackgroundResource(R.drawable.animated_rocket);
				animationDrawable = (AnimationDrawable) img_speak_record.getBackground();
				animationDrawable.start();

				setImgStar();// 清空评分
				Toast toast = makeText(PictureStoryActivity.this, "请说话...", Toast
						.LENGTH_SHORT);
				toast.setGravity(CENTER, 0, 0);
				toast.show();
				recordFile = Path + "/record" + File.separator + "record_" + nextNUM + ".wav";
				pe.start(stringList.get(nextNUM), recordFile);
				recordList.add(nextNUM, recordFile);
				break;
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		switch (v.getId()) {
			case R.id.video_img_back:   // 返回按钮点击事件
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(PictureStoryActivity.this, R.raw.click);
						img_back.setImageResource(R.drawable.fanhui_2);
						break;
					case MotionEvent.ACTION_UP:
						img_back.setImageResource(R.drawable.fanhui_1);
						setResult(RESULT_OK);
						finish();
					case MotionEvent.ACTION_CANCEL:
						break;
				}
				break;
			case R.id.video_speak_next:    // 下一页按钮点击事件
				setImgStar();   // 清空评分
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						img_speak_next.setImageResource(R.drawable.next_2);
						myUtil.playSound(PictureStoryActivity.this, R.raw.click);
						break;
					case MotionEvent.ACTION_UP:
						img_speak_next.setImageResource(R.drawable.next_1);
						preNUM = nextNUM;
						nextNUM++;
						if (nextNUM < audioNum && nextNUM > 0) {
							setAudioImg(nextNUM);
							img_speak_pre.setVisibility(View.VISIBLE);
						}
						if (nextNUM == audioNum - 1) {
							img_speak_next.setVisibility(View.INVISIBLE);
						}
						break;
					case MotionEvent.ACTION_CANCEL:
						break;
				}
				break;
			case R.id.video_speak_pre:  // 下一页按钮点击事件
				setImgStar();   // 清空评分
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						img_speak_pre.setImageResource(R.drawable.pre_2);
						myUtil.playSound(PictureStoryActivity.this, R.raw.click);
						break;
					case MotionEvent.ACTION_UP:
						img_speak_pre.setImageResource(R.drawable.pre_1);
						if (preNUM == audioNum - 1) {
							setAudioImg(preNUM);
						}
						if (preNUM < audioNum - 1 && preNUM >= 0) {
							setAudioImg(preNUM);
							img_speak_next.setVisibility(View.VISIBLE);
						}
						if (preNUM == 0) {
							img_speak_pre.setVisibility(View.INVISIBLE);
						}
						nextNUM = preNUM;
						preNUM--;
						break;
					case MotionEvent.ACTION_CANCEL:
						break;
				}
				break;
			case R.id.img_make_video:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						img_make_video.setImageResource(R.drawable.hcsp_1);
						myUtil.playSound(PictureStoryActivity.this, R.raw.click);
						break;
					case MotionEvent.ACTION_UP:
						img_make_video.setImageResource(R.drawable.hcsp_0);
						File file = new File(Path + "/record");
						File[] files = file.listFiles();
						int count = files.length;
						if (count == imageList.size()) {
							Log.d("MakeVideo", "MakeVideo");
							try {
								progressDialog.show();
								new Thread(runnable).start();
							} catch (Exception e) {
								e.printStackTrace();
								Log.d("MakeVideo", e.toString());
							}
						} else {
							Toast toast = Toast.makeText(this, "缺少语音文件", Toast.LENGTH_SHORT);
							toast.setGravity(CENTER, 0, 0);
							toast.show();
						}
						break;
				}
				break;
			case R.id.img_play_video:
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						img_play_video.setImageResource(R.drawable.bfsp_1);
						myUtil.playSound(PictureStoryActivity.this, R.raw.click);
						break;
					case MotionEvent.ACTION_UP:
						img_play_video.setImageResource(R.drawable.bfsp_0);
						mediaPlayer.stop();
						if (!video_VideoView.isPlaying()) {
							String str = Environment.getExternalStorageDirectory() + File
									.separator + "/gogostar/Video/" + pictureInfo.getTitle()
									.replace(" ", "") + ".mp4";
							if (new File(str).exists()) {
								bottom_relative.setVisibility(View.GONE);
								video_relative_makeVideo.setVisibility(View.GONE);
								img_watch.setImageResource(R.drawable.watch_1);
								img_speak.setImageResource(R.drawable.speak_0);
								watch_relative.setVisibility(View.VISIBLE);
								speak_relative.setVisibility(View.GONE);

								video_VideoView.setVideoPath(str);
								video_VideoView.requestFocus();
								video_VideoView.start();
							} else {
								Toast toast = Toast.makeText(this, "请先合成视频", Toast.LENGTH_SHORT);
								toast.setGravity(CENTER, 0, 0);
								toast.show();
							}
						}
						break;
				}
				break;
		}
		return true;
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
			setResult(RESULT_OK);
			finish();
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 播放视频子线程
	 */
	public void startVideo() {
		new Thread(runnable2).start();
	}

	private Runnable runnable2 = new Runnable() {
		@Override
		public void run() {
			doSearch(Path);// 搜索文件
			Message message = handler.obtainMessage();
			message.what = handKey;
			handler.sendMessage(message);// 发送handler信息
		}
	};

	/**
	 * 搜索该路径下的所有文件
	 *
	 * @param path 资源路径
	 */
	private void doSearch(String path) {
		File file = new File(path);

		if (file.exists()) {
			if (file.isDirectory()) {
				File[] fileArray = file.listFiles();
				// 遍历循环
				for (File f : fileArray) {
					if (f.isDirectory()) {//判断是否文件夹
						doSearch(f.getPath());
					} else {
						// 如果文件的后缀名是mp4或3gp
						if (f.getName().endsWith("mp4") || f.getName().endsWith("3gp")) {
							String string1 = f.getName().substring(0, f.getName().lastIndexOf("" +
									"."));
							// 如果文件的名字等于资源的名字，设置资源的视频地址为该文件地址
							if (string1.toLowerCase().equals(pictureInfo.getTitle().toLowerCase()
									.replace(" ", ""))) {
								pictureInfo.setVideo_path(f.toString());
							}
						} else if (f.getName().endsWith("mp3")) { // 如果文件的后缀名是mp3
							String string2 = f.getName().substring(0, f.getName().lastIndexOf
									("_"));
							// 如果文件的名字等于资源的名字，将该文件添加到audioList集合中
							if (string2.toLowerCase().equals(pictureInfo.getTitle().toLowerCase()
									.replace(" ", ""))) {
								audioNum++;
								audioList.add(f);
							}
						} else if (f.getName().endsWith("png")) {   // 如果文件的后缀名是png
							String string3 = f.getName().substring(0, f.getName().lastIndexOf
									("_"));
							// 如果文件的名字等于资源的名字，将该文件添加到imageList集合中
							if (string3.toLowerCase().equals(pictureInfo.getTitle().toLowerCase()
									.replace(" ", ""))) {
								imageList.add(f.getPath());
							}
						} else if (f.getName().endsWith("txt")) {   // 如果文件的后缀名是txt
							String string4 = f.getName().substring(0, f.getName().lastIndexOf
									("_"));
							// 如果文件的名字等于资源的名字，将该文件添加到stringList集合中
							if (string4.toLowerCase().equals(pictureInfo.getTitle().toLowerCase()
									.replace(" ", ""))) {
								String string = myUtil.readTxtFile(f);
								stringList.add(string);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * 设置speak页面显示的image
	 *
	 * @param i 页码
	 */
	private void setAudioImg(int i) {
		nextNUM = i;
		Bitmap bitmap = BitmapFactory.decodeFile(imageList.get(i).toString());
		bitmap = Bitmap.createScaledBitmap(bitmap, width, height, true);
		img_speak_img.setImageBitmap(bitmap);
		myUtil.initMediaPlayer(audioList.get(i).toString());
	}

	/**
	 * 播放视频
	 */
	private Handler handler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
				case handKey:
					video_VideoView.setVideoPath(pictureInfo.getVideo_path());
					video_VideoView.requestFocus();
					video_VideoView.start();
					break;
				case 1:
					progressDialog.show();
					progressDialog.setProgress(0);
					//
					new Thread(runnable3).start();
					break;
			}
		}
	};

	/**
	 * 获取评分
	 *
	 * @param d 讯飞评huju
	 */
	private void getScore(double d) {

		int i = (int) Math.floor(d + 0.5);// 对d进行取值
		if (i == 1 || i == 0) {
			img_star1.setImageResource(R.drawable.star_1);
		} else if (i == 2) {
			img_star1.setImageResource(R.drawable.star_1);
			img_star2.setImageResource(R.drawable.star_1);
		} else if (i == 3) {
			img_star1.setImageResource(R.drawable.star_1);
			img_star2.setImageResource(R.drawable.star_1);
			img_star3.setImageResource(R.drawable.star_1);
		} else if (i == 4) {
			img_star1.setImageResource(R.drawable.star_1);
			img_star2.setImageResource(R.drawable.star_1);
			img_star3.setImageResource(R.drawable.star_1);
			img_star4.setImageResource(R.drawable.star_1);
		} else if (i == 5) {
			img_star1.setImageResource(R.drawable.star_1);
			img_star2.setImageResource(R.drawable.star_1);
			img_star3.setImageResource(R.drawable.star_1);
			img_star4.setImageResource(R.drawable.star_1);
			img_star5.setImageResource(R.drawable.star_1);
		}
	}

	/**
	 * 隐藏评分
	 */
	private void setImgStar() {
		img_star1.setImageResource(R.drawable.star_0);
		img_star2.setImageResource(R.drawable.star_0);
		img_star3.setImageResource(R.drawable.star_0);
		img_star4.setImageResource(R.drawable.star_0);
		img_star5.setImageResource(R.drawable.star_0);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayer.stop();
		video_VideoView.stopPlayback();
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
			makeText(this, "压缩文件不存在！", Toast.LENGTH_SHORT).show();
			return;
		}
		// 异步解压
		ZipExtractorTask task = new ZipExtractorTask(url, out, PictureStoryActivity.this);
		task.execute();
	}

	@Override
	public void onError(String error) {
		Log.e("PronunceEvaluation", error);
	}

	@Override
	public void onResult(String score) {
		// 麦克风按钮停止动画
		animationDrawable.stop();
		img_speak_record.setBackgroundResource(record);
		// 从返回的字符串中解析出分数
		String s = myUtil.parseXMLWithPull(score);
		// 根据分数显示星星的个数
		getScore(Double.valueOf(s));
	}

	@Override
	public void onStartRecord() {
	}

	@Override
	public void onStopRecord() {
	}

	/**
	 * 将wav格式的录音转换成mp3格式
	 */
	Runnable runnable = new Runnable() {
		@Override
		public void run() {
			// 录音文件集合
			File file1 = new File(Path + "/record");
			final File[] files = file1.listFiles();

			// 循环录音文件
			for (int i = 0; i < files.length; i++) {
				final int a = i;
				final String str = Environment.getExternalStorageDirectory() +
						"/gogostar/Mp3/" + a + ".mp3";
				File file = new File(str);
				if (!file.getParentFile().exists()) {
					file.getParentFile().mkdirs();
				}
				try {
					String strFile = String.valueOf(files[i]);
					// 调用Wav2Mp3Builder类的Build方法转换MP3
					wav2Mp3Builder.Build(strFile, str, new IGogoBuilder() {
						@Override
						public void onPercent(int var1) {
							// 进度
							int progress = (int) ((100.0 / files.length) * a + var1 / files
									.length + 0.5);
							progressDialog.setProgress(progress);
						}

						@Override
						public void onFinished() {
							// 完成后将MP3地址添加到mp3List
							mp3List.add(a, str);
							isGo = true;
						}

						@Override
						public void onError(Exception var1) {
							var1.printStackTrace();
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}
				// 当isGo为true时break，继续下一次循环
				while (true) {
					if (isGo) {
						isGo = false;
						break;
					}
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
			progressDialog.dismiss();
			handler.sendEmptyMessage(1);
		}
	};

	/**
	 * 将图片集合和MP3集合合成视频
	 */
	Runnable runnable3 = new Runnable() {
		@Override
		public void run() {
			// 合成的视频地址
			String videoPath = Environment.getExternalStorageDirectory() + File
					.separator + "/gogostar/Video/" + pictureInfo.getTitle()
					.replace(" ", "") + ".mp4";
			File f = new File(videoPath);
			if (!f.getParentFile().exists()) {
				f.getParentFile().mkdirs();
			}
			// 调用GogoMp4Builder类的Bulid方法合成视频
			gogoMp4Builder.Build(320, 240, imageList, mp3List, videoPath, new
					IGogoBuilder() {

						@Override
						public void onPercent(int i) {
							// 进度条
							progressDialog.setProgress(i);
						}

						@Override
						public void onFinished() {
							// 完成后进度条消失
							progressDialog.dismiss();
							Toast t = makeText(PictureStoryActivity.this, "视频合成完成！", Toast
									.LENGTH_SHORT);
							t.setGravity(CENTER, 0, 0);
							t.show();
						}

						@Override
						public void onError(Exception e) {
							e.printStackTrace();
						}
					});
		}
	};
}