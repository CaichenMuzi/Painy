package com.gogostar.enstory;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

/**
 * Created by Administrator on 2017/4/11.
 * <p>
 * 自定义背景音乐Service
 */

public class MusicServer extends Service {

	private MediaPlayer mediaPlayer;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);

		// 设置播放器
		if (mediaPlayer == null) {
			mediaPlayer = MediaPlayer.create(this, R.raw.back);
			mediaPlayer.setLooping(true);
			mediaPlayer.start();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayer.stop();
	}
}
