package com.gogostar.gogostroydiy;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import java.io.File;

/**
 * Created by Administrator on 2017/3/3.
 */

public abstract class BaseActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 添加活动
		ActivityCollector.addActivity(this);
		if (ContextCompat.checkSelfPermission(BaseActivity.this, Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED) {
			ActivityCompat.requestPermissions(BaseActivity.this, new String[]{Manifest.permission
					.RECORD_AUDIO, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission
					.WRITE_EXTERNAL_STORAGE}, 1);
		}
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
			grantResults) {
		switch (requestCode) {
			case 1:
				if (grantResults.length > 0 && grantResults[0] == PackageManager
						.PERMISSION_GRANTED) {
					File file = new File(Environment.getExternalStorageDirectory() +
							"/gogostar/Video");
					if (!file.exists()) {
						file.mkdirs();
					}
				} else {
					finish();
				}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		// 移除活动
		ActivityCollector.removeActivity(this);
	}
}
