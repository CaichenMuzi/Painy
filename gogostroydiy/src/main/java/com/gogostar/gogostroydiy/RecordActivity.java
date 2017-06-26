package com.gogostar.gogostroydiy;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2017/4/27.
 */

public class RecordActivity extends BaseActivity {

	private ImageView mPictureImg, mBackImg, mRecordImg, mPlayImg, mStopImg;
	private String mImgPath;
	private File mImgFile;
	private int mIntFile;

	// 录音文件地址
	private String recordFile;

	private MyUtil myUtil = new MyUtil();

	private AnimationDrawable animationDrawable;
	private MediaRecorder mediaRecorder;
	private MediaPlayer mediaPlayer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_record);

		mIntFile = getIntent().getIntExtra("Position", -1);
		mImgPath = getIntent().getStringExtra("ImgPath");
		mImgFile = new File(mImgPath);
		initData();
	}

	private void initData() {
		mBackImg = (ImageView) findViewById(R.id.iv_record_back);
		mPictureImg = (ImageView) findViewById(R.id.iv_record_picture);
		mRecordImg = (ImageView) findViewById(R.id.iv_record_start);
		mPlayImg = (ImageView) findViewById(R.id.iv_record_play);
		mStopImg = (ImageView) findViewById(R.id.iv_record_stop);

		Bitmap bitmap = BitmapFactory.decodeFile(mImgPath);
		mPictureImg.setImageBitmap(bitmap);
		mRecordImg.setBackgroundResource(R.drawable.record);
		mPlayImg.setBackgroundResource(R.drawable.play_record_3);

		mBackImg.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						myUtil.playSound(RecordActivity.this, R.raw.click);
						mBackImg.setImageResource(R.drawable.fanhui_2);
						break;
					case MotionEvent.ACTION_UP:
						mBackImg.setImageResource(R.drawable.fanhui_1);
						setResult(0);
						finish();
				}
				return true;
			}
		});

		mRecordImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myUtil.playSound(RecordActivity.this, R.raw.click);
				mRecordImg.setBackgroundResource(R.drawable.animated_rocket);
				animationDrawable = (AnimationDrawable) mRecordImg.getBackground();
				animationDrawable.start();

				Toast toast = Toast.makeText(RecordActivity.this, "请说话...", Toast
						.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER, 0, 0);
				toast.show();
				recordFile = mImgFile.getParentFile() + "/record" + File.separator + "record_" +
						mIntFile + ".amr";
				startRecord(recordFile);
			}
		});

		mStopImg.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
					case MotionEvent.ACTION_DOWN:
						mStopImg.setImageResource(R.drawable.tzly_1);
						myUtil.playSound(RecordActivity.this, R.raw.click);
						break;
					case MotionEvent.ACTION_UP:
						stopRecord();
						mRecordImg.setBackgroundResource(R.drawable.record);
						mStopImg.setImageResource(R.drawable.tzly_0);
						break;
				}
				return true;
			}
		});

		mPlayImg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				myUtil.playSound(RecordActivity.this, R.raw.click);
				mPlayImg.setBackgroundResource(R.drawable.animated_play);
				animationDrawable = (AnimationDrawable) mPlayImg.getBackground();
				animationDrawable.start();
				recordFile = mImgFile.getParentFile() + "/record" + File.separator + "record_" +
						mIntFile + ".amr";
				File file = new File(recordFile);
				if (file.exists()) {
					playRecord(recordFile);
				} else {
					Toast toast = Toast.makeText(RecordActivity.this, "请先朗读句子...", Toast
							.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				}
			}
		});

		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				mPlayImg.setBackgroundResource(R.drawable.play_record_3);
			}
		});
	}

	private void startRecord(String recordFile) {
		try {
			mediaRecorder = new MediaRecorder();
			mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
			mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
			mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
			mediaRecorder.setOutputFile(recordFile);
			mediaRecorder.prepare();
			mediaRecorder.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void stopRecord() {
		if (recordFile != null && new File(recordFile).exists()) {
			mediaRecorder.stop();
			mediaRecorder.release();
		}
	}

	private void playRecord(String recordFile) {
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.reset();
			mediaPlayer.setDataSource(recordFile);
			mediaPlayer.prepare();
			mediaPlayer.start();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
